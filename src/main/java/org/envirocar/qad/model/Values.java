package org.envirocar.qad.model;

import org.envirocar.qad.utils.Interpolate;

public class Values {
    private final double speed;
    private final double fuelConsumption;
    private final double energyConsumption;
    private final double carbonDioxide;

    public Values() {
        this(0.0d, 0.0d, 0.0d, 0.0d);
    }

    public Values(double speed, double fuelConsumption, double energyConsumption, double carbonDioxide) {
        this.speed = speed;
        this.fuelConsumption = fuelConsumption;
        this.energyConsumption = energyConsumption;
        this.carbonDioxide = carbonDioxide;
    }

    public double getSpeed() {
        return speed;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public double getCarbonDioxide() {
        return carbonDioxide;
    }

    public static Values interpolate(Values v1, Values v2, double fraction) {
        double speed = Interpolate.linear(v1.getSpeed(), v2.getSpeed(), fraction);
        double fuelConsumption = Interpolate.linear(v1.getFuelConsumption(), v2.getFuelConsumption(), fraction);
        double energyConsumption = Interpolate.linear(v1.getEnergyConsumption(), v2.getEnergyConsumption(), fraction);
        double carbonDioxide = Interpolate.linear(v1.getCarbonDioxide(), v2.getCarbonDioxide(), fraction);
        return new Values(speed, fuelConsumption, energyConsumption, carbonDioxide);
    }

}
