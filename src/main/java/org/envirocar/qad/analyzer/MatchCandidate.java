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
    private LineString geometry;
    private Duration duration;
    private int stops = -1;
    private Duration stopTime;
    private final int start;
    private final int end;
    private final double startThresholdSpeed;
    private final double endThresholdSpeed;
    private final boolean simplifiedLength;

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
        return this.start;
    }

    public int getSize() {
        return this.end - this.start + 1;
    }

    public int getEnd() {
        return this.end;
    }

    public Segment getSegment() {
        return this.segment;
    }

    public SegmentResult toSegmentResult() {
        Values meanValues = getMeanValues();
        SegmentStatistics statistics = new SegmentStatistics();
        statistics.setFuelConsumption(meanValues.getFuelConsumption().orElse(0.0d));
        statistics.setEnergyConsumption(meanValues.getEnergyConsumption().orElse(0.0d));
        statistics.setEmission(meanValues.getCarbonDioxide().orElse(0.0d));
        statistics.setSpeed(meanValues.getSpeed());
        statistics.setStoppedTime(getStopTime());
        statistics.setTravelTime(getDuration());
        statistics.setStops(getStops());
        return new SegmentResult(this.segment,
                                 statistics,
                                 this.track.getRealIndex(this.start),
                                 this.track.getRealIndex(this.end));
    }

    public boolean checkOrientation() {
        double trackHeading = getHeading();
        double segmentHeading = this.segment.getHeading();
        double deviation = Math.abs(AngleUtils.deviation(trackHeading, segmentHeading));
        if (deviation <= this.parameters.getMaxAngleDeviation()) {
            return true;
        }
        LOG.debug("Orientation deviation to big ({}) for segment {}: segment: {}, track: {}",
                  this.segment, deviation, segmentHeading, trackHeading);
        return false;
    }

    public boolean checkLength() {
        if (Math.abs(getLength() - getSegmentLength()) <= this.parameters.getLengthDifferenceToTolerate() ||
            Math.abs(1 - getLengthRatio()) <= this.parameters.getMaxLengthDeviation()) {
            return true;
        }
        LOG.debug("Length deviation to big for segment {}: segment: {}, track: {}",
                  this.segment, getSegmentLength(), getLength());
        return false;
    }

    private double getLengthRatio() {
        return getLength() / getSegmentLength();
    }

    private double getUnadjustedLengthRatio() {
        return getUnadjustedLength() / getSegmentLength();
    }

    private double getSegmentLength() {
        return this.simplifiedLength ? GeometryUtils.simplifiedLength(this.segment.getGeometry())
                                     : this.segment.getLength();
    }

    private LineString snapped;

    private LineString getSnappedGeometry() {
        if (this.snapped == null) {
            this.snapped = GeometryUtils.snapLineToLine(getGeometry(), this.segment.getGeometry());
        }
        return this.snapped;
    }

    private double getLength() {
        if (getSize() == 1) {
            return 0.0d;
        }
        if (this.length < 0) {
            this.length = getUnadjustedLength();
            if (this.start > 0) {
                this.length += GeometryUtils.distance(this.track.getGeometry(this.start - 1),
                                                      this.track.getGeometry(this.start)) / 2;
            }
            if (this.end < this.track.size() - 1) {
                this.length += GeometryUtils.distance(this.track.getGeometry(this.end),
                                                      this.track.getGeometry(this.end + 1)) / 2;
            }
        }
        return this.length;
    }

    private double getUnadjustedLength() {
        if (this.simplifiedLength) {
            return GeometryUtils.distance(this.track.getGeometry(this.start),
                                          this.track.getGeometry(this.end));
        } else {
            return GeometryUtils.length(getSnappedGeometry());
        }
    }

    private int getStops() {
        if (this.stops < 0) {
            calculateStops();
        }
        return this.stops;
    }

    private void calculateStops() {
        if (this.start == this.end) {
            this.stops = 0;
            this.stopTime = Duration.ZERO;
            return;
        }
        List<Stop> stops = new LinkedList<>();
        int stopStart = -1;
        for (int idx = this.start; idx <= this.end; ++idx) {
            double speed = this.track.getSpeed(idx);
            if (stopStart < 0) {
                if (speed <= this.startThresholdSpeed) {
                    stopStart = idx;
                }
            } else if (speed > this.endThresholdSpeed) {
                stops.add(new Stop(stopStart, idx));
                stopStart = -1;
            }
        }
        if (stopStart >= 0) {
            stops.add(new Stop(stopStart, this.end));
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
            return this.track.getValues(this.start);
        }
        double speed = 0.0d;
        double carbonDioxide = 0.0d;
        double fuelConsumption = 0.0d;
        double energyConsumption = 0.0d;

        for (int idx = this.start; idx <= this.end; ++idx) {
            Values values = this.track.getValues(idx);
            speed += values.getSpeed();
            carbonDioxide += values.getCarbonDioxide().orElse(0.0d);
            fuelConsumption += values.getFuelConsumption().orElse(0.0d);
            energyConsumption += values.getEnergyConsumption().orElse(0.0d);
        }

        return new Values(speed / count,
                          fuelConsumption / count,
                          energyConsumption / count,
                          carbonDioxide / count);
    }

    private Duration getStopTime() {
        if (this.stopTime == null) {
            calculateStops();
        }
        return this.stopTime;
    }

    private Duration getDuration(Stop stop) {
        return this.track.getExtendedDuration(stop.start, stop.end);
    }

    private void logStop(Stop stop) {
        LOG.debug("Found stop in track {} on segment {}, from {} to {}",
                  this.track, this.segment, this.track.getRealIndex(stop.start), this.track.getRealIndex(stop.end));
    }

    private Duration getDuration() {
        if (this.start == this.end) {
            double speed = this.track.getValues(this.start).getSpeed();
            if (speed == 0) {
                return Duration.ZERO;
            }
            return BigDecimals.toDuration(BigDecimal.valueOf(this.segment.getLength() / (speed / 3.6)));
        }

        if (this.duration == null) {
            this.duration = this.track.getDuration(this.start, this.end);
            BigDecimal scaleFactor = BigDecimal.valueOf(1 / getUnadjustedLengthRatio());
            this.duration = BigDecimals.toDuration(BigDecimals.create(this.duration).multiply(scaleFactor));
        }
        return this.duration;
    }

    private LineString getGeometry() {
        if (this.geometry == null) {
            this.geometry = this.track.getGeometry(this.start, this.end);
        }
        return this.geometry;
    }

    private double getHeading() {
        return GeometryUtils.heading(this.track.getGeometry(this.start).getCoordinate(),
                                     this.track.getGeometry(this.end).getCoordinate());
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

}
