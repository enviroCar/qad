package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.axis.AxisId;
import org.envirocar.qad.axis.ModelId;

import java.time.Instant;
import java.util.List;

public class AnalysisResult {
    private ModelId model;
    private AxisId axis;
    private Instant start;
    private Instant end;
    private String fuelType;
    private List<SegmentResult> segments;
    private String track;

    @JsonProperty(JsonConstants.SEGMENTS)
    public List<SegmentResult> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentResult> segments) {
        this.segments = segments;
    }

    @JsonUnwrapped
    public ModelId getModel() {
        return model;
    }

    public void setModel(ModelId model) {
        this.model = model;
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

    @JsonProperty(JsonConstants.TRACK)
    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
