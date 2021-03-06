package org.envirocar.qad.axis;

import org.envirocar.qad.model.Enveloped;
import org.envirocar.qad.utils.TypeSafeSpatialIndex;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Axis implements Comparable<Axis>, Enveloped {
    private final List<Segment> segments;
    private final Envelope envelope = new Envelope();
    private final double length;
    private final AxisId id;
    private final int size;
    private final ModelId modelId;
    private final TypeSafeSpatialIndex<Segment> spatialIndex;

    public Axis(ModelId modelId, AxisId id, List<Segment> segments) {
        this.modelId = Objects.requireNonNull(modelId);
        this.id = Objects.requireNonNull(id);
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("no segments for axis " + id);
        }
        this.segments = Objects.requireNonNull(segments);

        segments.stream().map(Segment::getEnvelope).forEach(this.envelope::expandToInclude);
        this.length = segments.stream().mapToDouble(Segment::getLength).sum();
        this.size = segments.size();
        setPrevAndNext(segments);
        this.spatialIndex = new TypeSafeSpatialIndex<>(segments, segment -> segment.getBuffer().getEnvelopeInternal());
    }

    public List<Segment> findIntersectingSegments(Geometry geometry) {
        List<Segment> query = this.spatialIndex.query(geometry, segment -> {
            return segment.bufferIntersects(geometry);
        });
        Collections.sort(query);
        return query;
    }

    public ModelId getModelId() {
        return this.modelId;
    }

    private void setPrevAndNext(List<? extends Segment> segments) {
        for (int i = 0; i < this.size; ++i) {
            Segment segment = segments.get(i);
            int prev = i - 1;
            int next = i + 1;
            if (prev >= 0) {
                segment.setPrev(segments.get(prev));
            }
            if (next < this.size) {
                segment.setNext(segments.get(next));
            }
        }
    }

    public int getSize() {
        return this.size;
    }

    public AxisId getId() {
        return this.id;
    }

    @Override
    public Envelope getEnvelope() {
        return this.envelope;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(this.segments);
    }

    public boolean isApplicable(Enveloped enveloped) {
        return getEnvelope().intersects(enveloped.getEnvelope());
    }

    public double getLength() {
        return this.length;
    }

    @Override
    public String toString() {
        return String.format("Axis %s", this.id);
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

    @Override
    public int compareTo(Axis that) {
        return getId().compareTo(that.getId());
    }

}
