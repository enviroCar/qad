package org.envirocar.qad.mapmatching;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.model.Feature;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchedPoint {
    private Long osmId;
    private String measurementId;
    private String streetName;
    private Feature unmatchedPoint;
    private Feature pointOnRoad;

    @JsonGetter(JsonConstants.OSM_ID)
    public Long getOsmId() {
        return osmId;
    }

    @JsonSetter(JsonConstants.OSM_ID)
    public void setOsmId(Long osmId) {
        this.osmId = osmId;
    }

    @JsonGetter(JsonConstants.MEASUREMENT_ID)
    public String getMeasurementId() {
        return measurementId;
    }

    @JsonSetter(JsonConstants.MEASUREMENT_ID)
    public void setMeasurementId(String measurementId) {
        this.measurementId = measurementId;
    }

    @JsonGetter(JsonConstants.STREET_NAME)
    public String getStreetName() {
        return streetName;
    }

    @JsonSetter(JsonConstants.STREET_NAME)
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @JsonGetter(JsonConstants.UNMATCHED_POINT)
    public Feature getUnmatchedPoint() {
        return unmatchedPoint;
    }

    @JsonSetter(JsonConstants.UNMATCHED_POINT)
    public void setUnmatchedPoint(Feature unmatchedPoint) {
        this.unmatchedPoint = unmatchedPoint;
    }

    @JsonGetter(JsonConstants.POINT_ON_ROAD)
    public Feature getPointOnRoad() {
        return pointOnRoad;
    }

    @JsonSetter(JsonConstants.POINT_ON_ROAD)
    public void setPointOnRoad(Feature pointOnRoad) {
        this.pointOnRoad = pointOnRoad;
    }
}
