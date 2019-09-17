package org.envirocar.qad;

import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.axis.Axis;
import org.envirocar.qad.model.axis.AxisId;
import org.envirocar.qad.model.axis.AxisModel;
import org.envirocar.qad.model.axis.Segment;
import org.envirocar.qad.model.axis.SegmentType;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class AxisModelParser {
    private static final String SEGMENT_TYPE = "type";
    private static final String SEGMENT_RANK = "rank";
    private static final String AXIS_ID = "axis";
    private static final String AXIS_DIRECTION = "direction";
    private static final String MAX_SPEED = "maxSpeed";
    private static final String NAME = "name";
    private static final String TRAFFIC_LIGHT = "LSA";
    private final double segmentBufferSize;

    public AxisModelParser(@Value("${qad.model.segmentBufferSize}") double segmentBufferSize) {
        this.segmentBufferSize = segmentBufferSize;
    }

    public AxisModel createAxisModel(FeatureCollection collection) {
        List<Axis> axes = collection.getFeatures().stream()
                                    .collect(groupingBy(this::createAxisId, toList()))
                                    .entrySet().stream().map(e -> createAxis(e.getKey(), e.getValue()))
                                    .collect(toList());
        AxisModel axisModel = new AxisModel(collection.getName(), axes);
        axisModel.prepare();
        return axisModel;
    }

    @NotNull
    private AxisId createAxisId(Feature feature) {
        int id = feature.getProperties().path(AXIS_ID).intValue();
        int direction = feature.getProperties().path(AXIS_DIRECTION).intValue();
        return new AxisId(id, direction);
    }

    private Axis createAxis(AxisId axisId, List<Feature> features) {

        List<Segment> segments = features.stream().map(feature -> {
            int rank = feature.getProperties().path(SEGMENT_RANK).asInt();
            int type = feature.getProperties().path(SEGMENT_TYPE).asInt();
            int maxSpeed = feature.getProperties().path(MAX_SPEED).asInt();
            String name = feature.getProperties().path(NAME).textValue();
            String trafficLight = feature.getProperties().path(TRAFFIC_LIGHT).textValue();

            Geometry geometry = feature.getGeometry();

            LineString lineString;
            if (geometry instanceof LineString) {
                lineString = (LineString) geometry;
            } else if (geometry instanceof MultiLineString) {
                Coordinate[] coordinates = IntStream.range(0, geometry.getNumGeometries())
                                                    .mapToObj(geometry::getGeometryN)
                                                    .map(Geometry::getCoordinates).flatMap(Arrays::stream)
                                                    .toArray(Coordinate[]::new);
                lineString = geometry.getFactory().createLineString(coordinates);
            } else {
                throw new IllegalArgumentException("unsupported geometry type: " + geometry);
            }
            Segment segment = new Segment(axisId, name, rank, SegmentType.fromInteger(type),
                                          maxSpeed, lineString, trafficLight);
            segment.prepare(segmentBufferSize);
            return segment;
        }).sorted(Comparator.comparingInt(Segment::getRank)).collect(toList());
        Axis axis = new Axis(axisId, segments);
        axis.prepare();
        return axis;
    }

}
