package org.envirocar.qad.model;

import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.Objects;

public class Measurement {
    private final Point geometry;
    private final Instant time;
    private final String id;
    private final Values values;

    public Measurement(String id, Point geometry, Instant time, Values values) {
        this.id = Objects.requireNonNull(id);
        this.geometry = Objects.requireNonNull(geometry);
        this.time = Objects.requireNonNull(time);
        this.values = Objects.requireNonNull(values);
    }

    public String getId() {
        return id;
    }

    public Point getGeometry() {
        return geometry;
    }

    public Instant getTime() {
        return time;
    }

    public Values getValues() {
        return values;
    }

}
