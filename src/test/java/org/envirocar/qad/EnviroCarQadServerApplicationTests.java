package org.envirocar.qad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.analyzer.Analyzer;
import org.envirocar.qad.analyzer.AnalyzerFactory;
import org.envirocar.qad.analyzer.TrackPreparer;
import org.envirocar.qad.axis.Axis;
import org.envirocar.qad.axis.AxisModelRepository;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.result.AnalysisResult;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnviroCarQadServerApplicationTests {
    private static final Logger LOG = LoggerFactory.getLogger(EnviroCarQadServerApplicationTests.class);
    @Autowired
    private AnalyzerFactory analyzerFactory;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TrackPreparer trackPreparer;
    @Autowired
    private AxisModelRepository axisModelRepository;

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
    public void test_5e5d226377e02d42aa9350a0() throws IOException {
        FeatureCollection fc = readFeatureCollection("/tracks/5e5d226377e02d42aa934f4f.json");
        Axis axis = axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("01_2")).get();
        Track track = trackPreparer.prepare(fc);

        //writeTrack(track, "/home/autermann/Source/enviroCar/qad/src/test/resources/tracks/5e5d226377e02d42aa934f4f.processed.json");

        final Analyzer analyzer = analyzerFactory.create(axis, track);
        final List<AnalysisResult> collect = analyzer.analyze().collect(toList());
        assertThat(collect.size(), is(1));
    }

    @Test
    public void test_5e5620e777e02d42aa8e1153() throws IOException {
        FeatureCollection fc = readFeatureCollection("/tracks/5e5620e777e02d42aa8e1153.json");
        Axis axis = axisModelRepository.getAxisModel("HAM").flatMap(x -> x.getAxis("01_1")).get();
        Track track = trackPreparer.prepare(fc);

        //writeTrack(track, "/home/autermann/Source/enviroCar/qad/src/test/resources/tracks/5e5620e777e02d42aa8e1153.processed.json");

        final Analyzer analyzer = analyzerFactory.create(axis, track);
        analyzer.analyze().map(this::toJSON)
                .forEach(System.err::println);
    }

    @Test
    public void test_5e4132753965f36894e62148() throws IOException {
        FeatureCollection fc = readFeatureCollection("/tracks/5e4132753965f36894e62148.json");
        Axis axis = axisModelRepository.getAxisModel("CHE").flatMap(x -> x.getAxis("22_2")).get();
        Track track = trackPreparer.prepare(fc);

        //writeTrack(track, "/home/autermann/Source/enviroCar/qad/src/test/resources/tracks/5e4132753965f36894e62148.processed.json");

        final Analyzer analyzer = analyzerFactory.create(axis, track);
        analyzer.analyze().map(this::toJSON)
                .forEach(System.err::println);
    }

    private void writeTrack(Track track, String path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            FeatureCollection featureCollection = new FeatureCollection();
             List<Feature> features = new ArrayList<>(track.size());
            for (int idx=0; idx<track.size(); idx++) {
                Feature feature = new Feature();
                final Measurement x = track.getMeasurement(idx);
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

    @Test
    public void contextLoads() throws IOException {
        //getResourceFiles("/tracks")
        Stream.of("/tracks/5d138cb844ea855023b210ab.json")
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

    private FeatureCollection readFeatureCollection(String resource) {
        try {
            return objectMapper.readValue(getClass().getResourceAsStream(resource), FeatureCollection.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
