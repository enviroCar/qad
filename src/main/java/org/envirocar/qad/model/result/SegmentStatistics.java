package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.envirocar.qad.JsonConstants;

import java.time.Duration;

public class SegmentStatistics {
    @JsonProperty(JsonConstants.TRAVEL_TIME)
    private Duration travelTime;
    @JsonProperty(JsonConstants.STOPS)
    private int stops;
    @JsonProperty(JsonConstants.STOPPED_TIME)
    private Duration stoppedTime;
    @JsonProperty(JsonConstants.CONSUMPTION)
    private double consumption;
    @JsonProperty(JsonConstants.EMISSION)
    private double emission;
    @JsonProperty(JsonConstants.SPEED)
    private double speed;
    private int begin;
    private int end;
    private String trackId;

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Duration getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Duration travelTime) {
        this.travelTime = travelTime;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public Duration getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(Duration stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getEmission() {
        return emission;
    }

    public void setEmission(double emission) {
        this.emission = emission;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
