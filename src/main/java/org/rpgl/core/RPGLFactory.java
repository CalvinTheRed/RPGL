package org.rpgl.core;

import org.rpgl.datapack.DatapackLoader;
import org.rpgl.json.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a factory which creates RPGL-x objects. Any time such objects are needed, they should be created using
 * methods from this class.
 *
 * @author Calvin Withun
 */
public final class RPGLFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPGLFactory.class);

    /**
     * This method creates a new RPGLEffect instance.
     *
     * @param effectId an effect ID <code>(namespace:name)</code>
     * @param bonuses an array of bonuses to be applied to specified fields in the template
     * @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId, JsonArray bonuses) {
        String[] effectIdSplit = effectId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(effectIdSplit[0]).getEffectTemplate(effectIdSplit[1])
                    .applyBonuses(bonuses)
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLEffect: " + effectId);
            throw new RuntimeException("Encountered an error building a new RPGLEffect", e);
        }
    }

    /**
     * This method creates a new RPGLEffect instance.
     *
     * @param effectId an effect ID <code>(namespace:name)</code>
     * @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId) {
        return newEffect(effectId, new JsonArray());
    }

    /**
     * This method creates a new RPGLEvent instance.
     *
     * @param eventId an event ID <code>(namespace:name)</code>
     * @param bonuses an array of bonuses to be applied to specified fields in the template
     * @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId, JsonArray bonuses) {
        String[] eventIdSplit = eventId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(eventIdSplit[0]).getEventTemplate(eventIdSplit[1])
                    .applyBonuses(bonuses)
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLEvent: " + eventId);
            throw new RuntimeException("Encountered an error building a new RPGLEvent", e);
        }
    }

    /**
     * This method creates a new RPGLEvent instance.
     *
     * @param eventId an event ID <code>(namespace:name)</code>
     * @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId) {
        return newEvent(eventId, new JsonArray());
    }

    /**
     * This method creates a new RPGLItem instance.
     *
     * @param itemId an item ID <code>(namespace:name)</code>
     * @param bonuses an array of bonuses to be applied to specified fields in the template
     * @return a new RPGLItem object
     */
    public static RPGLItem newItem(String itemId, JsonArray bonuses) {
        String[] itemIdSplit = itemId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(itemIdSplit[0]).getItemTemplate(itemIdSplit[1])
                    .applyBonuses(bonuses)
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLItem: " + itemId);
            throw new RuntimeException("Encountered an error building a new RPGLItem", e);
        }
    }

    /**
     * This method creates a new RPGLItem instance.
     *
     * @param itemId an item ID <code>(namespace:name)</code>
     * @return a new RPGLItem object
     */
    public static RPGLItem newItem(String itemId) {
        return newItem(itemId, new JsonArray());
    }

    /**
     * This method creates a new RPGLObject instance.
     *
     * @param objectId an object ID <code>(namespace:name)</code>
     * @param userId the id for the user controlling the new object
     * @param bonuses an array of bonuses to be applied to specified fields in the template
     * @return a new RPGLObject object
     */
    public static RPGLObject newObject(String objectId, String userId, JsonArray bonuses) {
        String[] objectIdSplit = objectId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(objectIdSplit[0]).getObjectTemplate(objectIdSplit[1])
                    .applyBonuses(bonuses)
                    .newInstance()
                    .setUserId(userId);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLObject: " + objectId);
            throw new RuntimeException("Encountered an error building a new RPGLObject", e);
        }
    }

    /**
     * This method creates a new RPGLObject instance.
     *
     * @param objectId an object ID <code>(namespace:name)</code>
     * @param userId the id for the user controlling the new object
     * @return a new RPGLObject object
     */
    public static RPGLObject newObject(String objectId, String userId) {
        return newObject(objectId, userId, new JsonArray());
    }

    /**
     * This method creates a new RPGLResource instance.
     *
     * @param resourceId a resource ID <code>(namespace:name)</code>
     * @param bonuses an array of bonuses to be applied to specified fields in the template
     * @return a new RPGLResource object
     */
    public static RPGLResource newResource(String resourceId, JsonArray bonuses) {
        String[] resourceIdSplit = resourceId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(resourceIdSplit[0]).getResourceTemplate(resourceIdSplit[1])
                    .applyBonuses(bonuses)
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLResource: " + resourceId);
            throw new RuntimeException("Encountered an error building a new RPGLResource", e);
        }
    }

    /**
     * This method creates a new RPGLResource instance.
     *
     * @param resourceId a resource ID <code>(namespace:name)</code>
     * @return a new RPGLResource object
     */
    public static RPGLResource newResource(String resourceId) {
        return newResource(resourceId, new JsonArray());
    }

    /**
     * Returns the RPGLClass stored at the given classId.
     *
     * @param classId a class ID <code>(namespace:name)</code>
     * @return a RPGLClass object
     */
    public static RPGLClass getClass(String classId) {
        String[] classIdSplit = classId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(classIdSplit[0]).getClass(classIdSplit[1]);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error getting RPGLClass: " + classId);
            throw new RuntimeException("Encountered an error getting a RPGLClass", e);
        }
    }

    /**
     * Returns the RPGLRace stored at the given raceId.
     *
     * @param raceId a race ID <code>(namespace:name)</code>
     * @return a RPGLRace object
     */
    public static RPGLRace getRace(String raceId) {
        String[] raceIdSplit = raceId.split(":");
        try {
            return DatapackLoader.DATAPACKS.get(raceIdSplit[0]).getRace(raceIdSplit[1]);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error getting RPGLRace: " + raceId);
            throw new RuntimeException("Encountered an error getting a RPGLRace", e);
        }
    }

}
