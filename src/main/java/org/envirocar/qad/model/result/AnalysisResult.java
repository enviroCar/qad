package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.axis.AxisId;
import org.envirocar.qad.axis.ModelId;

import java.time.Instant;
import java.util.List;

public class AnalysisResult {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";
    private static final String TIME_ZONE = "Europe/Berlin";
    private ModelId model;
    private AxisId axis;
    private Instant start;
    private Instant end;
    private String fuelType;
    private List<SegmentResult> segments;
    private String track;

    @JsonProperty(JsonConstants.SEGMENTS)
    public List<SegmentResult> getSegments() {
        return this.segments;
    }

    public void setSegments(List<SegmentResult> segments) {
        this.segments = segments;
    }

    @JsonUnwrapped
    public ModelId getModel() {
        return this.model;
    }

    public void setModel(ModelId model) {
        this.model = model;
    }

    @JsonProperty(JsonConstants.AXIS)
    public AxisId getAxis() {
        return this.axis;
    }

    public void setAxis(AxisId axis) {
        this.axis = axis;
    }

    @JsonFormat(timezone = TIME_ZONE, pattern = DATE_TIME_PATTERN)
    @JsonProperty(JsonConstants.START)
    public Instant getStart() {
        return this.start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    @JsonFormat(timezone = TIME_ZONE, pattern = DATE_TIME_PATTERN)
    @JsonProperty(JsonConstants.END)
    public Instant getEnd() {
        return this.end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    @JsonProperty(JsonConstants.FUEL_TYPE)
    public String getFuelType() {
        return this.fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    @JsonProperty(JsonConstants.TRACK)
    public String getTrack() {
        return this.track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
