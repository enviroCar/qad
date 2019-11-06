package org.envirocar.qad.utils;

import java.util.Comparator;

public interface Interval<T, E extends Interval<T, E>> {
    T getStart();

    T getEnd();

    @SuppressWarnings("unchecked")
    default int compare(T t1, T t2) {
        return ((Comparator<T>) Comparator.naturalOrder()).compare(t1, t2);
    }

    default boolean before(E other) {
        return compare(getEnd(), other.getStart()) < 0;
    }

    default boolean after(E other) {
        return compare(getStart(), other.getEnd()) > 0;
    }

    default boolean meets(E other) {
        return compare(getEnd(), other.getStart()) == 0;
    }

    default boolean overlaps(E other) {
        return compare(getStart(), other.getStart()) < 0 &&
               compare(getEnd(), other.getStart()) > 0 &&
               compare(getEnd(), other.getEnd()) < 0;
    }

    default boolean starts(E other) {
        return compare(getStart(), other.getStart()) == 0 &&
               compare(getEnd(), other.getEnd()) < 0;
    }

    default boolean during(E other) {
        return compare(getStart(), other.getStart()) > 0 &&
               compare(getEnd(), other.getEnd()) < 0;
    }

    default boolean finishes(E other) {
        return compare(getStart(), other.getStart()) > 0 &&
               compare(getEnd(), other.getEnd()) == 0;
    }

    default boolean equal(E other) {
        return compare(getStart(), other.getStart()) == 0 &&
               compare(getEnd(), other.getEnd()) == 0;
    }

}
