package org.envirocar.qad.analyzer;

import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.axis.Segment;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.Values;
import org.envirocar.qad.model.result.SegmentResult;
import org.envirocar.qad.model.result.SegmentStatistics;
import org.envirocar.qad.utils.AngleUtils;
import org.envirocar.qad.utils.BigDecimals;
import org.envirocar.qad.utils.GeometryUtils;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MatchCandidate implements Comparable<MatchCandidate> {
    private static final Logger LOG = LoggerFactory.getLogger(MatchCandidate.class);
    private static final Comparator<MatchCandidate> COMPARATOR = Comparator.comparing(MatchCandidate::getSegment)
                                                                           .thenComparingInt(MatchCandidate::getStart)
                                                                           .thenComparingInt(MatchCandidate::getEnd);

    private final Segment segment;
    private final Track track;
    private final AlgorithmParameters parameters;
    private double length = -1.0d;
    private LineString geometry = null;
    private Duration duration = null;
    private int stops = -1;
    private Duration stopTime = null;
    private int start;
    private int end;
    private final double startThresholdSpeed;
    private final double endThresholdSpeed;
    private final boolean simplifiedLength;
    private final MeanType meanType = MeanType.ARITHMETIC;

    public MatchCandidate(AlgorithmParameters parameters, Segment segment, Track track, int start, int end) {
        this.segment = Objects.requireNonNull(segment);
        this.parameters = Objects.requireNonNull(parameters);
        this.track = Objects.requireNonNull(track);
        this.start = start;
        this.end = end;
        if (start > end) {
            throw new IllegalArgumentException("start >= end");
        }
        this.simplifiedLength = parameters.isSimplifyLengthCalculation();
        this.startThresholdSpeed = parameters.getStops().getStartThresholdSpeed();
        this.endThresholdSpeed = parameters.getStops().getEndThresholdSpeed();
    }

    public int getStart() {
        return start;
    }

    public int getSize() {
        return end - start + 1;
    }

    public int getEnd() {
        return end;
    }

    public Segment getSegment() {
        return segment;
    }

    public SegmentResult toSegmentResult() {
        Values meanValues = getMeanValues();
        SegmentStatistics statistics = new SegmentStatistics();
        statistics.setFuelConsumption(meanValues.getFuelConsumption().orElse(0.0d));
        statistics.setEnergyConsumption(meanValues.getEnergyConsumption().orElse(0.0d));
        statistics.setEmission(meanValues.getCarbonDioxide().orElse(0.0d));
        statistics.setSpeed(meanValues.getSpeed() / 3.6);
        statistics.setStoppedTime(getStopTime());
        statistics.setTravelTime(getDuration());
        statistics.setStops(getStops());
        return new SegmentResult(segment, statistics, track.getRealIndex(start), track.getRealIndex(end));
    }

    public boolean checkOrientation() {
        double trackHeading = getHeading();
        double segmentHeading = segment.getHeading();
        double deviation = Math.abs(AngleUtils.deviation(trackHeading, segmentHeading));
        if (deviation <= parameters.getMaxAngleDeviation()) {
            return true;
        }
        LOG.debug("Orientation deviation to big ({}) for segment {}: segment: {}, track: {}",
                  segment, deviation, segmentHeading, trackHeading);
        return false;
    }

    public boolean checkLength() {
        if (Math.abs(getLength() - getSegmentLength()) <= parameters.getLengthDifferenceToTolerate() ||
            Math.abs(1 - getLengthRatio()) <= parameters.getMaxLengthDeviation()) {
            return true;
        }
        LOG.debug("Length deviation to big for segment {}: segment: {}, track: {}",
                  segment, getSegmentLength(), getLength());
        return false;
    }

    private double getLengthRatio() {
        return getLength() / getSegmentLength();
    }

    private double getSegmentLength() {
        return simplifiedLength ? GeometryUtils.simplifiedLength(segment.getGeometry()) : segment.getLength();
    }

    private LineString snapped;

    private LineString getSnappedGeometry() {
        if (snapped == null) {
            snapped = GeometryUtils.snapLineToLine(getGeometry(), segment.getGeometry());
        }
        return snapped;
    }

    private double getLength() {
        if (getSize() == 1) {
            return 0.0d;
        }
        if (length < 0) {
            if (simplifiedLength) {
                length = GeometryUtils.distance(track.getGeometry(start),
                                                track.getGeometry(end));
            } else {
                length = GeometryUtils.length(getSnappedGeometry());
            }
            if (start > 0) {
                length += GeometryUtils.distance(track.getGeometry(start - 1),
                                                 track.getGeometry(start)) / 2;
            }
            if (end < track.size() - 1) {
                length += GeometryUtils.distance(track.getGeometry(end),
                                                 track.getGeometry(end + 1)) / 2;
            }
        }
        return length;
    }

    private int getStops() {
        if (stops < 0) {
            calculateStops();
        }
        return stops;
    }

    private void calculateStops() {
        if (start == end) {
            this.stops = 0;
            this.stopTime = Duration.ZERO;
            return;
        }
        List<Stop> stops = new LinkedList<>();
        int stopStart = -1;
        for (int idx = start; idx <= end; ++idx) {
            double speed = track.getSpeed(idx);
            if (stopStart < 0) {
                if (speed <= startThresholdSpeed) {
                    stopStart = idx;
                }
            } else if (speed > endThresholdSpeed) {
                stops.add(new Stop(stopStart, idx));
                stopStart = -1;
            }
        }
        if (stopStart >= 0) {
            stops.add(new Stop(stopStart, end));
        }
        this.stopTime = stops.stream()
                             .peek(this::logStop)
                             .map(this::getDuration)
                             .reduce(Duration.ZERO, Duration::plus);
        this.stops = stops.size();
    }

    private Values getMeanValues() {
        int count = getSize();
        if (count == 1) {
            return track.getValues(start);
        }
        double length = 0.0d;
        double speed = 0.0d;
        double carbonDioxide = 0.0d;
        double fuelConsumption = 0.0d;
        double energyConsumption = 0.0d;
        int maxIdx = track.size() - 1;

        for (int idx = start; idx <= end; ++idx) {
            Values values = track.getValues(idx);
            if (meanType == MeanType.HARMONIC) {
                if (idx > 0) {
                    double length0 = track.getLength(idx - 1, idx);
                    double speed0 = (values.getSpeed() + track.getSpeed(idx - 1)) / 2;
                    length += length0;
                    if (speed0 > 0.0d) {
                        speed += length0 / speed0;
                    }
                }

                if (idx < maxIdx) {
                    double length0 = track.getLength(idx, idx + 1);
                    double speed0 = (values.getSpeed() + track.getSpeed(idx + 1)) / 2;
                    length += length0;
                    if (speed0 > 0.0d) {
                        speed += length0 / speed0;
                    }
                }
            } else {
                speed += values.getSpeed();
            }

            carbonDioxide += values.getCarbonDioxide().orElse(0.0d);
            fuelConsumption += values.getFuelConsumption().orElse(0.0d);
            energyConsumption += values.getEnergyConsumption().orElse(0.0d);
        }

        fuelConsumption /= count;
        carbonDioxide /= count;
        energyConsumption /= count;
        switch (meanType) {
            case HARMONIC:
                speed = speed == 0.0d ? 0.0d : length / speed;
                break;
            case ARITHMETIC:
                speed /= count;
                break;
            default:
                throw new AssertionError("unsupported MeanType");
        }

        return new Values(speed, fuelConsumption, energyConsumption, carbonDioxide);
    }

    private Duration getStopTime() {
        if (stopTime == null) {
            calculateStops();
        }
        return stopTime;
    }

    private Duration getDuration(Stop stop) {
        return track.getExtendedDuration(stop.start, stop.end);
    }

    private void logStop(Stop stop) {
        LOG.debug("Found stop in track {} on segment {}, from {} to {}",
                  track, segment, track.getRealIndex(stop.start), track.getRealIndex(stop.end));
    }

    private Duration getDuration() {
        if (start == end) {
            double speed = track.getValues(start).getSpeed();
            if (speed == 0) {
                return Duration.ZERO;
            }
            return BigDecimals.toDuration(BigDecimal.valueOf((segment.getLength() / (speed / 3.6)) * 1000));
        }

        if (duration == null) {
            duration = track.getDuration(start, end);
            BigDecimal scaleFactor = BigDecimal.valueOf(1 / getLengthRatio());
            duration = BigDecimals.toDuration(BigDecimals.create(duration).multiply(scaleFactor));
        }
        return duration;
    }

    private LineString getGeometry() {
        if (geometry == null) {
            geometry = track.getGeometry(start, end);
        }
        return geometry;
    }

    private double getHeading() {
        return GeometryUtils.heading(track.getGeometry(start).getCoordinate(),
                                     track.getGeometry(end).getCoordinate());
    }

    @Override
    public int compareTo(MatchCandidate other) {
        Objects.requireNonNull(other);
        return COMPARATOR.compare(this, other);
    }

    private static final class Stop {
        final int start;
        final int end;

        Stop(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private enum MeanType {
        ARITHMETIC,
        HARMONIC
    }

}
