package org.envirocar.qad.analyzer;

import org.envirocar.qad.axis.Axis;
import org.envirocar.qad.axis.Segment;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.result.AnalysisResult;
import org.envirocar.qad.utils.SpliteratorAdapter;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class AxisAnalyzer implements Analyzer {
    private static final Logger LOG = LoggerFactory.getLogger(AxisAnalyzer.class);
    private final AnalyzerFactory analyzerFactory;
    private final Axis axis;
    private final Track track;

    public AxisAnalyzer(AnalyzerFactory analyzerFactory, Axis axis, Track track) {
        this.analyzerFactory = Objects.requireNonNull(analyzerFactory);
        this.axis = Objects.requireNonNull(axis);
        this.track = Objects.requireNonNull(track);
    }

    @Override
    public boolean isApplicable() {
        return this.axis.getEnvelope().intersects(this.track.getEnvelope());
    }

    @Override
    public Stream<AnalysisResult> analyze() {
        LOG.debug("Analyzing axis {} for track {}", this.axis.getId(), this.track.getId());
        return this.track.subset(this.axis.getEnvelope()).flatMap(track -> {
            LOG.debug("Analyzing axis {} for subset of track {}", this.axis.getId(), track);
            Stream<MatchCandidate> candidates = this.axis.findIntersectingSegments(track.getGeometry()).stream()
                                                         .flatMap(segment -> getCandidates(segment, track));
            return createResults(track, candidates);
        });
    }

    private Stream<MatchCandidate> getCandidates(Segment segment, Track track) {
        return StreamSupport.stream(new SpliteratorAdapter<MatchCandidate>() {
            private final int size = track.size();
            private int idx;
            private int start = -1;

            @Override
            public boolean tryAdvance(Consumer<? super MatchCandidate> action) {
                while (this.idx < this.size) {
                    Point geom = track.getMeasurement(this.idx).getGeometry();
                    if (segment.getBuffer().contains(geom) && isNearestSegment(geom)) {
                        if (this.start < 0) {
                            this.start = this.idx;
                        }
                    } else if (this.start >= 0 && checkCandidate(action)) {
                        return true;
                    }
                    this.idx++;
                }
                return this.start >= 0 && checkCandidate(action);
            }

            private boolean checkCandidate(Consumer<? super MatchCandidate> action) {
                // the last index is no longer in the segment buffer
                int end = this.idx - 1;
                int start = this.start;
                this.start = -1;
                MatchCandidate candidate = AxisAnalyzer.this.analyzerFactory.create(segment, track, start, end);
                if (start == end) {
                    LOG.warn("Found match with single measurement for track {} on segment {}: [{},{}]",
                             track.getId(), segment.getId(), start, end);
                    action.accept(candidate);
                    this.idx++;
                    return true;
                    // return false;
                }

                if (candidate.checkOrientation()) {
                    if (candidate.checkLength()) {
                        LOG.debug("Found match for track {} on segment {}: [{},{}]",
                                  track.getId(), segment.getId(), start, end);
                        action.accept(candidate);
                        this.idx++;
                        return true;
                    } else {
                        LOG.debug("Rejecting match for track {} on segment {}: [{},{}]: length deviation",
                                  track.getId(), segment.getId(), start, end);
                    }
                } else {
                    LOG.debug("Rejecting match for track {} on segment {}: [{},{}]: orientation deviation",
                              track.getId(), segment.getId(), start, end);
                }

                return false;
            }

            private boolean isNearestSegment(Point geom) {
                double distanceToThisSegment = geom.distance(segment.getGeometry());
                Optional<Double> next = segment.next().map(Segment::getGeometry).map(geom::distance);
                Optional<Double> prev = segment.prev().map(Segment::getGeometry).map(geom::distance);
                return (!next.isPresent() || distanceToThisSegment <= next.get()) &&
                       (!prev.isPresent() || distanceToThisSegment <= prev.get());
            }
        }, false);
    }

    private Stream<AnalysisResult> createResults(Track track, Stream<MatchCandidate> segmentResults) {
        return findStreaks(segmentResults).map(results -> createAnalysisResults(track, results))
                                          .filter(Optional::isPresent)
                                          .map(Optional::get);
    }

    private Stream<Deque<MatchCandidate>> findStreaks(Stream<MatchCandidate> results) {
        Map<Segment, Deque<MatchCandidate>> bySegment
                = results.collect(groupingBy(MatchCandidate::getSegment, TreeMap::new,
                                             collectingAndThen(toCollection(TreeSet::new), LinkedList::new)));
        return StreamSupport.stream(Spliterators.spliterator(new Iterator<Deque<MatchCandidate>>() {
            @Override
            public boolean hasNext() {
                return !bySegment.isEmpty();
            }

            @Override
            public Deque<MatchCandidate> next() {
                if (bySegment.isEmpty()) {
                    throw new NoSuchElementException();
                }
                LinkedList<MatchCandidate> streak = new LinkedList<>();
                Iterator<Deque<MatchCandidate>> iter = bySegment.values().iterator();
                Deque<MatchCandidate> candidates = iter.next();
                MatchCandidate candidate = candidates.pop();

                if (candidates.isEmpty()) {
                    iter.remove();
                }
                streak.addLast(candidate);
                Deque<MatchCandidate> nextCandidates;
                while ((nextCandidates = getNextCandidates(bySegment, streak)) != null) {
                    MatchCandidate nextCandidate = nextCandidates.pop();
                    streak.addLast(nextCandidate);
                    if (nextCandidates.isEmpty()) {
                        bySegment.remove(nextCandidate.getSegment());
                    }
                }
                return streak;
            }
        }, Long.MAX_VALUE, Spliterator.ORDERED & Spliterator.NONNULL), false);
    }

    private Deque<MatchCandidate> getNextCandidates(Map<Segment, ? extends Deque<MatchCandidate>> bySegment,
                                                    Deque<? extends MatchCandidate> streak) {
        return streak.getLast().getSegment().next().map(bySegment::get)
                     .filter(nextCandidates -> streak.getLast().getEnd() + 1 == nextCandidates.getFirst().getStart())
                     .orElse(null);
    }

    private Optional<AnalysisResult> createAnalysisResults(Track track, Deque<MatchCandidate> results) {

        if (!results.isEmpty() && results.getFirst().getSize() == 1) {
            results.removeFirst();

        }
        if (!results.isEmpty() && results.getLast().getSize() == 1) {
            results.removeLast();
        }
        if (results.isEmpty()) {
            return Optional.empty();
        }

        AnalysisResult result = new AnalysisResult();
        result.setTrack(track.getId());
        result.setAxis(this.axis.getId());
        result.setModel(this.axis.getModelId());
        result.setFuelType(track.getFuelType());
        result.setStart(track.getTime(results.getFirst().getStart()));
        result.setEnd(track.getTime(results.getLast().getEnd()));
        result.setSegments(results.stream().map(MatchCandidate::toSegmentResult).collect(toList()));
        return Optional.of(result);
    }
}
