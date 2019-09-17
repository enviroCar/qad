package org.envirocar.qad.model.axis;

import org.locationtech.jts.geom.Envelope;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Axis {
    private List<Segment> segments;
    private Envelope envelope;
    private double length;
    private AxisId id;

    public Axis(AxisId id, List<Segment> segments) {
        this.id = Objects.requireNonNull(id);
        this.segments = Objects.requireNonNull(segments);
    }

    public AxisId getId() {
        return id;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    private Envelope calculateEnvelope() {
        Envelope envelope = new Envelope();
        getSegments().stream().map(Segment::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public double getLength() {
        return length;
    }

    public void prepare() {
        envelope = calculateEnvelope();
        length = getSegments().stream().mapToDouble(Segment::getLength).sum();
    }

    @Override
    public String toString() {
        return String.format("Axis %s", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Axis)) {
            return false;
        }
        Axis axis = (Axis) o;
        return getId().equals(axis.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
