package org.envirocar.qad.mapmatching;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.model.Feature;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapMatchingResult {
    private Feature matchedRoute;
    private List<MatchedPoint> matchedPoints;

    @JsonGetter(JsonConstants.MATCHED_ROUTE)
    public Feature getMatchedRoute() {
        return this.matchedRoute;
    }

    @JsonSetter(JsonConstants.MATCHED_ROUTE)
    public void setMatchedRoute(Feature matchedRoute) {
        this.matchedRoute = matchedRoute;
    }

    @JsonGetter(JsonConstants.MATCHED_POINTS)
    public List<MatchedPoint> getMatchedPoints() {
        return this.matchedPoints;
    }

    @JsonSetter(JsonConstants.MATCHED_POINTS)
    public void setMatchedPoints(List<MatchedPoint> matchedPoints) {
        this.matchedPoints = matchedPoints;
    }
}
