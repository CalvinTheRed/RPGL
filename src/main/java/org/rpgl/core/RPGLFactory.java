package org.rpgl.core;

import org.rpgl.datapack.DatapackLoader;
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
     * This method creates a new RPGLEffect instance according to template data stored at the given effect ID.
     *
     * @param effectId an effect ID <code>(namespace:name)</code>
     * @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId) {
        String[] effectIdSplit = effectId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(effectIdSplit[0])
                    .getEffectTemplate(effectIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLEffect: " + effectId);
            throw new RuntimeException("Encountered an error building a new RPGLEffect", e);
        }
    }

    /**
     * This method creates a new RPGLEvent instance according to template data stored at the given event ID.
     *
     * @param eventId an event ID <code>(namespace:name)</code>
     * @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId) {
        String[] eventIdSplit = eventId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(eventIdSplit[0])
                    .getEventTemplate(eventIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLEvent: " + eventId);
            throw new RuntimeException("Encountered an error building a new RPGLEvent", e);
        }
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
     * @return a new RPGLObject object
     */
    public static RPGLObject newObject(String objectId) {
        String[] objectIdSplit = objectId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(objectIdSplit[0])
                    .getObjectTemplate(objectIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            LOGGER.error("encountered an error creating RPGLObject: " + objectId);
            throw new RuntimeException("Encountered an error building a new RPGLObject", e);
        }
    }

}
