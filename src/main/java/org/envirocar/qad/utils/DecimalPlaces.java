package org.envirocar.qad.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DecimalPlaces extends CustomDoubleSerializer {

    public DecimalPlaces(int decimalPlaces) {
        super(createNumberFormat(decimalPlaces));
    }

    private static NumberFormat createNumberFormat(int decimalPlaces) {
        NumberFormat format = DecimalFormat.getInstance(Locale.ROOT);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(decimalPlaces);
        format.setGroupingUsed(false);
        return format;
    }

    public static class Two extends DecimalPlaces {
        public Two() { super(2); }
    }

}
