package org.envirocar.qad;

import org.envirocar.qad.model.Track;

public interface TrackDensifier {

    Track densify(Track track);

    Track densify(Track track, int numPoints);
}
