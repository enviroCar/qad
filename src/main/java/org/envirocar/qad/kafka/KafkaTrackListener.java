package org.envirocar.qad.kafka;

import org.envirocar.qad.TrackAnalysisService;
import org.envirocar.qad.TrackProperties;
import org.envirocar.qad.model.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaTrackListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTrackListener.class);

    private final TrackAnalysisService service;

    @Autowired
    public KafkaTrackListener(TrackAnalysisService service) {
        this.service = service;
    }

    @KafkaListener(topics = "tracks")
    public void onNewTrack(FeatureCollection track) {
        String id = track.getProperties().path(TrackProperties.ID).textValue();
        LOG.info("Received track {}", id);
        service.analyzeTrack(track);
    }
}
