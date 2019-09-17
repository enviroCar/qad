package org.envirocar.qad.model;

import org.envirocar.qad.Interpolate;

import java.math.BigDecimal;

public class Values {
    private BigDecimal speed;
    private BigDecimal consumption;
    private BigDecimal carbonDioxide;

    public Values(BigDecimal speed, BigDecimal consumption, BigDecimal carbonDioxide) {
        this.speed = speed;
        this.consumption = consumption;
        this.carbonDioxide = carbonDioxide;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    public BigDecimal getCarbonDioxide() {
        return carbonDioxide;
    }

    public static Values interpolate(Values v1, Values v2, BigDecimal fraction) {
        BigDecimal speed = Interpolate.linear(v1.getSpeed(), v2.getSpeed(), fraction);
        BigDecimal consumption = Interpolate.linear(v1.getConsumption(), v2.getConsumption(), fraction);
        BigDecimal carbonDioxide = Interpolate.linear(v1.getCarbonDioxide(), v2.getCarbonDioxide(), fraction);
        return new Values(speed, consumption, carbonDioxide);
    }

}
