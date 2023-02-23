package org.rpgl.exception;

/**
 * This exception should be thrown if the json data passed to a Function indicates it was meant to be processed by a
 * different Function.
 *
 * @author Calvin Withun
 */
public class FunctionMismatchException extends Exception {

    public FunctionMismatchException(String expected, String found) {
        super(String.format("Expected function of type [%s] but found [%s] instead",
                expected,
                found
        ));
    }

}
