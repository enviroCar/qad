package org.envirocar.qad.utils;

public class IntInterval {
    private int start;
    private int end;

    public IntInterval(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("end < start");
        }
        this.start = start;
        this.end = end;
    }

    public int size() {
        return end - start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void increaseEnd() {
        this.end++;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean before(IntInterval other) {
        return end < other.start;
    }

    public boolean after(IntInterval other) {
        return start > other.end;
    }

    public boolean meets(IntInterval other) {
        return end == other.start;
    }

    public boolean overlaps(IntInterval other) {
        return start < other.start && end > other.start && end < other.end;
    }

    public boolean starts(IntInterval other) {
        return start == other.start && end < other.end;
    }

    public boolean during(IntInterval other) {
        return start > other.start && end < other.end;
    }

    public boolean finishes(IntInterval other) {
        return start > other.start && end == other.end;
    }

    public boolean equal(IntInterval other) {
        return start == other.start && end == other.end;
    }

}
