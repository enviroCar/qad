package org.envirocar.qad.reanalyze;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.EnviroCarApi;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.TrackAnalysisService;
import org.envirocar.qad.model.result.AnalysisResult;
import org.envirocar.qad.persistence.DirectoryResultPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "org.envirocar.qad")
@EnableConfigurationProperties({AlgorithmParameters.class})
public class ReanalyzeApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ReanalyzeApplication.class);
    public static final String REANALYZE_PROFILE = "reanalyze";

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", REANALYZE_PROFILE);
        SpringApplication.run(ReanalyzeApplication.class, args);
    }

    @Component
    @Profile(REANALYZE_PROFILE)
    static class Reanalyzer implements CommandLineRunner {
        private String begin;
        private String end;
        private EnviroCarApi api;
        private TrackAnalysisService service;

        @Autowired
        public void setApi(EnviroCarApi api) {
            this.api = api;
        }

        @Autowired
        public void setService(TrackAnalysisService service) {
            this.service = service;
        }

        @Value("${qad.reanalyze.begin}")
        public void setBegin(String begin) {
            this.begin = begin;
        }

        @Value("${qad.reanalyze.end}")
        public void setEnd(String end) {
            this.end = end;
        }

        private void reanalyze() {
            Map<String, String> query = Collections
                                                .singletonMap("during", String.format("%s,%s", this.begin, this.end));
            this.api.fetchTracks(query).forEach(track -> {
                String id = track.getProperties().path(JsonConstants.ID).textValue();
                try {
                    LOG.info("Received track {}", id);
                    this.service.analyzeTrack(track);
                } catch (Exception ex) {
                    LOG.error("Failed to analyze track " + id + ": " + ex.getMessage(), ex);
                }
            });
        }

        @Override
        public void run(String... args) throws Exception {
            reanalyze();
        }
    }

    @Primary
    @Service
    @Profile(REANALYZE_PROFILE)
    static class MyDirectoryResultPersistence extends DirectoryResultPersistence {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", LOCALE)
                                                                            .withZone(ZONE_ID);

        MyDirectoryResultPersistence(ObjectReader reader, ObjectWriter writer, AlgorithmParameters parameters) {
            super(reader, writer, parameters);
        }

        @Override
        protected Path getDirectory(AnalysisResult result) {
            return result == null ? getParameters().getOutputPath()
                                  : getParameters().getOutputPath().resolve(FORMATTER.format(result.getStart()));
        }
    }
}
