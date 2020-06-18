package org.envirocar.qad.axis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Comparator;
import java.util.Objects;

public class SegmentId implements Comparable<SegmentId> {
    private static final Comparator<SegmentId> COMPARATOR = Comparator.comparing(SegmentId::getAxis)
                                                                      .thenComparingInt(SegmentId::getRank);
    private final AxisId axis;

    private final int rank;

    public SegmentId(AxisId axis, int rank) {
        this.axis = Objects.requireNonNull(axis);
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }

    public AxisId getAxis() {
        return this.axis;
    }

    @JsonValue
    @Override
    public String toString() {
        return String.format("%d_%d_%02d", this.axis.getId(), this.axis.getDirection(), this.rank);
    }

    @Override
    public int compareTo(SegmentId that) {
        return COMPARATOR.compare(this, Objects.requireNonNull(that));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SegmentId)) {
            return false;
        }
        SegmentId segmentId = (SegmentId) o;
        return getRank() == segmentId.getRank() &&
               getAxis().equals(segmentId.getAxis());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAxis(), getRank());
    }

    @JsonCreator
    public static SegmentId fromString(String value) {
        String[] s = value.split("_");
        if (s.length != 3) {
            throw new IllegalArgumentException("invalid segment id: " + value);
        }
        try {
            AxisId axis = new AxisId(Integer.parseInt(s[0], 10), Integer.parseInt(s[1], 10));
            return new SegmentId(axis, Integer.parseInt(s[2], 10));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid segment id: " + value, e);
        }
    }
}
