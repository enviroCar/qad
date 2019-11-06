package org.envirocar.qad;

import org.envirocar.qad.analyzer.Analyzer;
import org.envirocar.qad.analyzer.AnalyzerFactory;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.persistence.ResultPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TrackAnalysisService {
    private static final Logger LOG = LoggerFactory.getLogger(TrackAnalysisService.class);

    private final AnalyzerFactory analyzerFactory;
    private final ResultPersistence resultPersistence;

    @Autowired
    public TrackAnalysisService(AnalyzerFactory analyzerFactory,
                                ResultPersistence resultPersistence) {
        this.analyzerFactory = Objects.requireNonNull(analyzerFactory);
        this.resultPersistence = Objects.requireNonNull(resultPersistence);
    }

    public void analyzeTrack(FeatureCollection featureCollection) {
        Analyzer analyzer = analyzerFactory.create(featureCollection);
        if (!analyzer.isApplicable()) {
            LOG.info("Skipping track {}, does not intersect with model.",
                     featureCollection.getProperties().path(JsonConstants.ID).textValue());
            return;
        }
        resultPersistence.persist(analyzer.analyze());
    }
}
