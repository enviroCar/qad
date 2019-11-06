package org.envirocar.qad.analyzer;

import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.axis.Axis;
import org.envirocar.qad.axis.AxisModel;
import org.envirocar.qad.axis.AxisModelRepository;
import org.envirocar.qad.axis.Segment;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AnalyzerFactory {
    private final AlgorithmParameters parameters;
    private final AxisModelRepository repository;
    private final TrackPreparer trackPreparer;

    @Autowired
    private AnalyzerFactory(AlgorithmParameters parameters, AxisModelRepository repository,
                            TrackPreparer trackPreparer) {
        this.parameters = Objects.requireNonNull(parameters);
        this.repository = Objects.requireNonNull(repository);
        this.trackPreparer = Objects.requireNonNull(trackPreparer);
    }

    public MatchCandidate create(Segment segment, Track track, int start, int end) {
        return new MatchCandidate(parameters, segment, track, start, end);
    }

    public Analyzer create(AxisModel axisModel, Track track) {
        return new AxisModelAnalyzer(this, axisModel, track);
    }

    public Analyzer create(Axis axis, Track track) {
        return new AxisAnalyzer(this, axis, track);
    }

    public Analyzer create(FeatureCollection featureCollection) {
        return new AxisModelsAnalyzer(this, repository, trackPreparer, featureCollection);
    }

}

