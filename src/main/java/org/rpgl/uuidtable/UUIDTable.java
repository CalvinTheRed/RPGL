package org.rpgl.uuidtable;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is dedicated to tracking all JsonObject objects which persist during runtime. Objects are given UUID's so
 * that they may reference them without the risk of generating an infinitely large JSON structure via mutual reference.
 *
 * @author Calvin Withun
 */
public final class UUIDTable {

    private static final Map<Long, JsonObject> UUID_TABLE;
    private static final Random r;

    static {
        UUID_TABLE = new HashMap<>();
        r = new Random(System.currentTimeMillis());
    }

    /**
     * This method assigns a JsonObject a UUID and registers it with the UUIDTable. If the passed object already has a
     * UUID, it is registered under that value. This method is intended to be used to register RPGLEffects, RPGLItems,
     * and RPGLObjects.
     *
     * @param data the JsonObject to be registered
     */
    public static void register(JsonObject data) {
        Long uuid = (Long) data.get("uuid");
        while (uuid == null || UUID_TABLE.containsKey(uuid)) {
            uuid = r.nextLong();
        }
        UUID_TABLE.put(uuid, data);
        data.put("uuid", uuid);
    }

    /**
     * This method removes a UUID from its associated JsonObject (if it exists) and removes the UUID from the UUIDTable.
     *
     * @param uuid the UUID of the JsonObject to be unregistered
     */
    public static JsonObject unregister(long uuid) {
        JsonObject data = UUID_TABLE.remove(uuid);
        if (data != null) {
            data.remove("uuid");
        }
        return data;
    }

    /**
     * This method clears the UUIDTable. It should only be used during testing.
     * <br><br>
     * NOTE: this method does not remove the UUID from any of the associated JsonObjects.
     */
    public static void clear() {
        UUID_TABLE.clear();
    }

    /**
     * This method returns the RPGLEffect associated with a UUID.
     *
     * @param uuid the UUID of a RPGLEffect
     * @return an RPGLEffect
     * @throws AssertionError if the JsonObject associated with the UUID is null or not a RPGLEffect.
     */
    public static RPGLEffect getEffect(long uuid) {
        JsonObject data = UUID_TABLE.get(uuid);
        assert data instanceof RPGLEffect;
        return (RPGLEffect) data;
    }

    /**
     * This method returns the RPGLItem associated with a UUID.
     *
     * @param uuid the UUID of a RPGLItem
     * @return an RPGLItem
     * @throws AssertionError if the JsonObject associated with the UUID is null or not a RPGLItem.
     */
    public static RPGLItem getItem(long uuid) {
        JsonObject data = UUID_TABLE.get(uuid);
        assert data instanceof RPGLItem;
        return (RPGLItem) data;
    }

    /**
     * This method returns the RPGLObject associated with a UUID.
     *
     * @param uuid the UUID of a RPGLObject
     * @return an RPGLObject
     * @throws AssertionError if the JsonObject associated with the UUID is null or not a RPGLObject.
     */
    public static RPGLObject getObject(long uuid) {
        JsonObject data = UUID_TABLE.get(uuid);
        assert data instanceof RPGLObject;
        return (RPGLObject) data;
    }

    /**
     * This method returns the number of objects being tracked in UUIDTable.
     *
     * @return a number
     */
    public static int size() {
        return UUID_TABLE.size();
    }

}
