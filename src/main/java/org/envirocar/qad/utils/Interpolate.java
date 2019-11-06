package org.envirocar.qad.utils;

import java.math.BigDecimal;
import java.time.Instant;

public class Interpolate {

    public static Instant linear(Instant v0, Instant v1, double fraction) {
        return BigDecimals.toInstant(linear(BigDecimals.create(v0),
                                            BigDecimals.create(v1),
                                            BigDecimal.valueOf(fraction)));
    }

    public static BigDecimal linear(BigDecimal v0, BigDecimal v1, BigDecimal fraction) {
        if (v0 == null) {
            return v1;
        }
        if (v1 == null) {
            return v0;
        }
        return v0.add(fraction.multiply(v1.subtract(v0)));
    }

    public static double linear(double v0, double v1, double fraction) {
        return v0 + (fraction * (v1 - v0));
    }
}
