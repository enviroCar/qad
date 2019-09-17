package org.envirocar.qad.model;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Track {

    private final String id;
    private final LineString geometry;
    private final List<Measurement> measurements;

    public Track(String id, List<Measurement> measurements) {
        this.id = Objects.requireNonNull(id);
        this.measurements = new ArrayList<>(measurements);
        if (measurements.isEmpty()) {
            throw new IllegalArgumentException("no measurements");
        }
        this.geometry = calculateLineString();

    }

    private LineString calculateLineString() {
        GeometryFactory factory = this.measurements.iterator().next().getGeometry().getFactory();
        return factory.createLineString(this.measurements.stream().map(Measurement::getGeometry).map(Point.class::cast)
                                                         .map(Point::getCoordinate).toArray(Coordinate[]::new));
    }

    public List<Measurement> getMeasurements() {
        return Collections.unmodifiableList(measurements);
    }

    public Measurement getMeasurement(int idx) {
        return this.measurements.get(idx);
    }

    public int size() {
        return getMeasurements().size();
    }

    public String getId() {
        return id;
    }

    public LineString getGeometry() {
        return geometry;
    }

    public Envelope getEnvelope() {
        return geometry.getEnvelopeInternal();
    }

}
