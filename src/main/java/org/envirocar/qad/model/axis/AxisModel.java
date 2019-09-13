package org.envirocar.qad.model.axis;

import org.locationtech.jts.geom.Envelope;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class AxisModel {

    private final String id;
    private final Map<String, Axis> axis;

    public AxisModel(String id, Collection<Axis> axes) {
        this.id = Objects.requireNonNull(id);
        this.axis = axes.stream().collect(toMap(Axis::getId, Function.identity()));
    }

    public String getId() {
        return id;
    }

    public Map<String, Axis> getAxis() {
        return axis;
    }

    public Axis getAxis(String id) {
        return axis.get(id);
    }

    public Envelope getEnvelope() {
        Envelope envelope = new Envelope();
        getAxis().values().stream().map(Axis::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }
}
