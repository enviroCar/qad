package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.envirocar.qad.JsonConstants;

import java.time.OffsetDateTime;
import java.util.List;

public class AnalysisResult {
    private String city;
    private int axis;
    private int direction;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String fuelType;
    private List<SegmentResult> segments;

    @JsonGetter(JsonConstants.SEGMENTS)
    public List<SegmentResult> getSegments() {
        return segments;
    }

    @JsonSetter(JsonConstants.SEGMENTS)
    public void setSegments(List<SegmentResult> segments) {
        this.segments = segments;
    }

    @JsonGetter(JsonConstants.TOWN)
    public String getCity() {
        return city;
    }

    @JsonSetter(JsonConstants.TOWN)
    public void setCity(String city) {
        this.city = city;
    }

    @JsonGetter(JsonConstants.AXIS)
    public int getAxis() {
        return axis;
    }

    @JsonSetter(JsonConstants.AXIS)
    public void setAxis(int axis) {
        this.axis = axis;
    }

    @JsonGetter(JsonConstants.DIRECTION)
    public int getDirection() {
        return direction;
    }

    @JsonSetter(JsonConstants.DIRECTION)
    public void setDirection(int direction) {
        this.direction = direction;
    }

    @JsonGetter(JsonConstants.START)
    public OffsetDateTime getStart() {
        return start;
    }

    @JsonSetter(JsonConstants.START)
    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    @JsonGetter(JsonConstants.END)
    public OffsetDateTime getEnd() {
        return end;
    }

    @JsonSetter(JsonConstants.END)
    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    @JsonGetter(JsonConstants.FUEL_TYPE)
    public String getFuelType() {
        return fuelType;
    }

    @JsonSetter(JsonConstants.FUEL_TYPE)
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}
