package org.envirocar.qad;

import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;

@FunctionalInterface
public interface TrackParser {
    Track createTrack(FeatureCollection collection) throws TrackParsingException;
}
