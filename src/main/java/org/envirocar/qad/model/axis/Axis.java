package org.envirocar.qad.model.axis;

import org.locationtech.jts.geom.Envelope;

import java.util.List;
import java.util.Objects;

public class Axis {
    private String id;
    private List<Segment> segments;

    public Axis(String id, List<Segment> segments) {
        this.id = Objects.requireNonNull(id);
        this.segments = Objects.requireNonNull(segments);

    }

    public String getId() {
        return id;
    }

    public Envelope getEnvelope() {
        Envelope envelope = new Envelope();
        getSegments().stream().map(Segment::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;

    }

    public List<Segment> getSegments() {
        return segments;
    }

}
