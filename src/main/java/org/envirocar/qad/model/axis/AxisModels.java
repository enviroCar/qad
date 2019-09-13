package org.envirocar.qad.model.axis;

import org.locationtech.jts.geom.Envelope;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AxisModels {

    public Map<String, AxisModel> axisModels;

    public AxisModels(List<AxisModel> axisModels) {
        this.axisModels = axisModels.stream().collect(Collectors.toMap(AxisModel::getId, Function.identity()));
    }

    public Map<String, AxisModel> getAxisModels() {
        return Collections.unmodifiableMap(axisModels);
    }

    public AxisModel getAxisModel(String id) {
        return axisModels.get(id);
    }

    public Envelope getEnvelope() {
        Envelope envelope = new Envelope();
        this.axisModels.values().stream().map(AxisModel::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }
}
