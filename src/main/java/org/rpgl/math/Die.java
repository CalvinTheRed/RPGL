package org.rpgl.math;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents a die, and is used for bounded random integer generation. This class may be put into testing
 * mode, during which dice may be given pre-determined values to be rolled rather than using random number generation.
 *
 * @author Calvin Withun
 */
public final class Die {

    private static final Random R = new Random(System.currentTimeMillis());

    private static boolean testing = false;

    // TODO javadoc here. Also... consider having "determined" be an array for dice rolling, which is read as a FIFO queue. That way, you don't need all of these arbitrary reroll keys hard-coded in the code...
    public static int roll(int upperBound, ArrayList<Object> determinedList) {
        if (testing && !determinedList.isEmpty()) {
            return (Integer) determinedList.remove(0);
        } else {
            return R.nextInt(upperBound) + 1;
        }
    }

    public static void setTesting(boolean isTesting) {
        Die.testing = isTesting;
    }

}
