package org.rpgl.math;

import java.util.Random;

public final class Die {

    private static final Random R = new Random(System.currentTimeMillis());

    private static boolean testing = false;

    public static long roll(long upperBound, Long determinedValue) {
        if (testing && determinedValue != null) {
            return determinedValue;
        } else {
            return R.nextLong(upperBound) + 1;
        }
    }

    public static void setTesting(boolean isTesting) {
        Die.testing = isTesting;
    }

}
