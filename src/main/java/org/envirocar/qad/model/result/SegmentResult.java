package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.envirocar.qad.JsonConstants;

import java.math.BigDecimal;
import java.time.Duration;

public class SegmentResult {
    private int segmentId;
    private double segmentLength;
    private boolean trafficLightInfluence;
    private String trafficLightId;
    private Duration travelTime;
    private int stops;
    private Duration stoppedTime;
    private BigDecimal consumption;
    private BigDecimal emission;
    private BigDecimal speed;

    @JsonGetter(JsonConstants.SEGMENT_ID)
    public int getSegmentId() {
        return segmentId;
    }

    @JsonSetter(JsonConstants.SEGMENT_ID)
    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    @JsonGetter(JsonConstants.SEGMENT_LENGTH)
    public double getSegmentLength() {
        return segmentLength;
    }

    @JsonSetter(JsonConstants.SEGMENT_LENGTH)
    public void setSegmentLength(double segmentLength) {
        this.segmentLength = segmentLength;
    }

    @JsonGetter(JsonConstants.TRAFFIC_LIGHT_INFLUENCE)
    public boolean isTrafficLightInfluence() {
        return trafficLightInfluence;
    }

    @JsonSetter(JsonConstants.TRAFFIC_LIGHT_INFLUENCE)
    public void setTrafficLightInfluence(boolean trafficLightInfluence) {
        this.trafficLightInfluence = trafficLightInfluence;
    }

    @JsonGetter(JsonConstants.TRAFFIC_LIGHT_ID)
    public String getTrafficLightId() {
        return trafficLightId;
    }

    @JsonSetter(JsonConstants.TRAFFIC_LIGHT_ID)
    public void setTrafficLightId(String trafficLightId) {
        this.trafficLightId = trafficLightId;
    }

    @JsonGetter(JsonConstants.TRAVEL_TIME)
    public Duration getTravelTime() {
        return travelTime;
    }

    @JsonSetter(JsonConstants.TRAVEL_TIME)
    public void setTravelTime(Duration travelTime) {
        this.travelTime = travelTime;
    }

    @JsonGetter(JsonConstants.STOPS)
    public int getStops() {
        return stops;
    }

    @JsonSetter(JsonConstants.STOPS)
    public void setStops(int stops) {
        this.stops = stops;
    }

    @JsonGetter(JsonConstants.STOPPED_TIME)
    public Duration getStoppedTime() {
        return stoppedTime;
    }

    @JsonSetter(JsonConstants.STOPPED_TIME)
    public void setStoppedTime(Duration stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    @JsonGetter(JsonConstants.CONSUMPTION)
    public BigDecimal getConsumption() {
        return consumption;
    }

    @JsonSetter(JsonConstants.CONSUMPTION)
    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }

    @JsonGetter(JsonConstants.EMISSION)
    public BigDecimal getEmission() {
        return emission;
    }

    @JsonSetter(JsonConstants.EMISSION)
    public void setEmission(BigDecimal emission) {
        this.emission = emission;
    }

    @JsonGetter(JsonConstants.SPEED)
    public BigDecimal getSpeed() {
        return speed;
    }

    @JsonSetter(JsonConstants.SPEED)
    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }
}
