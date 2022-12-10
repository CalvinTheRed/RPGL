package org.rpgl.uuidtable;

import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is dedicated to tracking all JsonObject objects which persist during runtime. Objects are given UUID's so
 * that they may reference them without the risk of generating an infinitely large JSON structure via mutual reference.
 *
 * @author Calvin Withun
 */
public final class UUIDTable {

    private static final Map<String, UUIDTableElement> UUID_TABLE = new ConcurrentHashMap<>();

    /**
     * This method assigns a UUIDTableElement a UUID and registers it with the UUIDTable. If the passed object already
     * has a UUID, it is registered under that value. This method is intended to be used to register RPGLEffects,
     * RPGLItems, and RPGLObjects. This method may cause problems if an object already has a UUID which has already been
     * assigned to a different object.
     *
     *  @param data the UUIDTableElement to be registered
     */
    public static void register(UUIDTableElement data) {
        String uuid = data.getUuid();
        while (uuid == null || UUID_TABLE.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        UUID_TABLE.put(uuid, data);
        data.setUuid(uuid);
    }

    /**
     * This method deletes a UUID from its associated UUIDTableElement (if it exists) and removes the UUID from the
     * UUIDTable.
     *
     *  @param uuid the UUID of the UUIDTableElement to be unregistered
     */
    public static UUIDTableElement unregister(String uuid) {
        UUIDTableElement data = UUID_TABLE.remove(uuid);
        if (data != null) {
            data.deleteUuid();
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
     *  @param uuid the UUID of a RPGLEffect
     *  @return an RPGLEffect
     */
    public static RPGLEffect getEffect(String uuid) {
        return (RPGLEffect) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns the RPGLItem associated with a UUID.
     *
     *  @param uuid the UUID of a RPGLItem
     *  @return an RPGLItem
     */
    public static RPGLItem getItem(String uuid) {
        return (RPGLItem) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns the RPGLObject associated with a UUID.
     *
     *  @param uuid the UUID of a RPGLObject
     *  @return an RPGLObject
     */
    public static RPGLObject getObject(String uuid) {
        return (RPGLObject) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns the number of objects being tracked in UUIDTable.
     *
     *  @return a number
     */
    public static int size() {
        return UUID_TABLE.size();
    }

}
