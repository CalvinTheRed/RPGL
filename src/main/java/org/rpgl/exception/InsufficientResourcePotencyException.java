package org.rpgl.exception;

/**
 * This exception should be thrown if a RPGLEvent is being invoked using a resource whose potency is too low.
 *
 * @author Calvin Withun
 */
public class InsufficientResourcePotencyException extends RuntimeException {

    public InsufficientResourcePotencyException(String resourceId, int expected, int actual) {
        super(String.format("Resource %s has insufficient potency for event (%d < %d)", resourceId, expected, actual));
    }

}
