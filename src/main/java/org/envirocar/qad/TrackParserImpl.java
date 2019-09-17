package org.envirocar.qad;

import com.fasterxml.jackson.databind.JsonNode;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.Values;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class TrackParserImpl implements TrackParser {
    private static final String PHENOMENON_SPEED = "Speed";
    private static final String PHENOMENON_CONSUMPTION = "Consumption";
    private static final String PHENOMENON_CARBON_DIOXIDE = "CO2";

    @Override
    public Track createTrack(FeatureCollection collection) {

        String id = collection.getProperties().path(JsonConstants.ID).textValue();
        List<Measurement> measurements = collection.getFeatures().stream()
                                                   .map(this::createMeasurement)
                                                   .collect(toList());
        return new Track(id, measurements);
    }

    private Measurement createMeasurement(Feature feature) {
        Point geometry = (Point) feature.getGeometry();
        String id = feature.getProperties().path(JsonConstants.ID).textValue();
        Instant time = OffsetDateTime.parse(feature.getProperties().path(JsonConstants.TIME).textValue(),
                                            DateTimeFormatter.ISO_DATE_TIME).toInstant();
        JsonNode phenomenons = feature.getProperties().path(JsonConstants.PHENOMENONS);
        BigDecimal speed = phenomenons.get(PHENOMENON_SPEED).path(JsonConstants.VALUE).decimalValue();
        BigDecimal consumption = phenomenons.get(PHENOMENON_CONSUMPTION).path(JsonConstants.VALUE).decimalValue();
        BigDecimal carbonDioxide = phenomenons.get(PHENOMENON_CARBON_DIOXIDE).path(JsonConstants.VALUE).decimalValue();
        return new Measurement(id, geometry, time, new Values(speed, consumption, carbonDioxide));
    }

}
