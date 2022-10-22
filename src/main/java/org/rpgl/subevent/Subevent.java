package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.exception.SubeventMismatchException;

import java.util.HashMap;
import java.util.Map;

public abstract class Subevent extends JsonObject {

    public static final Map<String, Subevent> SUBEVENTS;

    static {
        SUBEVENTS = new HashMap<>();
    }

    public void verifySubevent(String expected, JsonObject data) throws SubeventMismatchException {
        if (!expected.equals(data.get("subevent"))) {
            throw new SubeventMismatchException(expected, (String) data.get("subevent"));
        }
    }

    public abstract Subevent clone();

    public abstract void invokeSubevent(long sourceUuid, long targetUuid, JsonObject data) throws SubeventMismatchException;

}
