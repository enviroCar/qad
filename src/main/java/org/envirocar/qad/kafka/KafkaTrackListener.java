package org.envirocar.qad.kafka;

import org.envirocar.qad.TrackAnalysisService;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.model.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(KafkaListenerAnnotationBeanPostProcessor.class)
public class KafkaTrackListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTrackListener.class);

    private final TrackAnalysisService service;

    @Autowired
    public KafkaTrackListener(TrackAnalysisService service) {
        this.service = service;
    }

    @KafkaListener(topics = "tracks")
    public void onNewTrack(FeatureCollection track) {
        String id = track.getProperties().path(JsonConstants.ID).textValue();
        LOG.info("Received track {}", id);
        this.service.analyzeTrack(track);
    }
}
