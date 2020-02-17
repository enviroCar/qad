package org.envirocar.qad.axis;

import org.envirocar.qad.model.Enveloped;
import org.locationtech.jts.geom.Envelope;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class AxisModel implements Comparable<AxisModel>, Enveloped {

    private final ModelId id;
    private final Map<AxisId, Axis> axis;
    private final Envelope envelope;

    public AxisModel(ModelId id, Collection<Axis> axes) {
        this.id = Objects.requireNonNull(id);
        this.axis = axes.stream().collect(toMap(Axis::getId, Function.identity()));
        this.envelope = calculateEnvelope();
    }

    public ModelId getId() {
        return id;
    }

    public String getVersion() {
        return id.getVersion();
    }

    public Collection<Axis> getAxis() {
        return Collections.unmodifiableCollection(axis.values());
    }

    public Optional<Axis> getAxis(String id) {
        return Optional.ofNullable(id)
                       .map(x -> x.split("_"))
                       .filter(x -> x.length == 2)
                       .map(x -> Stream.of(x).mapToInt(Integer::parseInt).toArray())
                       .map(x -> new AxisId(x[0], x[1]))
                       .map(axis::get);
    }

    @Override
    public Envelope getEnvelope() {
        return new Envelope(envelope);
    }

    private Envelope calculateEnvelope() {
        Envelope envelope = new Envelope();
        getAxis().stream().map(Axis::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AxisModel)) {
            return false;
        }
        AxisModel axisModel = (AxisModel) o;
        return getId().equals(axisModel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public int compareTo(AxisModel axisModel) {
        return getId().compareTo(axisModel.getId());
    }
}
