package org.envirocar.qad.mapmatching;

import org.envirocar.qad.model.FeatureCollection;

public interface MapMatcher {

    FeatureCollection mapMatch(FeatureCollection featureCollection) throws MapMatchingException;

}
