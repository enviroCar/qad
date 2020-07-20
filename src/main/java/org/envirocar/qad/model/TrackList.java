package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.envirocar.qad.JsonConstants;

import java.util.List;

public class TrackList {
    @JsonProperty(JsonConstants.TRACKS)
    private List<TrackSummary> tracks;

    public List<TrackSummary> getTracks() {
        return this.tracks;
    }

    public void setTracks(List<TrackSummary> tracks) {
        this.tracks = tracks;
    }
}
