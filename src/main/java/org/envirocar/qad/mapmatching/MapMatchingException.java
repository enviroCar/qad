package org.envirocar.qad.mapmatching;

public class MapMatchingException extends Exception {
    public MapMatchingException() {
    }

    public MapMatchingException(String message) {
        super(message);
    }

    public MapMatchingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapMatchingException(Throwable cause) {
        super(cause);
    }

    protected MapMatchingException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
