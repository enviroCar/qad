package org.envirocar.qad.utils;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.index.strtree.STRtree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypeSafeSpatialIndex<T> {
    private final SpatialIndex index;

    public TypeSafeSpatialIndex(Collection<T> items, Function<T, Envelope> envelopeFunction) {
        index = new STRtree();
        items.forEach(item -> index.insert(envelopeFunction.apply(item), item));
    }

    @SuppressWarnings("unchecked")
    public List<T> query(Envelope search, Predicate<T> predicate) {
        List<T> items = new LinkedList<>();
        Predicate<T> test = predicate == null ? (t) -> true : predicate;
        index.query(search, item -> {
            T t = (T) item;
            if (test.test(t)) {
                items.add(t);
            }
        });
        return items;
    }

    public List<T> query(Geometry search, Predicate<T> predicate) {
        return query(search.getEnvelopeInternal(), predicate);
    }

    public List<T> query(Geometry search) {
        return query(search, null);
    }

    public List<T> query(Envelope search) {
        return query(search, null);
    }
}
