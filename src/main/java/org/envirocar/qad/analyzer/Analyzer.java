package org.envirocar.qad.analyzer;

import org.envirocar.qad.model.result.AnalysisResult;

import java.util.stream.Stream;

public interface Analyzer {
    boolean isApplicable();

    Stream<AnalysisResult> analyze() throws AnalysisException;

}
