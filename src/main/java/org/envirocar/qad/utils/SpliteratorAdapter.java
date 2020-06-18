package org.envirocar.qad.utils;

import java.util.Spliterator;
import java.util.function.Consumer;

@FunctionalInterface
public interface SpliteratorAdapter<T> extends Spliterator<T> {
    @Override
    boolean tryAdvance(Consumer<? super T> action);

    @Override
    default Spliterator<T> trySplit() {
        return null;
    }

    @Override
    default long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    default int characteristics() {
        return Spliterator.ORDERED & Spliterator.NONNULL;
    }
}
