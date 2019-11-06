package org.envirocar.qad;

import org.envirocar.qad.analyzer.AnalysisException;

public class TrackParsingException extends AnalysisException {
    public TrackParsingException() {
    }

    public TrackParsingException(String message) {
        super(message);
    }

    public TrackParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackParsingException(Throwable cause) {
        super(cause);
    }

    protected TrackParsingException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
