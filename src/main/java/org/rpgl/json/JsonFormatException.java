package org.rpgl.json;

import java.io.Serial;

public class JsonFormatException extends Exception {

    @Serial
    private static final long serialVersionUID = 5240789095640930508L;

    public JsonFormatException(String msg) {
        super("JSON formatting exception: " + msg);
    }

}