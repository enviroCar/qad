package org.envirocar.qad.model;

import org.envirocar.qad.utils.Interpolate;

public class Values {
    private final double speed;
    private final double consumption;
    private final double carbonDioxide;

    public Values() {
        this(0.0d, 0.0d, 0.0d);
    }

    public Values(double speed, double consumption, double carbonDioxide) {
        this.speed = speed;
        this.consumption = consumption;
        this.carbonDioxide = carbonDioxide;
    }

    public double getSpeed() {
        return speed;
    }

    public double getConsumption() {
        return consumption;
    }

    public double getCarbonDioxide() {
        return carbonDioxide;
    }

    public static Values interpolate(Values v1, Values v2, double fraction) {
        double speed = Interpolate.linear(v1.getSpeed(), v2.getSpeed(), fraction);
        double consumption = Interpolate.linear(v1.getConsumption(), v2.getConsumption(), fraction);
        double carbonDioxide = Interpolate.linear(v1.getCarbonDioxide(), v2.getCarbonDioxide(), fraction);
        return new Values(speed, consumption, carbonDioxide);
    }

}
