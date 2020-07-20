package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.envirocar.qad.JsonConstants;

import java.time.Instant;

public class TrackSummary {
    @JsonProperty(JsonConstants.ID)
    private String id;
    @JsonProperty(JsonConstants.LENGTH)
    private double length;
    @JsonProperty(JsonConstants.BEGIN)
    private Instant begin;
    @JsonProperty(JsonConstants.END)
    private Instant end;
    @JsonProperty(JsonConstants.SENSOR)
    private Sensor sensor;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Instant getBegin() {
        return this.begin;
    }

    public void setBegin(Instant begin) {
        this.begin = begin;
    }

    public Instant getEnd() {
        return this.end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
