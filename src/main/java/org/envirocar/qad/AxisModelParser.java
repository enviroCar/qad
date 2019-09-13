package org.envirocar.qad;

import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.axis.Axis;
import org.envirocar.qad.model.axis.AxisModel;
import org.envirocar.qad.model.axis.Segment;
import org.envirocar.qad.model.axis.SegmentType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class AxisModelParser {
    private static final String SEGMENT_TYPE = "Segmenttyp";
    private static final String SEGMENT_RANK = "rank";
    private static final String AXIS_ID = "Achsen_ID";

    public AxisModel createAxisModel(FeatureCollection collection) {
        List<Axis> axes = collection.getFeatures().stream()
                                    .collect(groupingBy(feature -> feature.getProperties()
                                                                          .path(AXIS_ID)
                                                                          .textValue(),
                                                        toList()))
                                    .entrySet().stream().map(e -> createAxis(e.getKey(), e.getValue()))
                                    .collect(toList());
        return new AxisModel(collection.getName(), axes);
    }

    private Axis createAxis(String id, List<Feature> features) {

        List<Segment> segments = features.stream().map(feature -> {
            int rank = feature.getProperties().path(SEGMENT_RANK).asInt();
            int type = feature.getProperties().path(SEGMENT_TYPE).asInt();
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
            return new Segment(rank, SegmentType.fromInteger(type), lineString);
        }).sorted(Comparator.comparingInt(Segment::getRank)).collect(toList());
        return new Axis(id, segments);
    }

}
