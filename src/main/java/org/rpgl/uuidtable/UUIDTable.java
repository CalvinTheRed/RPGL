package org.rpgl.uuidtable;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class UUIDTable {

    private static final Map<Long, JsonObject> UUID_TABLE;
    private static final Random r;

    static {
        UUID_TABLE = new HashMap<>();
        r = new Random(System.currentTimeMillis());
    }

    public static void register(JsonObject data) {
        Long uuid;
        do {
            uuid = r.nextLong();
        } while (UUID_TABLE.containsKey(uuid));
        UUID_TABLE.put(uuid, data);
        data.put("uuid", uuid);
    }

    public static void unregister(long uuid) {
        UUID_TABLE.remove(uuid);
    }

    public static void clear() {
        UUID_TABLE.clear();
    }

    public static RPGLEffect getEffect(long uuid) {
        JsonObject data = UUID_TABLE.get(uuid);
        assert data instanceof RPGLEffect;
        return (RPGLEffect) data;
    }

    public static RPGLItem getItem(long uuid) {
        JsonObject data = UUID_TABLE.get(uuid);
        assert data instanceof RPGLItem;
        return (RPGLItem) data;
    }

    public static RPGLObject getObject(long uuid) {
        JsonObject data = UUID_TABLE.get(uuid);
        assert data instanceof RPGLObject;
        return (RPGLObject) data;
    }

}
