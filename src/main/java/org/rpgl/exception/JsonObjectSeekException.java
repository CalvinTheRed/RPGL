package org.rpgl.exception;

public class JsonObjectSeekException extends Exception {

    public JsonObjectSeekException(String keyPath, Exception e) {
        super("Syntax error found in seek key path: `" + keyPath + "`", e);
    }

}
