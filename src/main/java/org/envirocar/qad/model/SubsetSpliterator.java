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
    private int idx;

    SubsetSpliterator(Track track, Envelope envelope) {
        this.track = Objects.requireNonNull(track);
        this.envelope = Objects.requireNonNull(envelope);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Track> action) {
        List<Measurement> measurements = this.track.getMeasurements();
        int size = measurements.size();
        while (this.idx < size) {
            if (this.envelope.contains(measurements.get(this.idx).getGeometry().getCoordinate())) {
                if (this.begin < 0) {
                    this.begin = this.idx;
                }
            } else if (this.begin >= 0) {
                return subset(action);
            }
            this.idx++;
        }
        if (this.begin >= 0) {
            return subset(action);
        }
        return false;
    }

    private boolean subset(Consumer<? super Track> action) {
        int start = this.begin > 0 ? this.begin - 1 : this.begin;
        int end = this.idx < this.track.size() ? this.idx : this.idx - 1;
        action.accept(this.track.subset(start, end));
        this.begin = -1;
        this.idx++;
        return true;
    }
}
