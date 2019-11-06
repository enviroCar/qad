package org.envirocar.qad;

import com.fasterxml.jackson.databind.JsonNode;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.Values;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TrackParserImpl implements TrackParser {
    private static final Logger LOG = LoggerFactory.getLogger(TrackParserImpl.class);
    public static final String PHENOMENON_SPEED = "Speed";
    public static final String PHENOMENON_CONSUMPTION = "Consumption";
    public static final String PHENOMENON_CARBON_DIOXIDE = "CO2";

    @Override
    public Track createTrack(FeatureCollection collection) throws TrackParsingException {

        String id = collection.getProperties().path(JsonConstants.ID).textValue();
        List<Measurement> measurements = new ArrayList<>();
        AtomicBoolean missingConsumption = new AtomicBoolean(false);
        AtomicBoolean missingEmission = new AtomicBoolean(false);
        for (Feature feature : collection.getFeatures()) {
            Point geometry = (Point) feature.getGeometry();
            String measurementId = feature.getProperties().path(JsonConstants.ID).textValue();
            Instant time = OffsetDateTime.parse(feature.getProperties().path(JsonConstants.TIME).textValue(),
                                                DateTimeFormatter.ISO_DATE_TIME).toInstant();
            JsonNode phenomenons = feature.getProperties().path(JsonConstants.PHENOMENONS);
            JsonNode speed = phenomenons.path(PHENOMENON_SPEED).path(JsonConstants.VALUE);
            JsonNode consumption = phenomenons.path(PHENOMENON_CONSUMPTION).path(JsonConstants.VALUE);
            JsonNode emission = phenomenons.path(PHENOMENON_CARBON_DIOXIDE).path(JsonConstants.VALUE);
            if (speed.isNull() || speed.isMissingNode()) {
                throw new TrackParsingException(String.format("track %s is missing speed measurements", measurementId));
            }
            if (consumption.isNull() || consumption.isMissingNode()) {
                missingConsumption.lazySet(true);

            }
            if (emission.isNull() || emission.isMissingNode()) {
                missingEmission.lazySet(true);
            }
            measurements.add(new Measurement(measurementId, geometry, time,
                                             new Values(speed.doubleValue(),
                                                        consumption.doubleValue(),
                                                        emission.doubleValue())));
        }

        if (missingConsumption.get()) {
            LOG.warn("track {} is missing consumption values", id);
        }

        if (missingEmission.get()) {
            LOG.warn("track {} is missing emission values", id);
        }

        String fuelType = collection.getProperties().path(JsonConstants.SENSOR).path(JsonConstants.PROPERTIES)
                                    .path(JsonConstants.FUEL_TYPE).textValue();

        return new Track(id, fuelType, measurements);
    }

}
