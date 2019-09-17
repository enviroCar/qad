package org.envirocar.qad;

import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;

public interface TrackParser {
    Track createTrack(FeatureCollection collection);
}
