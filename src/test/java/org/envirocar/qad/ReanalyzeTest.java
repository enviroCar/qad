package org.envirocar.qad;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.reanalyze.ReanalyzeDirectoryResultPersistence;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"qad.outputPath=/home/autermann/qad",
                              "qad.archive=false",
                              "logging.file.name=/home/autermann/qad.log"})
@Import(Application.class)
@ActiveProfiles("test")
public class ReanalyzeTest {
    private static final Logger LOG = LoggerFactory.getLogger(ReanalyzeTest.class);
    @Autowired
    private EnviroCarApi api;
    @Autowired
    private TrackAnalysisService service;

    @Configuration
    public static class TestConfiguration {
        @Primary
        @Bean
        public ReanalyzeDirectoryResultPersistence reanalyzeDirectoryResultPersistence(ObjectReader reader,
                                                                                       ObjectWriter writer,
                                                                                       QADParameters parameters) {
            return new ReanalyzeDirectoryResultPersistence(reader, writer, parameters);
        }
    }

    @Test
    public void test() {
        String[] ids = {"5f10291300375c5a26446fe7",
                        "5f17e487d2ad470001e2adb0",
                        "5f17e860d2ad470001e4118b",
                        "5f17eb64d2ad470001e4ad25",
                        "5f17ec56d2ad470001e4df6a",
                        "5f17eebad2ad470001e54d3a",
                        "5f17efe2d2ad470001e5a2af",
                        "5f17f1b5d2ad470001e5ff3e",
                        "5f17f359d2ad470001e6658e",
                        "5f17f4f2d2ad470001e6e1e7",
                        "5f17f806d2ad470001e7352b",
                        "5f17ff96d2ad470001e86ca5",
                        "5f180024d2ad470001e8e22a"};
        Arrays.stream(ids).map(this.api::fetchTrack).forEach(track -> {
            String id = track.getProperties().path(JsonConstants.ID).textValue();
            try {
                LOG.info("Received track {}", id);
                this.service.analyzeTrack(track);
            } catch (Exception ex) {
                LOG.error("Failed to analyze track " + id + ": " + ex.getMessage(), ex);
            }
        });
    }
}
