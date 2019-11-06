package org.envirocar.qad.axis;

import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.model.Enveloped;
import org.envirocar.qad.utils.GeometryException;
import org.envirocar.qad.utils.GeometryUtils;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import java.util.Objects;
import java.util.Optional;

public class Segment implements Comparable<Segment>, Enveloped {
    private final boolean trafficLightInfluence;
    private final LineString geometry;
    private final int maxSpeed;
    private final String trafficLight;
    private final double length;
    private final double heading;
    private final Geometry buffer;
    private final SegmentId id;
    private Segment prev;
    private Segment next;

    public Segment(SegmentId id, boolean trafficLightInfluence, int maxSpeed, LineString geometry,
                   String trafficLight, AlgorithmParameters parameters) throws GeometryException {
        this.id = Objects.requireNonNull(id);
        this.trafficLightInfluence = trafficLightInfluence;
        this.geometry = Objects.requireNonNull(geometry);
        this.maxSpeed = maxSpeed;
        this.trafficLight = trafficLight;
        this.length = GeometryUtils.length(geometry);
        this.buffer = GeometryUtils.buffer(geometry, parameters.getSegments().getBufferSize());
        this.heading = GeometryUtils.heading(geometry);
    }

    public Optional<Segment> prev() {
        return Optional.ofNullable(prev);
    }

    public Optional<Segment> next() {
        return Optional.ofNullable(next);
    }

    public void setPrev(Segment prev) {
        this.prev = Objects.requireNonNull(prev);
    }

    public void setNext(Segment next) {
        this.next = Objects.requireNonNull(next);
    }

    public SegmentId getId() {
        return id;
    }

    public String getName() {
        return this.id.toString();
    }

    public String getTrafficLight() {
        return trafficLight;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public AxisId getAxis() {
        return id.getAxis();
    }

    public int getRank() {
        return id.getRank();
    }

    public boolean isTrafficLightInfluence() {
        return trafficLightInfluence;
    }

    public LineString getGeometry() {
        return geometry;
    }

    public double getHeading() {
        return heading;
    }

    @Override
    public Envelope getEnvelope() {
        return getBuffer().getEnvelopeInternal();
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

    @Override
    public int compareTo(Segment that) {
        return getId().compareTo(that.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Segment)) {
            return false;
        }
        Segment segment = (Segment) o;
        return getId().equals(segment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getId().toString();
    }

}
