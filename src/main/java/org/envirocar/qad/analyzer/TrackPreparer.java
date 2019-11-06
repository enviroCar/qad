package org.envirocar.qad.analyzer;

import org.envirocar.qad.TrackDensifier;
import org.envirocar.qad.TrackParser;
import org.envirocar.qad.TrackParsingException;
import org.envirocar.qad.mapmatching.MapMatcher;
import org.envirocar.qad.mapmatching.MapMatchingException;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class TrackPreparer {
    private final MapMatcher mapMatcher;
    private final TrackParser trackParser;
    private final TrackDensifier densifier;

    @Autowired
    public TrackPreparer(TrackParser trackParser,
                         TrackDensifier densifier,
                         Optional<MapMatcher> mapMatcher) {
        this.mapMatcher = Objects.requireNonNull(mapMatcher).orElse(null);
        this.trackParser = Objects.requireNonNull(trackParser);
        this.densifier = Objects.requireNonNull(densifier);
    }

    public Track prepare(FeatureCollection featureCollection) throws TrackParsingException {
        return trackParser.createTrack(densifier.densify(mapMatch(featureCollection)));
    }

    private FeatureCollection mapMatch(FeatureCollection featureCollection) {
        if (mapMatcher == null) {
            return featureCollection;
        }
        try {
            return mapMatcher.mapMatch(featureCollection);
        } catch (MapMatchingException ex) {
            throw new AnalysisException("Could not map match track", ex);
        }
    }
}
