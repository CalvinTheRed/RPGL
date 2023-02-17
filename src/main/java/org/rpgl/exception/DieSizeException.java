package org.rpgl.exception;

public class DieSizeException extends RuntimeException {

    public DieSizeException(int size) {
        super(String.format("Expected a die size >0 but found [%d] instead",
                size));
    }

}
