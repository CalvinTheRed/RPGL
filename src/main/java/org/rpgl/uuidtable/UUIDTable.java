package org.rpgl.uuidtable;

import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.json.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if (uuid != null) {
            UUIDTableElement element = UUID_TABLE.get(uuid);
            if (element instanceof RPGLEffect effect) {
                return effect;
            }
        }
        return null;
    }

    /**
     * This method returns a RPGLItem object with the passed uuid, or null if no RPGLItem exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLItem
     * @return a RPGLItem, or null if the uuid is null or if the uuid does not map to an item
     */
    public static RPGLItem getItem(String uuid) {
        if (uuid != null) {
            UUIDTableElement element = UUID_TABLE.get(uuid);
            if (element instanceof RPGLItem item) {
                return item;
            }
        }
        return null;
    }

    /**
     * This method returns a RPGLObject object with the passed uuid, or null if no RPGLObject exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLObject
     * @return a RPGLObject, or null if the uuid is null or if the uuid does not map to an object
     */
    public static RPGLObject getObject(String uuid) {
        if (uuid != null) {
            UUIDTableElement element = UUID_TABLE.get(uuid);
            if (element instanceof RPGLObject object) {
                return object;
            }
        }
        return null;
    }

    /**
     * This method returns a RPGLResource object with the passed uuid, or null if no RPGLResource exists for that uuid or
     * the uuid is null.
     *
     * @param uuid the UUID of a RPGLResource
     * @return a RPGLResource, or null if the uuid is null or if the uuid does not map to a resource
     */
    public static RPGLResource getResource(String uuid) {
        if (uuid != null) {
            UUIDTableElement element = UUID_TABLE.get(uuid);
            if (element instanceof RPGLResource resource) {
                return resource;
            }
        }
        return null;
    }

    /**
     * This method returns the number of UUIDTableElement objects stored in UUIDTable.
     *
     * @return the number of UUIDTableElement objects stored in UUIDTable
     */
    public static int size() {
        return UUID_TABLE.size();
    }

    /**
     * Saves all data in UUIDTable to the passed directory.
     *
     * @param directory a directory in which UUIDTable data is to be stored
     *
     * @throws IOException if an I/O exception occurs
     */
    public static void saveToDirectory(File directory) throws IOException {
        deleteDir(directory);
        File effectsDirectory = new File(directory.getAbsolutePath() + File.separator + "effects");
        File itemsDirectory = new File(directory.getAbsolutePath() + File.separator + "items");
        File objectsDirectory = new File(directory.getAbsolutePath() + File.separator + "objects");
        File resourcesDirectory = new File(directory.getAbsolutePath() + File.separator + "resources");

        effectsDirectory.mkdirs();
        itemsDirectory.mkdirs();
        objectsDirectory.mkdirs();
        resourcesDirectory.mkdirs();

        BufferedWriter writer;

        for (Map.Entry<String, UUIDTableElement> entry : UUID_TABLE.entrySet()) {
            UUIDTableElement element = entry.getValue();
            if (element instanceof RPGLEffect effect) {
                writer = new BufferedWriter(new FileWriter(effectsDirectory.getAbsolutePath() + File.separator + effect.getUuid() + ".json"));
                writer.write(new RPGLEffectTO(effect).toRPGLEffect().toString());
                writer.close();
            } else if (element instanceof RPGLItem item) {
                writer = new BufferedWriter(new FileWriter(itemsDirectory.getAbsolutePath() + File.separator + item.getUuid() + ".json"));
                writer.write(new RPGLItemTO(item).toRPGLItem().toString());
                writer.close();
            } else if (element instanceof RPGLObject object) {
                writer = new BufferedWriter(new FileWriter(objectsDirectory.getAbsolutePath() + File.separator + object.getUuid() + ".json"));
                writer.write(new RPGLObjectTO(object).toRPGLObject().toString());
                writer.close();
            } else if (element instanceof RPGLResource resource) {
                writer = new BufferedWriter(new FileWriter(resourcesDirectory.getAbsolutePath() + File.separator + resource.getUuid() + ".json"));
                writer.write(new RPGLResourceTO(resource).toRPGLResource().toString());
                writer.close();
            }
        }
    }

    /**
     * This helper method recursively deletes files and directories within a passed directory.
     *
     * @param directory a directory to be deleted, along with all of its contents
     */
    static void deleteDir(File directory) {
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        deleteDir(file);
                    }
                    file.delete();
                }
            }
        }
    }

    /**
     * Loads data into UUIDTable from the passed directory.
     *
     * @param directory a directory in which UUIDTable data is stored
     *
     * @throws IOException if an I/O exception occurs
     */
    public static void loadFromDirectory(File directory) throws IOException {
        for (File file : Objects.requireNonNull(new File(directory.getAbsolutePath() + File.separator + "effects").listFiles())) {
            UUIDTable.register(JsonObject.MAPPER.readValue(file, RPGLEffectTO.class).toRPGLEffect());
        }
        for (File file : Objects.requireNonNull(new File(directory.getAbsolutePath() + File.separator + "items").listFiles())) {
            UUIDTable.register(JsonObject.MAPPER.readValue(file, RPGLItemTO.class).toRPGLItem());
        }
        for (File file : Objects.requireNonNull(new File(directory.getAbsolutePath() + File.separator + "objects").listFiles())) {
            UUIDTable.register(JsonObject.MAPPER.readValue(file, RPGLObjectTO.class).toRPGLObject());
        }
        for (File file : Objects.requireNonNull(new File(directory.getAbsolutePath() + File.separator + "resources").listFiles())) {
            UUIDTable.register(JsonObject.MAPPER.readValue(file, RPGLResourceTO.class).toRPGLResource());
        }
    }

    /**
     * Returns a list of RPGLObjects under the control of a specified user.
     *
     * @param userId a user id
     * @return a list of RPGLObjects
     */
    public static List<RPGLObject> getObjectsByUserId(String userId) {
        List<RPGLObject> objects = new ArrayList<>();
        for (String uuid : UUID_TABLE.keySet()) {
            RPGLObject object = getObject(uuid);
            if (object != null && object.getUserId().equals(userId)) {
                objects.add(object);
            }
        }
        return objects;
    }

    /**
     * Returns a list of all RPGLObjects stored in UUIDTable.
     *
     * @return a list of RPGLObjects
     */
    public static List<RPGLObject> getObjects() {
        List<RPGLObject> objects = new ArrayList<>();
        for (String uuid : UUID_TABLE.keySet()) {
            RPGLObject object = getObject(uuid);
            if (object != null) {
                objects.add(object);
            }
        }
        return objects;
    }

}
