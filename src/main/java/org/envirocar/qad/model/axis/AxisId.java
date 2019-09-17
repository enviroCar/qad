package org.envirocar.qad.model.axis;

import java.util.Objects;

public class AxisId {
    private final int id;
    private final int direction;

    public AxisId(int id, int direction) {
        this.id = id;
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    public int getDirection() {
        return direction;
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

    @Override
    public String toString() {
        return String.format("axis %d_%d", id, direction);
    }
}
