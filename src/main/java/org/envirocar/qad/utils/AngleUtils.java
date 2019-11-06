package org.envirocar.qad.utils;

public class AngleUtils {
    public static double deviation(double a1, double a2) {
        return (Math.abs(a1 - a2) + 180) % 360 - 180;
    }

    public static double normalize(double angle) {
        return (angle + 360) % 360;
    }
}
