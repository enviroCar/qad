package org.envirocar.qad;

import org.envirocar.qad.mapmatching.MapMatcher;
import org.envirocar.qad.mapmatching.MapMatchingException;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.axis.Axis;
import org.envirocar.qad.model.axis.AxisModel;
import org.envirocar.qad.model.axis.Segment;
import org.envirocar.qad.model.result.AnalysisResult;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class TrackAnalysisService {
    private static final Logger LOG = LoggerFactory.getLogger(TrackAnalysisService.class);
    private final AxisModelRepository axisModels;
    private final MapMatcher mapMatcher;
    private final TrackParser trackParser;
    private final TrackDensifier densifier;
    private final GeometryFactory geometryFactory;
    private final double maxAngleDeviation = 180.0d;
    private final double maxLengthDeviation = 0.20d;

    @Autowired
    public TrackAnalysisService(AxisModelRepository axisModels,
                                MapMatcher mapMatcher,
                                TrackParser trackParser,
                                TrackDensifier densifier,
                                GeometryFactory geometryFactory) {
        this.axisModels = Objects.requireNonNull(axisModels);
        this.mapMatcher = Objects.requireNonNull(mapMatcher);
        this.trackParser = Objects.requireNonNull(trackParser);
        this.densifier = Objects.requireNonNull(densifier);
        this.geometryFactory = Objects.requireNonNull(geometryFactory);
    }

    public void analyzeTrack(FeatureCollection featureCollection) {
        try {
            Envelope trackEnvelope = featureCollection.getEnvelope();
            if (!axisModels.getEnvelope().intersects(trackEnvelope)) {
                LOG.info("Skipping track {}, does not intersect with model.",
                         featureCollection.getProperties().path(JsonConstants.ID).textValue());
                return;
            }

            Track track = prepareTrack(featureCollection);

            List<AnalysisResult> results = analyze(track).collect(Collectors.toList());

        } catch (MapMatchingException e) {
            e.printStackTrace();
        }

    }

    @NotNull
    private Stream<AnalysisResult> analyze(Track track) {
        return axisModels.getAxisModels().stream()
                         .map(model -> new AxisModelAnalyzer(model, track))
                         .filter(AxisModelAnalyzer::isApplicable)
                         .flatMap(AxisModelAnalyzer::analyze);
    }

    private class AxisModelAnalyzer {
        private final AxisModel model;
        private final Track track;

        public AxisModelAnalyzer(AxisModel model, Track track) {
            this.model = model;
            this.track = track;
        }

        public boolean isApplicable() {
            return model.getEnvelope().intersects(track.getEnvelope());
        }

        public Stream<AnalysisResult> analyze() {
            LOG.info("Analyzing track {} in regard to model {}", track.getId(), model.getId());
            return model.getAxis().stream()
                        .map(axis -> new AxisAnalyzer(axis, track))
                        .filter(AxisAnalyzer::isApplicable)
                        .flatMap(AxisAnalyzer::analyze);
        }
    }

    private class AxisAnalyzer {
        private final Axis axis;
        private final Track track;

        public AxisAnalyzer(Axis axis, Track track) {
            this.axis = Objects.requireNonNull(axis);
            this.track = Objects.requireNonNull(track);
        }

        public boolean isApplicable() {
            return axis.getEnvelope().intersects(track.getEnvelope());
        }

        public Stream<AnalysisResult> analyze() {
            LOG.info("Analyzing axis {} for track {}", axis.getId(), track.getId());
            List<Segment> segments = axis.getSegments().stream()
                                         .filter(segment -> segment.bufferIntersects(track.getGeometry()))
                                         .collect(toList());

            for (Segment segment : segments) {
                int start = 0;
                int end = 0;
                for (int i = 0; i < track.size(); i++) {
                    Measurement current = track.getMeasurement(i);
                    if (segment.getBuffer().contains(current.getGeometry())) {
                        if (start < 0) {
                            start = i;
                        }
                    } else if (start >= 0) {
                        end = i;
                    }

                    if (start >= 0 && end >= 0) {
                        MatchCandidate candidate = new MatchCandidate(segment, track, start, end);
                        if (candidate.checkOrientation(maxAngleDeviation) &&
                            candidate.checkLength(maxLengthDeviation)) {
                            // it's a match!
                            // do something with it
                        }
                        start = end = -1;
                    }

                }
            }
            if (segments.isEmpty()) {
                return Stream.empty();
            }

            return Stream.empty();
        }
    }

    private class MatchCandidate {
        private final Segment segment;
        private final Track track;
        private final int start;
        private final int end;
        private LineString geometry;

        public MatchCandidate(Segment segment, Track track, int start, int end) {
            this.segment = segment;
            this.track = track;
            this.start = start;
            this.end = end;
        }

        public boolean checkLength(double maxDeviation) {
            LineString geometry = getGeometry(track, start, end);
            double length = geometry.getLength();
            if (Math.abs(length - segment.getLength()) / segment.getLength() <= maxDeviation) {
                return true;
            }
            LOG.debug("Length deviation to big: segment: {}, track: {}",
                      segment.getLength(), length);
            return false;
        }

        public boolean checkOrientation(double maxDeviation) {
            double orientation = getOrientation(track, start, end);
            if (Math.abs(orientation - segment.getOrientation()) <= maxDeviation) {
                return true;
            }
            LOG.debug("Orientation deviation to big: segment: {}, track: {}",
                      segment.getOrientation(), orientation);
            return false;
        }

        private LineString getGeometry(Track track, int start, int end) {
            return geometryFactory.createLineString(IntStream.rangeClosed(start, end)
                                                             .mapToObj(track::getMeasurement)
                                                             .map(Measurement::getGeometry)
                                                             .map(Point::getCoordinate)
                                                             .toArray(Coordinate[]::new));
        }

        private double getOrientation(Track track, int start, int end) {
            if (start == end) {
                return 0.0d;
            }
            Coordinate c0 = track.getMeasurement(start).getGeometry().getCoordinate();
            Coordinate c1 = track.getMeasurement(end).getGeometry().getCoordinate();
            return Math.toDegrees(new LineSegment(c0, c1).angle());
        }

    }

    private Track prepareTrack(FeatureCollection featureCollection) throws MapMatchingException {
        FeatureCollection collection = mapMatcher.mapMatch(featureCollection);
        Track track = trackParser.createTrack(collection);
        return densifier.densify(track);
    }

}
