package org.envirocar.qad.analyzer;

import org.envirocar.qad.axis.AxisModelRepository;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.result.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

public class AxisModelsAnalyzer implements Analyzer {
    private static final Logger LOG = LoggerFactory.getLogger(AxisModelsAnalyzer.class);
    private final AnalyzerFactory analyzerFactory;
    private final AxisModelRepository repository;
    private final FeatureCollection featureCollection;
    private final TrackPreparer preparer;

    public AxisModelsAnalyzer(AnalyzerFactory analyzerFactory, AxisModelRepository repository,
                              TrackPreparer preparer, FeatureCollection featureCollection) {
        this.analyzerFactory = Objects.requireNonNull(analyzerFactory);
        this.repository = Objects.requireNonNull(repository);
        this.preparer = Objects.requireNonNull(preparer);
        this.featureCollection = Objects.requireNonNull(featureCollection);
    }

    @Override
    public boolean isApplicable() {
        return repository.getEnvelope().intersects(featureCollection.getEnvelope());
    }

    @Override
    public Stream<AnalysisResult> analyze() throws AnalysisException {
        Track track = preparer.prepare(featureCollection);
        return repository.getAxisModels().stream()
                         .map(model -> analyzerFactory.create(model, track))
                         .filter(Analyzer::isApplicable)
                         .flatMap(Analyzer::analyze);

    }

}
