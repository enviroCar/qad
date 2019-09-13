package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Envelope;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(name = "FeatureCollection", value = FeatureCollection.class)})
public class FeatureCollection {

    @JsonProperty("name")
    private String name;
    @JsonProperty("properties")
    private ObjectNode properties;
    @JsonProperty("features")
    private List<Feature> features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    public Envelope getEnvelope() {
        Envelope envelope = new Envelope();
        getFeatures().stream().map(Feature::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }
}
