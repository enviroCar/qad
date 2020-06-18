package org.envirocar.qad.analyzer;

import org.envirocar.qad.axis.AxisModel;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.result.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

public class AxisModelAnalyzer implements Analyzer {
    private static final Logger LOG = LoggerFactory.getLogger(AxisModelAnalyzer.class);
    private final AnalyzerFactory analyzerFactory;
    private final AxisModel model;
    private final Track track;

    public AxisModelAnalyzer(AnalyzerFactory analyzerFactory, AxisModel model, Track track) {
        this.analyzerFactory = Objects.requireNonNull(analyzerFactory);
        this.model = Objects.requireNonNull(model);
        this.track = Objects.requireNonNull(track);
    }

    @Override
    public boolean isApplicable() {
        return this.model.getEnvelope().intersects(this.track.getEnvelope());
    }

    @Override
    public Stream<AnalysisResult> analyze() {
        LOG.debug("Analyzing track {} in regard to model {}", this.track.getId(), this.model.getId());

        return this.track.subset(this.model.getEnvelope())
                         .flatMap(track -> this.model.getAxis().stream()
                                                     .map(axis -> this.analyzerFactory.create(axis, track))
                                                     .filter(Analyzer::isApplicable)
                                                     .flatMap(Analyzer::analyze));

    }
}
