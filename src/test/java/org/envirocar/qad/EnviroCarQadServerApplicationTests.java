package org.envirocar.qad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.analyzer.Analyzer;
import org.envirocar.qad.analyzer.AnalyzerFactory;
import org.envirocar.qad.model.FeatureCollection;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnviroCarQadServerApplicationTests {
    private static final Logger LOG = LoggerFactory.getLogger(EnviroCarQadServerApplicationTests.class);
    @Autowired
    private AnalyzerFactory analyzerFactory;
    @Autowired
    private ObjectMapper objectMapper;

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
    public void contextLoads() throws IOException {
        //getResourceFiles("/tracks")
        Stream.of("/tracks/5d138cb844ea855023b210ab.json")
              .map(this::readFeatureCollection)
              .map(analyzerFactory::create)
              .filter(Analyzer::isApplicable)
              .flatMap(Analyzer::analyze)
              .map(this::toJSON)
              .forEach(System.err::println);
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
