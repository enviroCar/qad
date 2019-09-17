package org.envirocar.qad;

import org.envirocar.qad.model.axis.AxisModel;
import org.locationtech.jts.geom.Envelope;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AxisModelRepository {

    private final Map<String, AxisModel> axisModels;
    private Envelope envelope;

    public AxisModelRepository(List<AxisModel> axisModels) {
        this.axisModels = axisModels.stream().collect(Collectors.toMap(AxisModel::getId, Function.identity()));
    }

    public Collection<AxisModel> getAxisModels() {
        return Collections.unmodifiableCollection(axisModels.values());
    }

    public AxisModel getAxisModel(String id) {
        return axisModels.get(id);
    }

    @PostConstruct
    public void prepare() {
        envelope = calculateEnvelope();
    }

    public Envelope getEnvelope() {
        return new Envelope(envelope);
    }

    private Envelope calculateEnvelope() {
        Envelope envelope = new Envelope();
        this.axisModels.values().stream().map(AxisModel::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }
}
