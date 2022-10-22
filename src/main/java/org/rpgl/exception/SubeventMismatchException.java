package org.rpgl.exception;

public class SubeventMismatchException extends Exception {

    public SubeventMismatchException(String expected, String found) {
        super(String.format("Expected subevent of type {} but found {} instead.",
                expected,
                found
        ));
    }

}
