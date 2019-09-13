package org.envirocar.qad;

import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.axis.AxisModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TrackAnalysisService {
    private static final Logger LOG = LoggerFactory.getLogger(TrackAnalysisService.class);
    private final AxisModels axisModels;

    @Autowired
    public TrackAnalysisService(AxisModels axisModels) {
        this.axisModels = Objects.requireNonNull(axisModels);
    }

    public void analyzeTrack(FeatureCollection track) {
        if (!axisModels.getEnvelope().intersects(track.getEnvelope())) {
            LOG.info("Skipping track {}, does not intersect with model.",
                     track.getProperties().path(TrackProperties.ID).textValue());
            return;
        }


    }

}
