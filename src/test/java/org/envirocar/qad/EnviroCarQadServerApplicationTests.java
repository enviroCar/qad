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
    public void test_5f03231cd2ad47000190f29e() {
        Axis axis = this.axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("04_02")).get();
        Stream<Track> track = writeAndPrepare("5f03231cd2ad47000190f29e");
        track.flatMap(t -> this.analyzerFactory.create(axis, t).analyze())
             .forEach(this.directoryResultPersistence::persist);
    }

    @Test
    public void test_5f0c67d0d2ad4700019729cd() {
        String[] tracks = {"5f0c0193d2ad47000194940c",
                           "5f0c2bdcd2ad470001959d90",
                           "5f0c3c2cd2ad470001960e9a",
                           "5f0c4211d2ad470001963522",
                           "5f0c53afd2ad47000196a402",
                           "5f0c67d0d2ad4700019729cd",
                           "5f0c6fcdd2ad470001976b7d"};

        AxisModel model = this.axisModelRepository.getAxisModel("KRE").get();
        Arrays.stream(tracks).flatMap(this::writeAndPrepare)
              .flatMap(t -> this.analyzerFactory.create(model, t).analyze())
              .forEach(this.directoryResultPersistence::persist);
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
    public void test_5f057fc3d2ad470001a0a7e5() {
        Axis axis = this.axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("04_02")).get();
        Stream<Track> track = writeAndPrepare("5f057fc3d2ad470001a0a7e5");
        track.flatMap(t -> this.analyzerFactory.create(axis, t).analyze())
             .map(this::toJSON)
             .forEach(System.err::println);
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
        try {
            return writeAndPrepare(this.api.fetchTrack(id));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
