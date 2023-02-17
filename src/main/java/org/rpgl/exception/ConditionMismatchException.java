package org.rpgl.exception;

public class ConditionMismatchException extends Exception {

    public ConditionMismatchException(String expected, String found) {
        super(String.format("Expected condition of type [%s] but found [%s] instead",
                expected,
                found
        ));
    }

}
