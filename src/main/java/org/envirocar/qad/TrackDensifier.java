package org.envirocar.qad;

import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;

public interface TrackDensifier {

    Track densify(Track track);

    Track densify(Track track, int numPoints);

    FeatureCollection densify(FeatureCollection track);

    FeatureCollection densify(FeatureCollection track, int numPoints);
}

