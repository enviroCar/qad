package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.utils.DecimalPlaces;

import java.time.Duration;

public class SegmentStatistics {
    private Duration travelTime;
    private int stops;
    private Duration stoppedTime;
    private double consumption;
    private double emission;
    private double speed;

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

    @JsonProperty(JsonConstants.STOPPED_TIME)
    public Duration getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(Duration stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonProperty(JsonConstants.CONSUMPTION)
    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
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

}
