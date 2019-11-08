package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.axis.AxisId;

import java.time.Instant;
import java.util.List;

public class AnalysisResult {
    private String city;
    private AxisId axis;
    private Instant start;
    private Instant end;
    private String fuelType;
    private List<SegmentResult> segments;

    @JsonProperty(JsonConstants.SEGMENTS)
    public List<SegmentResult> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentResult> segments) {
        this.segments = segments;
    }

    @JsonProperty(JsonConstants.CITY)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty(JsonConstants.AXIS)
    public AxisId getAxis() {
        return axis;
    }

    public void setAxis(AxisId axis) {
        this.axis = axis;
    }

    @JsonProperty(JsonConstants.START)
    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    @JsonProperty(JsonConstants.END)
    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    @JsonProperty(JsonConstants.FUEL_TYPE)
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}
