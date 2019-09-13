package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(name = "Feature", value = Feature.class)})
public class Feature {
    @JsonProperty("id")
    private String id;
    @JsonProperty("geometry")
    private Geometry geometry;
    @JsonProperty("properties")
    private ObjectNode properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    public Envelope getEnvelope() {
        return geometry.getEnvelopeInternal();
    }
}
