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

    /**
     * This method returns a number as though a die matching the parameter has been rolled. Unless the die haas a
     * determined value to be rolled, the rolled number will be a random number from 1 to the die's maximum face value.
     *
     * @param upperBound     the maximum face value of the die to be simulated.
     * @param determinedList a list of upcoming values the simulated die should roll (if null or empty, the simulated
     *                       die will roll randomly). This parameter is only used if Die is in testing mode.
     * @return the value rolled by the simulated die.
     */
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

    /**
     * This helper method logs a number rolled in the roll() method.
     *
     * @param upperBound the maximum face value of the rolled die
     * @param roll       the value rolled by the simulated die
     */
    private static void logRoll(int upperBound, int roll) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[d").append(upperBound).append("]");
        if (upperBound < 10)  {
            stringBuilder.append(" ");
        }
        stringBuilder.append(" -> ").append(roll);
        LOGGER.debug(stringBuilder.toString());
    }

    /**
     * This method sets the testing mode of the Die class. When set to true, this class will honor deterministic dice.
     *
     * @param isTesting whether the class should be set to testing mode
     */
    public static void setTesting(boolean isTesting) {
        Die.testing = isTesting;
    }

}
