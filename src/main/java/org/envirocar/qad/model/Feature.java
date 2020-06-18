package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.envirocar.qad.JsonConstants;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = JsonConstants.TYPE)
@JsonSubTypes({@JsonSubTypes.Type(name = "Feature", value = Feature.class)})
public class Feature implements Enveloped {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(JsonConstants.ID)
    private String id;
    @JsonProperty(JsonConstants.GEOMETRY)
    private Geometry geometry;
    @JsonProperty(JsonConstants.PROPERTIES)
    private ObjectNode properties;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ObjectNode getProperties() {
        return this.properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    @JsonIgnore
    @Override
    public Envelope getEnvelope() {
        return this.geometry.getEnvelopeInternal();
    }
}
