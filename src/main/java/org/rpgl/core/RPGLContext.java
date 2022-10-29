package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

import java.util.HashMap;
import java.util.Map;

public class RPGLContext {

    private final Map<String, RPGLObject> contextObjects;

    public RPGLContext(JsonArray objectUuids) {
        contextObjects = new HashMap<>();
        if (objectUuids != null) {
            for (Object objectUuidElement : objectUuids) {
                String objectUuid = (String) objectUuidElement;
                RPGLObject object = UUIDTable.getObject(objectUuid);
                contextObjects.put(objectUuid, object);
            }
        }
    }

    public void processSubevent(Subevent subevent) throws FunctionMismatchException, ConditionMismatchException {
        boolean wasProcessed;
        do {
            wasProcessed = false;
            for (Map.Entry<String, RPGLObject> contextObjectEntry : contextObjects.entrySet()) {
                wasProcessed |= contextObjectEntry.getValue().processSubevent(subevent);
            }
        } while (wasProcessed);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (Map.Entry<String, RPGLObject> contextObjectEntry : contextObjects.entrySet()) {
            stringBuilder.append(contextObjectEntry.getValue().get("uuid"));
            stringBuilder.append(',');
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

}
