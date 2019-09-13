package org.envirocar.qad.model.axis;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;

public class Segment implements Comparable<Segment> {
    private final int rank;
    private final SegmentType type;
    private final LineString geometry;

    public Segment(int rank, SegmentType type, LineString geometry) {
        this.rank = rank;
        this.type = type;
        this.geometry = geometry;
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

    public Envelope getEnvelope() {
        return getGeometry().getEnvelopeInternal();
    }

    @Override
    public int compareTo(Segment that) {
        return Integer.compare(rank, that.getRank());
    }
}
