package org.rpgl.core;

import org.rpgl.datapack.DatapackLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class is a factory which creates RPGL-x objects. Any time such objects are needed, they should be created using
 * methods from this class.
 *
 * @author Calvin Withun
 */
public final class RPGLFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPGLFactory.class);

    /**
     * This method creates a new RPGLEffect instance according to template data stored at the given effect ID.
     *
     * @param effectId an effect ID <code>(namespace:name)</code>
     * @param originItem an item UUID to be stored for the new effect's origin item
     * @param resources a list of resources used to produce a new effect
     * @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId, String originItem, List<RPGLResource> resources) {
        String[] effectIdSplit = effectId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(effectIdSplit[0])
                    .getEffectTemplate(effectIdSplit[1])
                    .newInstance(originItem, resources);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLEffect: " + effectId);
            throw new RuntimeException("Encountered an error building a new RPGLEffect", e);
        }
    }

    /**
     * This method creates a new RPGLEffect instance according to template data stored at the given effect ID.
     *
     * @param effectId an effect ID <code>(namespace:name)</code>
     * @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId) {
        return newEffect(effectId, null, List.of());
    }

    /**
     * This method creates a new RPGLEvent instance according to template data stored at the given event ID.
     *
     * @param eventId an event ID <code>(namespace:name)</code>
     * @param originItem an item UUID to be stored for the new event's origin item
     * @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId, String originItem, String sourceUuid) {
        String[] eventIdSplit = eventId.split(":");
        try {
            RPGLEvent event = DatapackLoader.DATAPACKS
                    .get(eventIdSplit[0])
                    .getEventTemplate(eventIdSplit[1])
                    .newInstance(originItem);
            event.putString("source", sourceUuid);
            return event;
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLEvent: " + eventId);
            throw new RuntimeException("Encountered an error building a new RPGLEvent", e);
        }
    }

    /**
     * This method creates a new RPGLEvent instance according to template data stored at the given event ID.
     *
     * @param eventId an event ID <code>(namespace:name)</code>
     * @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId) {
        return newEvent(eventId, null, null);
    }

    /**
     * This method creates a new RPGLItem instance according to template data stored at the given item ID.
     *
     * @param itemId an item ID <code>(namespace:name)</code>
     * @return a new RPGLItem object
     */
    public static RPGLItem newItem(String itemId) {
        String[] itemIdSplit = itemId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(itemIdSplit[0])
                    .getItemTemplate(itemIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLItem: " + itemId);
            throw new RuntimeException("Encountered an error building a new RPGLItem", e);
        }
    }

    /**
     * This method creates a new RPGLObject instance according to template data stored at the given object ID.
     *
     * @param objectId an object ID <code>(namespace:name)</code>
     * @param userId the id for the user controlling the new object
     * @return a new RPGLObject object
     */
    public static RPGLObject newObject(String objectId, String userId) {
        String[] objectIdSplit = objectId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(objectIdSplit[0])
                    .getObjectTemplate(objectIdSplit[1])
                    .newInstance(userId);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLObject: " + objectId);
            throw new RuntimeException("Encountered an error building a new RPGLObject", e);
        }
    }

    /**
     * This method creates a new RPGLResource instance according to template data stored at the given resource ID.
     *
     * @param resourceId a resource ID <code>(namespace:name)</code>
     * @param originItem an item UUID to be stored for the new resource's origin item
     * @return a new RPGLResource object
     */
    public static RPGLResource newResource(String resourceId, String originItem) {
        String[] resourceIdSplit = resourceId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(resourceIdSplit[0])
                    .getResourceTemplate(resourceIdSplit[1])
                    .newInstance(originItem);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLResource: " + resourceId);
            throw new RuntimeException("Encountered an error building a new RPGLResource", e);
        }
    }

    /**
     * This method creates a new RPGLResource instance according to template data stored at the given resource ID.
     *
     * @param resourceId a resource ID <code>(namespace:name)</code>
     * @return a new RPGLResource object
     */
    public static RPGLResource newResource(String resourceId) {
        return newResource(resourceId, null);
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
            return DatapackLoader.DATAPACKS
                    .get(classIdSplit[0])
                    .getClass(classIdSplit[1]);
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
            return DatapackLoader.DATAPACKS
                    .get(raceIdSplit[0])
                    .getRace(raceIdSplit[1]);
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error getting RPGLRace: " + raceId);
            throw new RuntimeException("Encountered an error getting a RPGLRace", e);
        }
    }

}
