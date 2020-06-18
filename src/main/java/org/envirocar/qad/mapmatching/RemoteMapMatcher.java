package org.envirocar.qad.mapmatching;

import okhttp3.ResponseBody;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class RemoteMapMatcher implements MapMatcher {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteMapMatcher.class);
    private final MapMatchingService service;

    public RemoteMapMatcher(MapMatchingService service) {
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public FeatureCollection mapMatch(FeatureCollection featureCollection) throws MapMatchingException {
        try {
            LOG.debug("Map matching track {}", featureCollection.getProperties().path(JsonConstants.ID).textValue());
            Response<MapMatchingResult> result = this.service.mapMatch(featureCollection).execute();

            if (!result.isSuccessful() || result.body() == null) {
                Exception suppressed = null;
                ResponseBody responseBody = result.errorBody();
                String response = null;
                if (responseBody != null) {
                    try {
                        response = responseBody.string();
                    } catch (IOException ex) {
                        suppressed = ex;
                    }
                }
                String message = String.format("Map matching service returned %d: %s", result.code(), response);
                MapMatchingException mapMatchingException = new MapMatchingException(message);
                if (suppressed != null) {
                    mapMatchingException.addSuppressed(suppressed);
                }
                throw mapMatchingException;
            }
            List<Geometry> geometries = result.body().getMatchedPoints().stream()
                                              .map(MatchedPoint::getPointOnRoad)
                                              .map(Feature::getGeometry)
                                              .collect(toList());

            List<Feature> features = featureCollection.getFeatures();
            if (geometries.size() != features.size()) {
                String message = String.format("service returned wrong number of geometries, expected %d but was %d",
                                               features.size(), geometries.size());
                throw new MapMatchingException(message);
            }

            IntStream.range(0, features.size()).forEach(i -> features.get(i).setGeometry(geometries.get(i)));
            LOG.debug("Map matched track {}", featureCollection.getProperties().path(JsonConstants.ID).textValue());
            return featureCollection;
        } catch (IOException e) {
            throw new MapMatchingException(e);
        }
    }

}
