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
import java.math.RoundingMode;
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

    public MatchCandidate(AlgorithmParameters parameters, Segment segment, Track track, int start, int end) {
        this.segment = Objects.requireNonNull(segment);
        this.parameters = Objects.requireNonNull(parameters);
        this.track = Objects.requireNonNull(track);
        this.start = start;
        this.end = end;
        if (start >= end) {
            throw new IllegalArgumentException("start >= end");
        }
        this.simplifiedLength = parameters.isSimplifyLengthCalculation();
        this.startThresholdSpeed = parameters.getStops().getStartThresholdSpeed();
        this.endThresholdSpeed = parameters.getStops().getEndThresholdSpeed();
    }

    public int getStart() {
        return start;
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
        statistics.setFuelConsumption(meanValues.getFuelConsumption());
        statistics.setEnergyConsumption(meanValues.getEnergyConsumption());;
        statistics.setEmission(meanValues.getCarbonDioxide());
        statistics.setSpeed(meanValues.getSpeed());
        statistics.setStoppedTime(getStopTime());
        statistics.setTravelTime(getDuration());
        statistics.setStops(getStops());
        return new SegmentResult(segment, statistics);
    }

    public boolean checkOrientation() {
        double trackHeading = getHeading();
        double segmentHeading = segment.getHeading();
        double deviation = Math.abs(AngleUtils.deviation(trackHeading, segmentHeading));
        if (deviation <= parameters.getMaxAngleDeviation()) {
            LOG.debug("Matching orientation ({}) for segment {}: segment: {}, track: {}",
                      segment, deviation, segmentHeading, trackHeading);
            return true;
        } else {
            LOG.debug("Orientation deviation to big ({}) for segment {}: segment: {}, track: {}",
                      segment, deviation, segmentHeading, trackHeading);
            return false;
        }
    }

    public boolean checkLength() {
        double ratio = getLengthRatio();
        if (Math.abs(1 - ratio) <= parameters.getMaxLengthDeviation()) {
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
        List<Stop> stops = new LinkedList<>();
        int stopStart = -1;
        for (int idx = start; idx <= end; ++idx) {
            double speed = track.getSpeed(idx);
            if (stopStart < 0) {
                if (speed <= startThresholdSpeed) {
                    stopStart = idx;
                }
            } else if (speed > endThresholdSpeed) {
                stops.add(new Stop(start, idx));
                stopStart = -1;
            }
        }
        stopTime = stops.stream()
                        .peek(this::logStop)
                        .map(this::getDuration)
                        .reduce(Duration.ZERO, Duration::plus);
        this.stops = stops.size();
    }

    private Values getMeanValues() {
        int count = (end - start);
        if (count <= 1) {
            return track.getValues(start);
        }
        double sumLength = 0.0d;
        double sumWeightedSpeeds = 0.0d;
        double sumCarbonDioxide = 0.0d;
        double sumFuelConsumption = 0.0d;
        double sumEnergyConsumption = 0.0d;
        int maxIdx = track.size() - 1;
        for (int idx = start; idx <= end; idx++) {
            Values values = track.getValues(idx);
            if (idx > 0) {
                double length = track.getLength(idx - 1, idx);
                double speed = (values.getSpeed() + track.getSpeed(idx - 1)) / 2;
                sumLength += length;
                if (speed != 0) {
                    sumWeightedSpeeds += length / speed;
                }
            }

            if (idx < maxIdx) {
                double length = track.getLength(idx, idx + 1);
                double speed = (values.getSpeed() + track.getSpeed(idx + 1)) / 2;
                sumLength += length;
                if (speed != 0) {
                    sumWeightedSpeeds += length / speed;
                }

            }
            sumCarbonDioxide += values.getCarbonDioxide();
            sumFuelConsumption += values.getFuelConsumption();
            sumEnergyConsumption += values.getEnergyConsumption();
        }

        double speed = sumWeightedSpeeds == 0.0d ? 0.0d : sumLength / sumWeightedSpeeds;
        double fuelConsumption = sumFuelConsumption / count;
        double carbonDioxide = sumCarbonDioxide / count;
        double energyConsumption = sumEnergyConsumption / count;
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
        LOG.debug("Found stop in track {} on segment {}, from {} to {}", track, segment, stop.start, stop.end);
    }

    private Duration getDuration() {
        if (duration == null) {
            duration = track.getDuration(start, end);
            BigDecimal scaleFactor = BigDecimal.ONE.divide(BigDecimal.valueOf(getLengthRatio()), RoundingMode.HALF_UP);
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

}
