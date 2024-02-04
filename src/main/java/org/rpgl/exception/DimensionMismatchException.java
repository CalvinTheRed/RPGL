package org.rpgl.exception;

import org.rpgl.json.JsonArray;

/**
 * This exception should be thrown if an operation is being performed on arrays which have different lengths.
 *
 * @author Calvin Withun
 */
public class DimensionMismatchException extends Exception {

    public DimensionMismatchException(JsonArray array1, JsonArray array2) {
        super(String.format("Arrays did not have the same dimensionality! <%s>, <%s>",
                array1.toString(),
                array2.toString()
        ));
    }

}
