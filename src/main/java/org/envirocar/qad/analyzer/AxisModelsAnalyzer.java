package org.envirocar.qad.analyzer;

import org.envirocar.qad.TrackSplitter;
import org.envirocar.qad.axis.AxisModelRepository;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.result.AnalysisResult;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class AxisModelsAnalyzer implements Analyzer {
    private final AnalyzerFactory analyzerFactory;
    private final AxisModelRepository repository;
    private final FeatureCollection featureCollection;
    private final TrackPreparer preparer;
    private final TrackSplitter trackSplitter;

    public AxisModelsAnalyzer(AnalyzerFactory analyzerFactory, AxisModelRepository repository,
                              TrackPreparer preparer, TrackSplitter trackSplitter,
                              FeatureCollection featureCollection) {
        this.analyzerFactory = Objects.requireNonNull(analyzerFactory);
        this.repository = Objects.requireNonNull(repository);
        this.preparer = Objects.requireNonNull(preparer);
        this.featureCollection = Objects.requireNonNull(featureCollection);
        this.trackSplitter = trackSplitter;
    }

    @Override
    public boolean isApplicable() {
        return repository.getEnvelope().intersects(featureCollection.getEnvelope());
    }

    @Override
    public Stream<AnalysisResult> analyze() throws AnalysisException {
        Track track = preparer.prepare(featureCollection);
        return split(track).flatMap(t -> repository.getAxisModels()
                                                   .stream()
                                                   .map(model -> analyzerFactory.create(model, t))
                                                   .filter(Analyzer::isApplicable)
                                                   .flatMap(Analyzer::analyze));

    }

    private Stream<Track> split(Track track) {
        return Optional.ofNullable(trackSplitter).<Function<Track, Stream<Track>>>map(s -> s::split)
                       .orElse(Stream::of)
                       .apply(track);
    }

}
