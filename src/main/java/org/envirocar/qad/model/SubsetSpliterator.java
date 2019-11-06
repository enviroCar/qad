package org.envirocar.qad.model;

import org.envirocar.qad.utils.SpliteratorAdapter;
import org.locationtech.jts.geom.Envelope;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SubsetSpliterator implements SpliteratorAdapter<Track> {
    private final Envelope envelope;
    private final Track track;
    private int begin = -1;
    private int idx = 0;

    SubsetSpliterator(Track track, Envelope envelope) {
        this.track = Objects.requireNonNull(track);
        this.envelope = Objects.requireNonNull(envelope);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Track> action) {
        List<Measurement> measurements = track.getMeasurements();
        final int size = measurements.size();
        while (idx < size) {
            if (envelope.contains(measurements.get(idx).getGeometry().getCoordinate())) {
                if (begin < 0) {
                    begin = idx;
                }
            } else if (begin >= 0) {
                return subset(action);
            }
            idx++;
        }
        if (begin >= 0) {
            return subset(action);
        }
        return false;
    }

    private boolean subset(Consumer<? super Track> action) {
        int start = begin > 0 ? begin - 1 : begin;
        int end = idx < track.size() ? idx : idx - 1;
        action.accept(track.subset(start, end));
        begin = -1;
        idx++;
        return true;
    }
}
