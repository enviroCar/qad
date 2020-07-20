package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.envirocar.qad.JsonConstants;

public class Sensor {
    @JsonProperty(JsonConstants.TYPE)
    private String type;
    @JsonProperty(JsonConstants.PROPERTIES)
    private ObjectNode properties;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectNode getProperties() {
        return this.properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }
}
