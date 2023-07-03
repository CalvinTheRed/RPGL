package org.rpgl.exception;

/**
 * This exception should be thrown if a RPGLEvent is provided with the wrong number of resources when invoked.
 *
 * @author Calvin Withun
 */
public class ResourceCountException extends RuntimeException {

    public ResourceCountException(int expected, int actual) {
        super(String.format("Expected to find %d resources, but found %d instead", expected, actual));
    }

}
