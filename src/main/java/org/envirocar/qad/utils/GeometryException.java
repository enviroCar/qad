package org.envirocar.qad.utils;

public class GeometryException extends Exception {
    public GeometryException() {
    }

    public GeometryException(String message) {
        super(message);
    }

    public GeometryException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeometryException(Throwable cause) {
        super(cause);
    }

    protected GeometryException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
