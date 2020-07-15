package org.envirocar.qad;

import com.fasterxml.jackson.databind.JsonNode;
import org.envirocar.qad.model.Feature;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.Values;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class TrackParserImpl implements TrackParser {
    private static final Logger LOG = LoggerFactory.getLogger(TrackParserImpl.class);
    private static final String PHENOMENON_SPEED = "Speed";
    private static final String PHENOMENON_CONSUMPTION = "Consumption";
    private static final String PHENOMENON_CARBON_DIOXIDE = "CO2";
    private static final String PHENOMENON_ENERGY_CONSUMPTION = "Energy Consumption";
    private static final String PHENOMENON_GPS_SPEED = "GPS Speed";
    private static final String PHENOMENON_CARBON_DIOXIDE_GPS = "CO2 Emission (GPS-based)";
    private static final String PHENOMENON_CONSUMPTION_GPS = "Consumption (GPS-based)";

    @Override
    public Track createTrack(FeatureCollection collection) throws TrackParsingException {
        return new ParseContext(collection).createTrack();
    }

    private static class ParseContext {
        private final FeatureCollection featureCollection;
        private final boolean hasNonZeroSpeedValues;
        private boolean missingFuelConsumption;
        private boolean missingEmission;
        private boolean missingEnergyConsumption;

        ParseContext(FeatureCollection featureCollection) {
            this.featureCollection = featureCollection;
            this.hasNonZeroSpeedValues = hasNonZeroSpeedValues();
        }

        private void missingFuelConsumption() {
            this.missingFuelConsumption = true;
        }

        private void missingEmission() {
            this.missingEmission = true;
        }

        private void missingEnergyConsumption() {
            this.missingEnergyConsumption = true;
        }

        private boolean hasMissingConsumption() {
            return this.missingFuelConsumption && this.missingEnergyConsumption;
        }

        private boolean hasMissingEmission() {
            return this.missingEmission;
        }

        private boolean hasNonZeroSpeedValues() {
            return this.featureCollection.getFeatures().stream()
                                         .map(this::getSpeed)
                                         .filter(OptionalDouble::isPresent)
                                         .mapToDouble(OptionalDouble::getAsDouble)
                                         .filter(s -> s != 0.0d)
                                         .findAny()
                                         .isPresent();
        }

        private OptionalDouble getSpeed(Feature feature) {
            return getPhenomenon(feature, PHENOMENON_SPEED);
        }

        @NotNull
        private OptionalDouble optionalDoubleValue(JsonNode node) {
            if (node.isNumber()) {
                return OptionalDouble.of(node.doubleValue());
            } else {
                return OptionalDouble.empty();
            }
        }

        private String getFuelType() {
            return this.featureCollection.getProperties()
                                         .path(JsonConstants.SENSOR)
                                         .path(JsonConstants.PROPERTIES)
                                         .path(JsonConstants.FUEL_TYPE)
                                         .textValue();
        }

        Track createTrack() {
            String id = getId();
            List<Measurement> measurements = this.featureCollection.getFeatures().stream()
                                                                   .map(this::createMeasurement)
                                                                   .collect(Collectors.toList());

            if (hasMissingConsumption()) {
                LOG.warn("track {} is missing consumption values", id);
            }

            if (hasMissingEmission()) {
                LOG.warn("track {} is missing emission values", id);
            }

            return new Track(id, getFuelType(), measurements);
        }

        private Measurement createMeasurement(Feature feature) {
            return new Measurement(getId(feature), getGeometry(feature), getTime(feature), getValues(feature));
        }

        private Values getValues(Feature feature) {
            OptionalDouble fuelConsumption = getFuelConsumption(feature);
            OptionalDouble emission = getEmission(feature);
            OptionalDouble energyConsumption = getEnergyConsumption(feature);

            if (!fuelConsumption.isPresent()) {
                missingFuelConsumption();
            }
            if (!energyConsumption.isPresent()) {
                missingEnergyConsumption();
            }
            if (!emission.isPresent()) {
                missingEmission();
            }
            OptionalDouble speed;
            if (this.hasNonZeroSpeedValues) {
                speed = getPhenomenon(feature, PHENOMENON_SPEED);
                if (!speed.isPresent()) {
                    speed = getPhenomenon(feature, PHENOMENON_GPS_SPEED);
                }
            } else {
                speed = getPhenomenon(feature, PHENOMENON_GPS_SPEED);
                if (!speed.isPresent()) {
                    speed = getPhenomenon(feature, PHENOMENON_SPEED);
                }
            }
            return new Values(speed.orElseThrow(() -> missingSpeedValue(feature)),
                              fuelConsumption, energyConsumption, emission);
        }

        private TrackParsingException missingSpeedValue(Feature feature) {
            return new TrackParsingException(String.format("track %s is missing speed measurements", getId(feature)));
        }

        private Point getGeometry(Feature feature) {
            return (Point) feature.getGeometry();
        }

        private Instant getTime(Feature feature) {
            return OffsetDateTime.parse(feature.getProperties().path(JsonConstants.TIME).textValue(),
                                        DateTimeFormatter.ISO_DATE_TIME).toInstant();
        }

        private OptionalDouble getEnergyConsumption(Feature feature) {
            return getPhenomenon(feature, PHENOMENON_ENERGY_CONSUMPTION);
        }

        private OptionalDouble getFuelConsumption(Feature feature) {
            OptionalDouble consumption = getPhenomenon(feature, PHENOMENON_CONSUMPTION);
            if (!consumption.isPresent()) {
                return getPhenomenon(feature, PHENOMENON_CONSUMPTION_GPS);
            }
            return consumption;
        }

        private OptionalDouble getEmission(Feature feature) {
            OptionalDouble emission = getPhenomenon(feature, PHENOMENON_CARBON_DIOXIDE);
            if (!emission.isPresent()) {
                return getPhenomenon(feature, PHENOMENON_CARBON_DIOXIDE_GPS);
            }
            return emission;
        }

        private OptionalDouble getPhenomenon(Feature feature, String phenomenon) {
            return optionalDoubleValue(feature.getProperties()
                                              .path(JsonConstants.PHENOMENONS)
                                              .path(phenomenon)
                                              .path(JsonConstants.VALUE));
        }

        private String getId() {
            return this.featureCollection.getProperties().path(JsonConstants.ID).textValue();
        }

        private String getId(Feature feature) {
            return feature.getProperties().path(JsonConstants.ID).textValue();
        }

    }
}
