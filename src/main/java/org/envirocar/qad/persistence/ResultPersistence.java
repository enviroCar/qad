package org.envirocar.qad.persistence;

import org.envirocar.qad.model.result.AnalysisResult;

import java.util.stream.Stream;

public interface ResultPersistence {

    default void persist(Stream<AnalysisResult> results) {
        results.forEach(this::persist);
    }

    void persist(AnalysisResult result);
}
