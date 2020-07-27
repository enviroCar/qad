package org.envirocar.qad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.analyzer.AnalyzerFactory;
import org.envirocar.qad.axis.Axis;
import org.envirocar.qad.axis.AxisModel;
import org.envirocar.qad.axis.AxisModelRepository;
import org.envirocar.qad.mapmatching.MapMatcher;
import org.envirocar.qad.mapmatching.MapMatchingException;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.result.AnalysisResult;
import org.envirocar.qad.persistence.DirectoryResultPersistence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnviroCarQadServerApplicationTests {
    private static final Logger LOG = LoggerFactory.getLogger(EnviroCarQadServerApplicationTests.class);
    private static final Path PATH = Paths.get("src/test/resources/tracks");
    @Autowired
    private AnalyzerFactory analyzerFactory;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AxisModelRepository axisModelRepository;
    @Autowired
    private TrackParser trackParser;
    @Autowired
    private TrackDensifier densifier;
    @Autowired
    private Optional<MapMatcher> mapMatcher;
    @Autowired
    private Optional<TrackSplitter> trackSplitter;
    @Autowired
    private EnviroCarApi api;
    @Value("${qad.tests.write:false}")
    private boolean write;
    @Autowired
    private DirectoryResultPersistence directoryResultPersistence;

    @Test
    public void test_5e5f7a8e77e02d42aa95b384() {
        AxisModel axis = this.axisModelRepository.getAxisModel("CHE").orElseThrow(IllegalStateException::new);
        Stream<Track> track = writeAndPrepare("5e5f7a8e77e02d42aa95b384");

        List<AnalysisResult> collect = track.flatMap(t -> this.analyzerFactory.create(axis, t).analyze())
                                            .collect(toList());

        for (AnalysisResult result : collect) {
            this.directoryResultPersistence.persist(result);
        }
        assertThat(collect.size(), is(4));

    }

    @Test
    public void test_5e5d226377e02d42aa9350a0() {
        Axis axis = this.axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("01_2")).get();
        Stream<Track> track = writeAndPrepare("5e5d226377e02d42aa934f4f");
        List<AnalysisResult> collect = track.flatMap(t -> this.analyzerFactory.create(axis, t).analyze())
                                            .collect(toList());
        assertThat(collect.size(), is(1));
    }

    @Test
    public void test_5e5620e777e02d42aa8e1153() {
        Axis axis = this.axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("01_1")).get();
        Stream<Track> track = writeAndPrepare("5e5620e777e02d42aa8e1153");
        track.flatMap(t -> this.analyzerFactory.create(axis, t).analyze())
             .map(this::toJSON)
             .forEach(System.err::println);
    }

    @Test
    public void test_5f0efcf900375c5a264231c2() throws IOException {
        FeatureCollection featureCollection = this.api.fetchTrack("5f0efcf900375c5a264231c2");
        Stream<Track> track = writeAndPrepare(featureCollection);
        track.flatMap(t ->
                              this.axisModelRepository.getAxisModels().stream()
                                                      .flatMap(model -> this.analyzerFactory.create(model, t)
                                                                                            .analyze()))
             .map(this::toJSON)
             .forEach(System.err::println);

    }

    @Test
    public void test_this_week() {
        String[] tracks = {"5f118496d2ad470001b7f734",
                           "5f11584900375c5a26498817",
                           "5f11501b00375c5a264952cc",
                           "5f11501b00375c5a26495236",
                           "5f114c5f00375c5a26493f05",
                           "5f1134be00375c5a26491d82",
                           "5f10a2de00375c5a2648d6ca",
                           "5f10a2dd00375c5a2648d318",
                           "5f0f222900375c5a26426383",
                           "5f0efcf900375c5a264231c2",
                           "5f0ed9b7d2ad470001a6cf1e",
                           "5f0ed5f000375c5a2641736b",
                           "5f0ed5f000375c5a26417472",
                           "5f0ed5f000375c5a2641758e",
                           "5f0ed5f000375c5a264170b2",
                           "5f0ed5ef00375c5a26416a69",
                           "5f0ed5ef00375c5a2641697d",
                           "5f0ec00800375c5a263f14d6",
                           "5f0ebbe700375c5a263f0a91",
                           "5f0ebbe600375c5a263f0a3d",
                           "5f0eb97a00375c5a263f0539",
                           "5f0eb39030d93d341d58058d",
                           "5f0eae8630d93d341d54b31c",
                           "5f0ead9d30d93d341d54b132",
                           "5f0eac0a30d93d341d54b044",
                           "5f0eabc330d93d341d54203e",
                           "5f0ea3da30d93d341d540f4b",
                           "5f0dc94d30d93d341d472c53",
                           "5f0dc94d30d93d341d47283f",
                           "5f0dbba230d93d341d472236",
                           "5f0d9caa30d93d341d4445b2",
                           "5f0d9caa30d93d341d444545",
                           "5f0d874230d93d341d416693",
                           "5f0d874230d93d341d4165af"};
        for (String id : tracks) {
            try {
                FeatureCollection featureCollection = this.api.fetchTrack(id);
                Stream<Track> track = writeAndPrepare(featureCollection);
                track.flatMap(t ->
                                      this.axisModelRepository.getAxisModels().stream()
                                                              .flatMap(model -> this.analyzerFactory.create(model, t)
                                                                                                    .analyze()))
                     .map(this::toJSON)
                     .forEach(System.err::println);
            } catch (Exception e) {
                LOG.error("error analyzing track {}: {}", id, e.getMessage());
            }
        }
    }

    @Test
    public void test_5e4132753965f36894e62148() {
        Axis axis = this.axisModelRepository.getAxisModel("CHE").flatMap(x -> x.getAxis("22_2")).get();
        Stream<Track> track = writeAndPrepare("5e4132753965f36894e62148");
        track.flatMap(t -> this.analyzerFactory.create(axis, t).analyze())
             .map(this::toJSON)
             .forEach(System.err::println);
    }

    private void writeTrack(Track track, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            FeatureCollection featureCollection = new FeatureCollection();
            List<Feature> features = new ArrayList<>(track.size());
            for (int idx = 0; idx < track.size(); idx++) {
                Feature feature = new Feature();
                Measurement x = track.getMeasurement(idx);
                feature.setGeometry(x.getGeometry());
                feature.setId(x.getId());
                feature.setProperties(this.objectMapper.createObjectNode()
                                                       .putPOJO("time", x.getTime())
                                                       .put("speed", x.getValues().getSpeed())
                                                       .put("idx", idx));
                features.add(feature);
            }

            featureCollection.setFeatures(features);

            this.objectMapper.writeValue(writer, featureCollection);
        }
    }

    private Stream<Track> writeAndPrepare(String id) {
        return writeAndPrepare(this.api.fetchTrack(id));
    }

    private Stream<Track> writeAndPrepare(FeatureCollection featureCollection) {
        try {
            if (this.write) {
                writeTrack(this.trackParser.createTrack(featureCollection));
            }

            if (this.mapMatcher.isPresent()) {
                featureCollection = this.mapMatcher.get().mapMatch(featureCollection);
                if (this.write) {
                    writeTrack(this.trackParser.createTrack(featureCollection), "matched");
                }
            }
            featureCollection = this.densifier.densify(featureCollection);
            Track track = this.trackParser.createTrack(featureCollection);
            if (this.write) {
                writeTrack(track, "processed");
            }
            if (this.trackSplitter.isPresent()) {
                return this.trackSplitter.get().split(track);
            }
            return Stream.of(track);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (MapMatchingException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTrack(Track track) throws IOException {
        writeTrack(track, PATH.resolve(String.format("%s.json", track.getId())));
    }

    private void writeTrack(Track track, String classifier) throws IOException {
        writeTrack(track, PATH.resolve(String.format("%s.%s.json", track.getId(), classifier)));
    }

    private String toJSON(AnalysisResult result) {
        try {
            return this.objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

}
