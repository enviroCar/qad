package org.envirocar.qad.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.envirocar.qad.JsonConstants;
import org.locationtech.jts.geom.Envelope;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = JsonConstants.TYPE)
@JsonSubTypes({@JsonSubTypes.Type(name = "FeatureCollection", value = FeatureCollection.class)})
public class FeatureCollection implements Enveloped {
    @JsonProperty(JsonConstants.PROPERTIES)
    private ObjectNode properties;
    @JsonProperty(JsonConstants.FEATURES)
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(List<Feature> features) {
        if (features instanceof RandomAccess) {
            this.features = features;
        } else if (features == null) {
            this.features = null;
        } else {
            this.features = new ArrayList<>(features);

        }

        this.features = features;
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
        Envelope envelope = new Envelope();
        getFeatures().stream().map(Feature::getEnvelope).forEach(envelope::expandToInclude);
        return envelope;
    }

    public Feature getFeature(int i) {
        if (this.features == null) {
            throw new IndexOutOfBoundsException();
        }
        return this.features.get(i);
    }

    public int size() {
        return this.features == null ? 0 : this.features.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
