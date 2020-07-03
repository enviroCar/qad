package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.utils.DecimalPlaces;
import org.envirocar.qad.utils.DurationSerializer;

import java.time.Duration;

public class SegmentStatistics {
    @JsonSerialize(using = DurationSerializer.class)
    @JsonProperty(JsonConstants.TRAVEL_TIME)
    private Duration travelTime;
    @JsonProperty(JsonConstants.STOPS)
    private int stops;
    @JsonSerialize(using = DurationSerializer.class)
    @JsonProperty(JsonConstants.STOPPED_TIME)
    private Duration stoppedTime;
    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.FUEL_CONSUMPTION)
    private double fuelConsumption;
    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.ENERGY_CONSUMPTION)
    private double energyConsumption;
    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.EMISSION)
    private double emission;
    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.SPEED)
    private double speed;

    public Duration getTravelTime() {
        return this.travelTime;
    }

    public void setTravelTime(Duration travelTime) {
        this.travelTime = travelTime;
    }

    public int getStops() {
        return this.stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public Duration getStoppedTime() {
        return this.stoppedTime;
    }

    public void setStoppedTime(Duration stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    public double getFuelConsumption() {
        return this.fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public double getEnergyConsumption() {
        return this.energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public double getEmission() {
        return this.emission;
    }

    public void setEmission(double emission) {
        this.emission = emission;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
