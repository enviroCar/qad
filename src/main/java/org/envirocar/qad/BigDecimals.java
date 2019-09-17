package org.envirocar.qad;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;

public class BigDecimals {
    private static final BigInteger NANOS_PER_SECOND = BigInteger.TEN.pow(9);

    public static BigDecimal create(Instant instant) {
        return create(instant.getEpochSecond(), instant.getNano());
    }

    public static BigDecimal create(Duration duration) {

        return create(duration.getSeconds(), duration.getNano());
    }

    private static BigDecimal create(long seconds, int nanos) {
        return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9));
    }

    public static Instant toInstant(BigDecimal result) {
        BigInteger[] divRem = result.movePointRight(9).toBigIntegerExact().divideAndRemainder(NANOS_PER_SECOND);
        return Instant.ofEpochSecond(divRem[0].longValue(), divRem[1].longValue());
    }

    private static Duration toDuration(BigDecimal result) {
        BigInteger[] divRem = result.movePointRight(9).toBigIntegerExact().divideAndRemainder(NANOS_PER_SECOND);
        return Duration.ofSeconds(divRem[0].longValue(), divRem[1].longValue());
    }
}
