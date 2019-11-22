package org.envirocar.qad.utils;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.index.strtree.STRtree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypeSafeSpatialIndex<T> {
    private final SpatialIndex index;
    private Function<T, Envelope> envelopeFunction;

    public TypeSafeSpatialIndex(Collection<T> items, Function<T, Envelope> envelopeFunction) {
        index = new STRtree();
        this.envelopeFunction = Objects.requireNonNull(envelopeFunction);
        items.forEach(this::insert);
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

    @SuppressWarnings("unchecked")
    public void query(Envelope search, Consumer<T> consumer) {
        Objects.requireNonNull(consumer);
        index.query(search, item -> consumer.accept((T) item));
    }

    public void insert(T item) {
        index.insert(envelopeFunction.apply(item), item);
    }

    public List<T> query(Geometry search, Predicate<T> predicate) {
        return query(search.getEnvelopeInternal(), predicate);
    }

    public void query(Geometry search, Consumer<T> consumer) {
        query(search.getEnvelopeInternal(), consumer);
    }

    public List<T> query(Geometry search) {
        return query(search, (Predicate<T>) null);
    }

    public List<T> query(Envelope search) {
        return query(search, (Predicate<T>) null);
    }
}

