package org.envirocar.qad.axis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Comparator;
import java.util.Objects;

public class AxisId implements Comparable<AxisId> {
    private static final Comparator<AxisId> COMPARATOR = Comparator.comparingInt(AxisId::getId)
                                                                   .thenComparing(AxisId::getDirection);
    private final int id;
    private final int direction;

    public AxisId(int id, int direction) {
        this.id = id;
        this.direction = direction;
    }

    public int getId() {
        return this.id;
    }

    public int getDirection() {
        return this.direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AxisId)) {
            return false;
        }
        AxisId axisId = (AxisId) o;
        return getId() == axisId.getId() &&
               getDirection() == axisId.getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDirection());
    }

    @JsonValue
    @Override
    public String toString() {
        return String.format("%02d_%d", this.id, this.direction);
    }

    @Override
    public int compareTo(AxisId that) {
        return COMPARATOR.compare(this, Objects.requireNonNull(that));
    }

    @JsonCreator
    public static AxisId fromString(String value) {
        String[] s = value.split("_");
        if (s.length != 2) {
            throw new IllegalArgumentException("invalid axis id: " + value);
        }
        try {
            return new AxisId(Integer.parseInt(s[0], 10),
                              Integer.parseInt(s[1], 10));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid axis id: " + value, e);
        }
    }
}
