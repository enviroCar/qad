package org.envirocar.qad;

import java.math.BigDecimal;
import java.time.Instant;

public class Interpolate {

    public static Instant linear(Instant v0, Instant v1, BigDecimal fraction) {
        return BigDecimals
                       .toInstant(linear(BigDecimals.create(v0), BigDecimals
                                                                                            .create(v1), fraction));
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
}
