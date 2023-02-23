package org.rpgl.exception;

/**
 * This exception should be thrown if the json data passed to a Subevent indicates it was meant to be processed by a
 * different Subevent.
 *
 * @author Calvin Withun
 */
public class SubeventMismatchException extends Exception {

    public SubeventMismatchException(String expected, String found) {
        super(String.format("Expected subevent of type [%s] but found [%s] instead",
                expected,
                found
        ));
    }

}
