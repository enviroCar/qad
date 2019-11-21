package org.envirocar.qad.model.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.axis.Segment;
import org.envirocar.qad.utils.DecimalPlaces;
import org.envirocar.qad.utils.GeometryUtils;

import java.util.Objects;

public class SegmentResult {
    private final Segment segment;
    private final SegmentStatistics statistics;

    public SegmentResult(Segment segment, SegmentStatistics statistics) {
        this.segment = Objects.requireNonNull(segment);
        this.statistics = Objects.requireNonNull(statistics);
    }

    @JsonUnwrapped
    public SegmentStatistics getStatistics() {
        return statistics;
    }

    @JsonGetter(JsonConstants.SEGMENT_ID)
    public int getSegmentId() {
        return segment.getId().getRank();
    }

    @JsonSerialize(using = DecimalPlaces.Two.class)
    @JsonGetter(JsonConstants.SEGMENT_LENGTH)
    public double getSegmentLength() {
        return GeometryUtils.length(segment.getGeometry());
    }
}
