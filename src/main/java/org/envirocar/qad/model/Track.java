package org.envirocar.qad.model;

import org.envirocar.qad.configuration.JtsConfiguration;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Track implements Enveloped {

    private final String id;
    private final LineString geometry;
    private final List<Measurement> measurements;
    private final String fuelType;

    public Track(String id, String fuelType, List<Measurement> measurements) {
        this.id = Objects.requireNonNull(id);
        this.fuelType = fuelType;
        if (measurements instanceof RandomAccess) {
            this.measurements = Objects.requireNonNull(measurements);
        } else {
            this.measurements = new ArrayList<>(measurements);
        }
        this.geometry = calculateLineString();
    }

    public String getFuelType() {
        return fuelType;
    }

    private LineString calculateLineString() {
        return getGeometry(0, size() - 1);
    }

    public final Stream<Track> subset(Envelope envelope) {
        return StreamSupport.stream(new SubsetSpliterator(this, envelope), false);
    }

    public Track subset(int begin, int end) {
        return new TrackSubset(getId(), getFuelType(), getMeasurements(), begin, end);
    }

    private static class TrackSubset extends Track {
        private final int begin;
        private final int end;

        public TrackSubset(String id, String fuelType, List<Measurement> measurements, int begin, int end) {
            super(id, fuelType, measurements.subList(begin, end + 1));
            this.begin = begin;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("TrackSubset{id=%s, begin=%d, end=%d}", getId(), begin, end);
        }
    }

    public List<Measurement> getMeasurements() {
        return Collections.unmodifiableList(measurements);
    }

    public Measurement getMeasurement(int idx) {
        return this.measurements.get(idx);
    }

    public int size() {
        return measurements.size();
    }

    public final Point getGeometry(int idx) {
        return getMeasurement(idx).getGeometry();
    }

    public final Instant getTime(int idx) {
        return getMeasurement(idx).getTime();
    }

    public final double getSpeed(int idx) {
        return getMeasurement(idx).getValues().getSpeed();
    }

    public final Values getValues(int idx) {
        return getMeasurement(idx).getValues();
    }

    public final double getLength(int start, int end) {
        return getGeometry(start, end).getLength();
    }

    public final LineString getGeometry(int start, int end) {
        if (start == end) {
            return JtsConfiguration.geometryFactory().createLineString((CoordinateSequence) null);
        }
        return JtsConfiguration.geometryFactory().createLineString(IntStream.rangeClosed(start, end)
                                                                            .mapToObj(this::getMeasurement)
                                                                            .map(Measurement::getGeometry)
                                                                            .map(Point::getCoordinate)
                                                                            .toArray(Coordinate[]::new));
    }

    public final Duration getExtendedDuration(int start, int end) {
        Temporal startTime = getTime(start);
        Temporal endTime = getTime(end);
        if (start > 0) {
            startTime = Duration.between(getTime(start - 1), startTime).dividedBy(2).subtractFrom(startTime);
        }
        if (end < size()) {
            endTime = Duration.between(endTime, getTime(end + 1)).dividedBy(2).addTo(endTime);
        }
        return Duration.between(startTime, endTime);
    }

    public final Duration getDuration(int start, int end) {
        return Duration.between(getTime(start), getTime(end));
    }

    public final String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId();
    }

    public LineString getGeometry() {
        return geometry;
    }

    @Override
    public Envelope getEnvelope() {
        return getGeometry().getEnvelopeInternal();
    }

}
