package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.axis.AxisId;

import java.time.Instant;
import java.util.List;

public class AnalysisResult {
    @JsonProperty(JsonConstants.CITY)
    private String city;
    @JsonProperty(JsonConstants.AXIS)
    private AxisId axis;
    @JsonProperty(JsonConstants.START)
    private Instant start;
    @JsonProperty(JsonConstants.END)
    private Instant end;
    @JsonProperty(JsonConstants.FUEL_TYPE)
    private String fuelType;
    @JsonProperty(JsonConstants.SEGMENTS)
    private List<SegmentResult> segments;
    @JsonIgnore
    private String trackId;

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public List<SegmentResult> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentResult> segments) {
        this.segments = segments;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public AxisId getAxis() {
        return axis;
    }

    public void setAxis(AxisId axis) {
        this.axis = axis;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    @JsonGetter("numSegments")
    public int size() {
        return segments == null ? 0 : segments.size();
    }
}
