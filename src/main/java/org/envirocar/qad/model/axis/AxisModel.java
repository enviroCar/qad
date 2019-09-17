package org.envirocar.qad.model.axis;

import org.envirocar.qad.model.Track;
import org.locationtech.jts.geom.Envelope;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class AxisModel {

    private final String id;
    private final Map<AxisId, Axis> axis;
    private Envelope envelope;

    public AxisModel(String id, Collection<Axis> axes) {
        this.id = Objects.requireNonNull(id);
        this.axis = axes.stream().collect(toMap(Axis::getId, Function.identity()));
    }

    public String getId() {
        return id;
    }

    public Collection<Axis> getAxis() {
        return Collections.unmodifiableCollection(axis.values());
    }

    public Axis getAxis(String id) {
        return axis.get(id);
    }

    public void prepare() {
        getAxis().forEach(Axis::prepare);
        this.envelope = calculateEnvelope();
    }

    public Envelope getEnvelope() {
        return new Envelope(envelope);
    }

    public boolean isApplicable(Track track) {
        return getEnvelope().intersects(track.getEnvelope());
    }

    private Envelope calculateEnvelope() {
        Envelope envelope = new Envelope();
        getAxis().stream().map(Axis::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }
}
