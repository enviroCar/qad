package org.envirocar.qad.analyzer;

public class AnalysisException extends RuntimeException {
    public AnalysisException() {
    }

    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalysisException(Throwable cause) {
        super(cause);
    }

    protected AnalysisException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
