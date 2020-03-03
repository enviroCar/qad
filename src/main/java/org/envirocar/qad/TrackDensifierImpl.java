package org.envirocar.qad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.Values;
import org.envirocar.qad.utils.GeometryUtils;
import org.envirocar.qad.utils.Interpolate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TrackDensifierImpl implements TrackDensifier {
    private final int numPoints;
    private final JsonNodeCreator nodeFactory;

    @Autowired
    public TrackDensifierImpl(AlgorithmParameters parameters, JsonNodeCreator nodeFactory) {
        this.numPoints = parameters.getDensify().getNumPoints();
        this.nodeFactory = Objects.requireNonNull(nodeFactory);
    }

    @Override
    public Track densify(Track track) {
        return densify(track, numPoints);
    }

    @Override
    public FeatureCollection densify(FeatureCollection track) {
        return densify(track, numPoints);
    }

    @Override
    public FeatureCollection densify(FeatureCollection track, int numPoints) throws TrackParsingException {
        if (track.isEmpty()) {
            return track;
        }
        List<Feature> features = new ArrayList<>(track.size() + numPoints * (track.size() - 1));
        Feature prev = track.getFeature(0);
        features.add(prev);
        for (int i = 1; i < track.size(); i++) {
            Feature curr = track.getFeature(i);
            if (prev != null) {
                features.addAll(interpolateBetween(prev, curr, numPoints));
            }
            features.add(curr);
            prev = curr;
        }
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setProperties(track.getProperties());
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    @Override
    public Track densify(Track track, int numPoints) {
        if (track.size() == 0) {
            return track;
        }
        List<Measurement> features = new ArrayList<>(track.size() + numPoints * (track.size() - 1));
        features.add(track.getMeasurement(0));
        for (int i = 1; i < track.size(); i++) {
            Measurement curr = track.getMeasurement(i);
            features.addAll(interpolateBetween(track.getMeasurement(i - 1), curr, numPoints));
            features.add(curr);
        }
        return new Track(track.getId(), track.getFuelType(), features);
    }

    private String getId(Feature m1) {
        return m1.getProperties().path(JsonConstants.ID).textValue();
    }

    private Instant getTime(Feature feature) {
        return OffsetDateTime.parse(feature.getProperties().path(JsonConstants.TIME).textValue(),
                                    DateTimeFormatter.ISO_DATE_TIME).toInstant();
    }

    private ObjectNode interpolateValues(Feature f1, Feature f2, double fraction) {
        JsonNode p1 = f1.getProperties().path(JsonConstants.PHENOMENONS);
        JsonNode p2 = f2.getProperties().path(JsonConstants.PHENOMENONS);
        ObjectNode node = nodeFactory.objectNode();
        Set<String> keys = new HashSet<>();
        p1.fieldNames().forEachRemaining(keys::add);
        p2.fieldNames().forEachRemaining(keys::add);
        for (String key : keys) {
            JsonNode v1 = p1.path(key).path(JsonConstants.VALUE);
            JsonNode v2 = p2.path(key).path(JsonConstants.VALUE);
            if (v1.isValueNode()) {
                if (v2.isValueNode()) {
                    if (v1.isNumber() && v2.isNumber()) {
                        node.putObject(key)
                            .put(JsonConstants.VALUE, Interpolate.linear(v1.doubleValue(), v2.doubleValue(), fraction))
                            .set(JsonConstants.UNIT, p1.path(key).path(JsonConstants.UNIT));
                    } else {
                        node.set(key, p1.path(JsonConstants.VALUE));
                    }
                } else {
                    node.set(key, p1.path(JsonConstants.VALUE));
                }
            } else if (v2.isValueNode()) {
                node.set(key, p2.path(JsonConstants.VALUE));
            }
        }
        return node;
    }

    private List<Feature> interpolateBetween(Feature m1, Feature m2, int numPoints) {
        LineSegment lineSegment = new LineSegment(m1.getGeometry().getCoordinate(),
                                                  m2.getGeometry().getCoordinate());

        GeometryFactory geometryFactory = m1.getGeometry().getFactory();
        if (GeometryUtils.length(lineSegment) <= 0.0d) {
            return Collections.emptyList();
        }
        List<Feature> features = new ArrayList<>(numPoints);
        for (int i = 1; i <= numPoints; ++i) {
            ObjectNode properties = nodeFactory.objectNode();
            double fraction = i / (double) (numPoints + 1);
            Instant time = Interpolate.linear(getTime(m1), getTime(m2), fraction);
            Point geometry = geometryFactory.createPoint(lineSegment.pointAlong(fraction));
            properties.put(JsonConstants.ID, String.format("%s_%s_%d", getId(m1), getId(m2), i));
            properties.put(JsonConstants.TIME, time.toString());
            properties.set(JsonConstants.PHENOMENONS, interpolateValues(m1, m2, fraction));
            Feature feature = new Feature();
            feature.setGeometry(geometry);
            feature.setProperties(properties);
            features.add(feature);
        }
        return features;
    }

    private List<Measurement> interpolateBetween(Measurement m1, Measurement m2, int numPoints) {
        LineSegment lineSegment = new LineSegment(m1.getGeometry().getCoordinate(),
                                                  m2.getGeometry().getCoordinate());
        GeometryFactory geometryFactory = m1.getGeometry().getFactory();
        if (GeometryUtils.length(lineSegment) <= 0.0d) {
            return Collections.emptyList();
        }
        List<Measurement> features = new ArrayList<>(numPoints);
        for (int i = 1; i <= numPoints; ++i) {
            double fraction = i / (double) (numPoints + 1);
            Instant time = Interpolate.linear(m1.getTime(), m2.getTime(), fraction);
            Values values = Values.interpolate(m1.getValues(), m2.getValues(), fraction);
            Point geometry = geometryFactory.createPoint(lineSegment.pointAlong(fraction));
            String id = String.format("%s_%s_%d", m1.getId(), m2.getId(), i);
            features.add(new Measurement(id, geometry, time, values));
        }
        return features;
    }

}
