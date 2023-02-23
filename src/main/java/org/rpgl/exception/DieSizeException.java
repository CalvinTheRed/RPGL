package org.rpgl.exception;

/**
 * This exception should be thrown if a die is assigned an illegal size (anything less than 1).
 *
 * @author Calvin Withun
 */
public class DieSizeException extends RuntimeException {

    public DieSizeException(int size) {
        super(String.format("Expected a die size >0 but found [%d] instead", size));
    }

}
