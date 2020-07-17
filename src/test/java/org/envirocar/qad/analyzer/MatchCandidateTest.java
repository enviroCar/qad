package org.envirocar.qad.analyzer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.envirocar.qad.JsonConstants;
import org.envirocar.qad.utils.BigDecimals;
import org.envirocar.qad.utils.DurationSerializer;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;

public class MatchCandidateTest {

    @Test
    public void testDurationCalculation() throws JsonProcessingException {
        double speed = 9.09; // km/h
        speed = speed / 3.6; // m/s
        System.out.println(speed);
        double length = 22.97; // m
        BigDecimal result = BigDecimal.valueOf(length / speed);
        System.out.println(result);
        //result = result.multiply(BigDecimal.valueOf(1000));
        System.out.println(result);
        Duration duration = BigDecimals.toDuration(BigDecimal.valueOf(length / speed));
        System.out.println(duration);
        System.out.println(new ObjectMapper().writeValueAsString(new A(duration)));;
    }

    private static class A {
        @JsonSerialize(using = DurationSerializer.class)
        @JsonProperty(JsonConstants.TRAVEL_TIME)
        private final Duration travelTime;

        A(Duration travelTime) {
            this.travelTime = travelTime;
        }
    }
}