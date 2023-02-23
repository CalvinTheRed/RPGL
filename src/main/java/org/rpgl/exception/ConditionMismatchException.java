package org.rpgl.exception;

/**
 * This exception should be thrown if the json data passed to a Condition indicates it was meant to be processed by a
 * different Condition.
 *
 * @author Calvin Withun
 */
public class ConditionMismatchException extends Exception {

    public ConditionMismatchException(String expected, String found) {
        super(String.format("Expected condition of type [%s] but found [%s] instead",
                expected,
                found
        ));
    }

}
