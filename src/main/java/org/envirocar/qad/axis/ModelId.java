package org.envirocar.qad.axis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.envirocar.qad.JsonConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;

public class ModelId implements Comparable<ModelId> {
    private static final Comparator<ModelId> COMPARATOR = Comparator.comparing(ModelId::getValue)
                                                                    .thenComparing(ModelId::getVersion);
    private final String value;
    private final String version;

    @JsonCreator
    public ModelId(@JsonProperty(JsonConstants.CITY) String value,
                   @JsonProperty(JsonConstants.VERSION) String version) {
        this.value = Objects.requireNonNull(value);
        this.version = Objects.requireNonNull(version);
    }

    @JsonProperty(JsonConstants.CITY)
    public String getValue() {
        return this.value;
    }

    @JsonProperty(JsonConstants.VERSION)
    public String getVersion() {
        return this.version;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModelId)) {
            return false;
        }
        ModelId modelId = (ModelId) o;
        return getValue().equals(modelId.getValue()) && getVersion().equals(modelId.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getVersion());
    }

    @Override
    public int compareTo(@NotNull ModelId o) {
        return COMPARATOR.compare(this, o);
    }
}
