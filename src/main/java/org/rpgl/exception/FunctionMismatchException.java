package org.rpgl.exception;

public class FunctionMismatchException extends Exception {

    public FunctionMismatchException(String expected, String found) {
        super(String.format("Expected function of type %s but found %s instead.",
                expected,
                found
        ));
    }

}
