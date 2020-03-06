package org.envirocar.qad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.analyzer.Analyzer;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private static final Path PATH = Paths.get("/home/autermann/Source/enviroCar/qad/src/test/resources/tracks");
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
    private DirectoryResultPersistence directoryResultPersistence;

    private Stream<String> getResourceFiles(String path) throws IOException {

        List<String> filenames = new ArrayList<>();
        try (InputStream in = getClass().getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(path + "/" + resource);
            }
        }
        return filenames.stream();
    }

    @Test
    public void test_5e5f7a8e77e02d42aa95b384() throws IOException, MapMatchingException {

        AxisModel axis = axisModelRepository.getAxisModel("CHE").orElseThrow(IllegalStateException::new);
        Stream<Track> track = writeAndPrepare("5e5f7a8e77e02d42aa95b384");

        List<AnalysisResult> collect = track.flatMap(t -> analyzerFactory.create(axis, t).analyze())
                                            .collect(toList());

        for (AnalysisResult result : collect) {
            directoryResultPersistence.persist(result);
        }
        assertThat(collect.size(), is(4));

    }

    @Test
    public void test_5e5d226377e02d42aa9350a0() throws IOException, MapMatchingException {
        Axis axis = axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("01_2")).get();
        Stream<Track> track = writeAndPrepare("5e5d226377e02d42aa934f4f");
        List<AnalysisResult> collect = track.flatMap(t -> analyzerFactory.create(axis, t).analyze())
                                            .collect(toList());
        assertThat(collect.size(), is(1));
    }

    @Test
    public void test_5e5620e777e02d42aa8e1153() throws IOException, MapMatchingException {
        Axis axis = axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("01_1")).get();
        Stream<Track> track = writeAndPrepare("5e5620e777e02d42aa8e1153");
        track.flatMap(t -> analyzerFactory.create(axis, t).analyze())
             .map(this::toJSON)
             .forEach(System.err::println);
    }

    @Test
    public void test_5e4132753965f36894e62148() throws IOException, MapMatchingException {
        Axis axis = axisModelRepository.getAxisModel("CHE").flatMap(x -> x.getAxis("22_2")).get();
        Stream<Track> track = writeAndPrepare("5e4132753965f36894e62148");
        track.flatMap(t -> analyzerFactory.create(axis, t).analyze())
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
                feature.setProperties(objectMapper.createObjectNode()
                                                  .putPOJO("time", x.getTime())
                                                  .put("idx", idx));
                features.add(feature);
            }

            featureCollection.setFeatures(features);

            objectMapper.writeValue(writer, featureCollection);
        }
    }

    private Stream<Track> writeAndPrepare(String id) throws MapMatchingException, IOException {
        FeatureCollection featureCollection = readFeatureCollection(id);

        if (mapMatcher.isPresent()) {
            featureCollection = this.mapMatcher.get().mapMatch(featureCollection);
            writeTrack(trackParser.createTrack(featureCollection), "matched");
        }
        featureCollection = densifier.densify(featureCollection);
        Track track = trackParser.createTrack(featureCollection);
        writeTrack(track, "processed");
        if (trackSplitter.isPresent()) {
            return trackSplitter.get().split(track);
        }
        return Stream.of(track);
    }

    private void writeTrack(Track track, String classifier) throws IOException {
        writeTrack(track, PATH.resolve(String.format("%s.%s.json", track.getId(), classifier)));
    }

    private FeatureCollection readFeatureCollection(String id) {
        return readFeatureCollection(PATH.resolve(String.format("%s.json", id)));
    }

    @Test
    public void contextLoads() throws IOException {
        //getResourceFiles("/tracks")
        Stream.of("5d138cb844ea855023b210ab")
              .map(x -> PATH.resolve(String.format("%s.json", x)))
              .map(this::readFeatureCollection)
              .map(analyzerFactory::create)
              .filter(Analyzer::isApplicable)
              .flatMap(Analyzer::analyze)
              .map(this::toJSON)
              .forEach(x -> {});
    }

    private String toJSON(AnalysisResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private FeatureCollection readFeatureCollection(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            return objectMapper.readValue(in, FeatureCollection.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
