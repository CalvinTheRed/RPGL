package org.rpgl.math;

import org.rpgl.exception.DieSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * This class represents a die, and is used for bounded random integer generation. This class may be put into testing
 * mode, during which dice may be given pre-determined values to be rolled rather than using random number generation.
 *
 * @author Calvin Withun
 */
public final class Die {

    private static final Logger LOGGER = LoggerFactory.getLogger(Die.class);
    private static final Random R = new Random(System.currentTimeMillis());

    private static boolean testing = false;

    public static int roll(int upperBound, List<Object> determinedList) {
        int roll;
        if (testing && determinedList != null && !determinedList.isEmpty()) {
            roll = (int) determinedList.remove(0);
        } else if (upperBound > 0) {
            roll = R.nextInt(upperBound) + 1;
        } else {
            DieSizeException e = new DieSizeException(upperBound);
            LOGGER.error(e.getMessage());
            throw e;
        }
        logRoll(upperBound, roll);
        return roll;
    }

    private static void logRoll(int upperBound, int roll) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[d").append(upperBound).append("]");
        if (upperBound < 10)  {
            stringBuilder.append(" ");
        }
        stringBuilder.append(" -> ").append(roll);
        LOGGER.debug(stringBuilder.toString());
    }

    public static void setTesting(boolean isTesting) {
        Die.testing = isTesting;
    }

}
