package com.jku.dke.bac.ubsm;

import java.util.Random;

public class Util {
    final static public Random RANDOM = new Random(System.currentTimeMillis());

    // min - the minimum skewed value possible
    // max - the maximum skewed value possible
    // skew - the degree to which the values cluster around the mode of the distribution; higher values mean tighter clustering
    // bias - the tendency of the mode to approach the min, max or midpoint value; positive values bias towards max, negative values towards min
    public static int nextSkewedBoundedInt(double min, double max, double skew, double bias) {
        double range = max - min;
        double mid = min + range / 2.0;
        double unitGaussian = RANDOM.nextGaussian();
        double biasFactor = Math.exp(bias);
        double retval = mid + (range * (biasFactor / (biasFactor + Math.exp(-unitGaussian / skew)) - 0.5));
        return (int) retval;
    }
}
