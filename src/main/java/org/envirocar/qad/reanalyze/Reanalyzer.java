package org.envirocar.qad.reanalyze;

import org.envirocar.qad.EnviroCarApi;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.TrackAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@Profile(ReanalyzeApplication.REANALYZE_PROFILE)
public class Reanalyzer implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(Reanalyzer.class);
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
    public void run(String... args) {
        reanalyze();
    }
}
