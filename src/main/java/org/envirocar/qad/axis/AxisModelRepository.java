package org.envirocar.qad.axis;

import org.envirocar.qad.model.Enveloped;
import org.locationtech.jts.geom.Envelope;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AxisModelRepository implements Enveloped {

    private final Map<ModelId, AxisModel> axisModels;
    private final Envelope envelope;

    public AxisModelRepository(List<AxisModel> axisModels) {
        this.axisModels = axisModels.stream().collect(Collectors.toMap(AxisModel::getId, Function.identity()));
        this.envelope = calculateEnvelope();
    }

    public Collection<AxisModel> getAxisModels() {
        return Collections.unmodifiableCollection(axisModels.values());
    }

    public Optional<AxisModel> getAxisModel(ModelId modelId) {
        return Optional.ofNullable(axisModels.get(modelId));
    }

    public Optional<AxisModel> getAxisModel(String modelId, String version) {
        return getAxisModel(new ModelId(modelId, version));
    }

    public Optional<AxisModel> getAxisModel(String modelId) {
        return axisModels.keySet().stream()
                         .filter(x -> x.getValue().equals(modelId))
                         .max(Comparator.comparing(ModelId::getVersion))
                         .map(axisModels::get);
    }

    @Override
    public Envelope getEnvelope() {
        return new Envelope(envelope);
    }

    private Envelope calculateEnvelope() {
        Envelope envelope = new Envelope();
        this.axisModels.values().stream().map(AxisModel::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }
}
