package org.envirocar.qad.model.axis;

public enum SegmentType {
    INFLUENCE, NON_INFLUENCE;

    public static SegmentType fromInteger(int type) {
        switch (type) {
            case 0:
                return NON_INFLUENCE;
            case 1:
                return INFLUENCE;
            default:
                throw new IllegalArgumentException();
        }

    }
}
