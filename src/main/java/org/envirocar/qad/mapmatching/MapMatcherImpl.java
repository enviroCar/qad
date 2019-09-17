package org.envirocar.qad.mapmatching;

import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
public class MapMatcherImpl implements MapMatcher {
    private final MapMatchingService service;

    @Autowired
    public MapMatcherImpl(MapMatchingService service) {
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public FeatureCollection mapMatch(FeatureCollection featureCollection) throws MapMatchingException {

        MapMatchingResult result = service.mapMatch(featureCollection);
        List<Geometry> geometries = result.getMatchedPoints().stream()
                                          .map(MatchedPoint::getPointOnRoad)
                                          .map(Feature::getGeometry)
                                          .collect(toList());

        List<Feature> features = featureCollection.getFeatures();
        if (geometries.size() != features.size()) {
            throw new MapMatchingException(String.format("service returned wrong number of geometries, expected %d but was %d",
                                                         features.size(), geometries.size()));
        }

        IntStream.range(0, features.size()).forEach(i -> features.get(i).setGeometry(geometries.get(i)));
        return featureCollection;
    }

}
