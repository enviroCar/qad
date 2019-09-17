package org.envirocar.qad.model.axis;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;

import java.util.Objects;

public class Segment implements Comparable<Segment> {
    private final int rank;
    private final SegmentType type;
    private final LineString geometry;
    private double length;
    private double orientation;
    private final int maxSpeed;
    private final String name;
    private final AxisId axis;
    private Geometry buffer;
    private final String trafficLight;

    public Segment(AxisId axis, String name, int rank, SegmentType type, int maxSpeed, LineString geometry,
                   String trafficLight) {
        this.axis = Objects.requireNonNull(axis);
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.geometry = Objects.requireNonNull(geometry);
        this.maxSpeed = maxSpeed;
        this.rank = rank;
        this.trafficLight = trafficLight;
    }

    public String getTrafficLight() {
        return trafficLight;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public String getName() {
        return name;
    }

    public AxisId getAxis() {
        return axis;
    }

    public int getRank() {
        return rank;
    }

    public SegmentType getType() {
        return type;
    }

    public LineString getGeometry() {
        return geometry;
    }

    public double getOrientation() {
        return orientation;
    }

    public Envelope getEnvelope() {
        return getGeometry().getEnvelopeInternal();
    }

    @Override
    public int compareTo(Segment that) {
        return Integer.compare(rank, that.getRank());
    }

    public double getLength() {
        return length;
    }

    public Geometry getBuffer() {
        return buffer;
    }

    public boolean bufferIntersects(Geometry other) {
        return buffer.intersects(other);
    }

    public void prepare(double bufferSize) {
        // the envelope is cached inside the geometry
        length = geometry.getLength();
        buffer = geometry.buffer(bufferSize);
        orientation = getOrientation(geometry);
    }

    private double getOrientation(LineString lineString) {
        if (lineString.isEmpty()) {
            return 0.0d;
        }
        Coordinate c0 = lineString.getCoordinateN(0);
        Coordinate c1 = lineString.getCoordinateN(lineString.getNumPoints());
        return Math.toDegrees(new LineSegment(c0, c1).angle());
    }

}
