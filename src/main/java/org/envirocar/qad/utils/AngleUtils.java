package org.envirocar.qad.utils;

public class AngleUtils {
    /**
     * Finds the deviation of two angles. Normalized to be between -180 and 180.
     * @param a1 The first angle.
     * @param a2 The second angle.
     * @return
     */
    public static double deviation(double a1, double a2) {
         return (Math.abs(a1 - a2) + 180) % 360 - 180;
    }

    /**
     * Normalizes the angle to be between 0 and 360.
     *
     * @param angle The angle.
     * @return The normalized angle.
     */
    public static double normalize(double angle) {
        return (angle + 360) % 360;
    }
}
