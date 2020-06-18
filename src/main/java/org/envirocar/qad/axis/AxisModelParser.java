package org.envirocar.qad.axis;

import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.utils.GeometryException;
import org.envirocar.qad.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class AxisModelParser {
    private final AlgorithmParameters parameters;

    public AxisModelParser(AlgorithmParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
    }

    public AxisModel createAxisModel(FeatureCollection collection) {
        String name = collection.getProperties().path(JsonConstants.NAME).textValue();
        String version = collection.getProperties().path(JsonConstants.VERSION).textValue();
        ModelId modelId = new ModelId(name, version);
        List<Axis> axes = collection.getFeatures().stream()
                                    .collect(groupingBy(this::createAxisId, toList()))
                                    .entrySet().stream().map(e -> createAxis(modelId, e.getKey(), e.getValue()))
                                    .collect(toList());

        return new AxisModel(modelId, axes);
    }

    private AxisId createAxisId(Feature feature) {
        int id = feature.getProperties().path(JsonConstants.AXIS_ID).intValue();
        int direction = feature.getProperties().path(JsonConstants.AXIS_DIRECTION).intValue();
        return new AxisId(id, direction);
    }

    private Axis createAxis(ModelId modelId, AxisId axisId, List<? extends Feature> features) {
        List<Segment> segments = features.stream()
                                         .map(feature -> createSegments(axisId, feature))
                                         .sorted()
                                         .collect(toList());
        return new Axis(modelId, axisId, segments);
    }

    private Segment createSegments(AxisId axisId, Feature feature) {
        int rank = feature.getProperties().path(JsonConstants.SEGMENT_RANK).asInt();
        int maxSpeed = feature.getProperties().path(JsonConstants.MAX_SPEED).asInt();
        Geometry geometry = feature.getGeometry();

        LineString lineString;
        if (geometry instanceof LineString) {
            lineString = (LineString) geometry;
        } else if (geometry instanceof MultiLineString) {
            lineString = GeometryUtils.flatten((MultiLineString) geometry);
        } else {
            throw new IllegalArgumentException("unsupported geometry type: " + geometry);
        }
        try {
            return new Segment(new SegmentId(axisId, rank), maxSpeed, lineString, this.parameters);
        } catch (GeometryException e) {
            throw new RuntimeException(e);
        }
    }

}
