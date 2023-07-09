package org.rpgl.uuidtable;

import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;

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
     * @param uuidTableElement a UUIDTableElement
     */
    public static void register(UUIDTableElement uuidTableElement) {
        String uuid = uuidTableElement.getUuid();
        while (uuid == null || UUID_TABLE.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        UUID_TABLE.put(uuid, uuidTableElement);
        uuidTableElement.setUuid(uuid);
    }

    /**
     * This method deletes a UUID from its associated UUIDTableElement (if it exists) and removes the UUID from the
     * UUIDTable.
     *
     * @param uuid the UUID of a registered UUIDTableElement
     */
    public static UUIDTableElement unregister(String uuid) {
        UUIDTableElement uuidTableElement = UUID_TABLE.remove(uuid);
        if (uuidTableElement != null) {
            uuidTableElement.deleteUuid();
        }
        return uuidTableElement;
    }

    /**
     * This method clears the UUIDTable. Note that this method does not recursively remove references between
     * UUIDTableElement objects, so attempting to use or re-register some but not all of the unregistered
     * UUIDTableElement objects may result in errors.
     */
    public static void clear() {
        UUID_TABLE.clear();
    }

    /**
     * This method returns a RPGLEffect object with the passed uuid, or null if no RPGLEffect exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLEffect
     * @return a RPGLEffect, or null if the uuid is null or if the uuid does not map to an effect
     */
    public static RPGLEffect getEffect(String uuid) {
        return uuid == null ? null : (RPGLEffect) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns a RPGLItem object with the passed uuid, or null if no RPGLItem exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLItem
     * @return a RPGLItem, or null if the uuid is null or if the uuid does not map to an item
     */
    public static RPGLItem getItem(String uuid) {
        return uuid == null ? null : (RPGLItem) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns a RPGLObject object with the passed uuid, or null if no RPGLObject exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLObject
     * @return a RPGLObject, or null if the uuid is null or if the uuid does not map to an object
     */
    public static RPGLObject getObject(String uuid) {
        return uuid == null ? null : (RPGLObject) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns a RPGLResource object with the passed uuid, or null if no RPGLResource exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLResource
     * @return a RPGLResource, or null if the uuid is null or if the uuid does not map to a resource
     */
    public static RPGLResource getResource(String uuid) {
        return uuid == null ? null : (RPGLResource) UUID_TABLE.get(uuid);
    }

    /**
     * This method returns the number of UUIDTableElement objects stored in UUIDTable.
     *
     * @return the number of UUIDTableElement objects stored in UUIDTable
     */
    public static int size() {
        return UUID_TABLE.size();
    }

}
