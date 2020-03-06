package org.envirocar.qad;

import org.envirocar.qad.model.Track;

import java.util.stream.Stream;

public interface TrackSplitter {
    Stream<Track> split(Track track);
}
