package org.envirocar.qad;

import org.envirocar.qad.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractTrackSplitter implements TrackSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTrackSplitter.class);

    @Override
    public Stream<Track> split(Track track) {
        List<Track> tracks = new LinkedList<>();
        int index;
        Track rest = track;
        while ((index = findSplitIndex(rest)) >= 0) {
            LOG.debug("Splitting track {} at index {}", rest, rest.getRealIndex(index));
            tracks.add(rest.subset(0, index));
            rest = rest.subset(index + 1, rest.size() - 1);
        }
        if (tracks.isEmpty()) {
            return Stream.of(track);
        }
        return tracks.stream();
    }

    protected abstract int findSplitIndex(Track track);
}
