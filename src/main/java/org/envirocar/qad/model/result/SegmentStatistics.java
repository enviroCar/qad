package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.utils.BigDecimals;
import org.envirocar.qad.utils.DecimalPlaces;

import java.io.IOException;
import java.math.RoundingMode;
import java.time.Duration;

public class SegmentStatistics {
    private Duration travelTime;
    private int stops;
    private Duration stoppedTime;
    private double fuelConsumption;
    private double energyConsumption;
    private double emission;
    private double speed;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonProperty(JsonConstants.TRAVEL_TIME)
    public Duration getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Duration travelTime) {
        this.travelTime = travelTime;
    }

    @JsonProperty(JsonConstants.STOPS)
    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    @JsonSerialize(using = DurationSerializer.class)
    @JsonProperty(JsonConstants.STOPPED_TIME)
    public Duration getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(Duration stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.FUEL_CONSUMPTION)
    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.ENERGY_CONSUMPTION)
    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.EMISSION)
    public double getEmission() {
        return emission;
    }

    public void setEmission(double emission) {
        this.emission = emission;
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.SPEED)
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    private static class DurationSerializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeNumber(BigDecimals.create(value).setScale(3, RoundingMode.HALF_UP));
            }
        }
    }
}
