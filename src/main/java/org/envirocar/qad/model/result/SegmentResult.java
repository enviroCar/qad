package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.axis.Segment;
import org.envirocar.qad.utils.DecimalPlaces;
import org.envirocar.qad.utils.GeometryUtils;

import java.util.Objects;

public class SegmentResult {
    @JsonIgnore
    private Segment segment;
    @JsonUnwrapped
    private SegmentStatistics statistics;
    @JsonIgnore
    private int begin;
    @JsonIgnore
    private int end;
    @JsonProperty(JsonConstants.SEGMENT_ID)
    private int segmentId;

    public SegmentResult(Segment segment, SegmentStatistics statistics, int begin, int end) {
        this.segment = Objects.requireNonNull(segment);
        this.statistics = Objects.requireNonNull(statistics);
        this.begin = begin;
        this.end = end;
        this.segmentId = this.segment.getId().getRank();
    }

    //@JsonProperty("trackIndex")
    @JsonIgnore
    public int[] getIndex() {
        return new int[]{this.begin, this.end};
    }

    @JsonCreator
    public SegmentResult() {}

    public void setStatistics(SegmentStatistics statistics) {
        this.statistics = statistics;
    }

    public SegmentStatistics getStatistics() {
        return this.statistics;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public int getSegmentId() {
        return this.segmentId;
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonGetter(JsonConstants.SEGMENT_LENGTH)
    public double getSegmentLength() {
        if (this.segment == null) {
            return -1;
        }
        return GeometryUtils.length(this.segment.getGeometry());
    }
}
