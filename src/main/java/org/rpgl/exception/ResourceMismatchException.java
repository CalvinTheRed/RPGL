package org.rpgl.exception;

import org.rpgl.json.JsonArray;

/**
 * This exception should be thrown if a RPGLEvent is provided a resource which does not match its cost description.
 *
 * @author Calvin Withun
 */
public class ResourceMismatchException extends RuntimeException {

    public ResourceMismatchException(JsonArray expected, JsonArray actual) {
        super(String.format("Tags %s do not match any required tags from %s",
                actual.toString(), expected.toString()
        ));
    }

}
