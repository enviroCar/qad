package org.envirocar.qad.model;

import org.envirocar.qad.utils.Interpolate;

import java.util.OptionalDouble;

public class Values {
    private final double speed;
    private final OptionalDouble fuelConsumption;
    private final OptionalDouble energyConsumption;
    private final OptionalDouble carbonDioxide;

    public Values() {
        this(0.0, OptionalDouble.empty(), OptionalDouble.empty(), OptionalDouble.empty());
    }

    public Values(double speed, double fuelConsumption, double energyConsumption, double carbonDioxide) {
        this(speed,
             OptionalDouble.of(fuelConsumption),
             OptionalDouble.of(energyConsumption),
             OptionalDouble.of(carbonDioxide));
    }

    public Values(double speed, OptionalDouble fuelConsumption, OptionalDouble energyConsumption,
                  OptionalDouble carbonDioxide) {
        this.speed = speed;
        this.fuelConsumption = fuelConsumption;
        this.energyConsumption = energyConsumption;
        this.carbonDioxide = carbonDioxide;
    }

    public double getSpeed() {
        return this.speed;
    }

    public OptionalDouble getFuelConsumption() {
        return this.fuelConsumption;
    }

    public OptionalDouble getEnergyConsumption() {
        return this.energyConsumption;
    }

    public OptionalDouble getCarbonDioxide() {
        return this.carbonDioxide;
    }

    public static Values interpolate(Values v1, Values v2, double fraction) {
        double speed = Interpolate.linear(v1.getSpeed(),
                                          v2.getSpeed(),
                                          fraction);
        OptionalDouble fuelConsumption = Interpolate.linear(v1.getFuelConsumption(),
                                                            v2.getFuelConsumption(),
                                                            fraction);
        OptionalDouble energyConsumption = Interpolate.linear(v1.getEnergyConsumption(),
                                                              v2.getEnergyConsumption(),
                                                              fraction);
        OptionalDouble carbonDioxide = Interpolate.linear(v1.getCarbonDioxide(),
                                                          v2.getCarbonDioxide(),
                                                          fraction);
        return new Values(speed, fuelConsumption, energyConsumption, carbonDioxide);
    }

}
