package org.rpgl.core;

import org.rpgl.datapack.DatapackLoader;

/**
 * This class is a factory which creates RPGL-x objects. Any time such objects are needed, they should be created using
 * methods from this class.
 *
 * @author Calvin Withun
 */
public final class RPGLFactory {

    /**
     * This method creates a new RPGLEffect object.
     *
     * @param effectId an effect ID (<code>namespace:effectName</code>)
     * @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId) {
        String[] effectIdSplit = effectId.split(":");
        return DatapackLoader.DATAPACKS.get(effectIdSplit[0]).getEffectTemplate(effectIdSplit[1]).newInstance();
    }

    /**
     * This method creates a new RPGLEvent object.
     *
     * @param eventId an event ID (<code>namespace:eventName</code>)
     * @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId) {
        String[] eventIdSplit = eventId.split(":");
        return DatapackLoader.DATAPACKS.get(eventIdSplit[0]).getEventTemplate(eventIdSplit[1]).newInstance();
    }

    /**
     * This method creates a new RPGLItem object.
     *
     * @param itemId an item ID (<code>namespace:itemName</code>)
     * @return a new RPGLItem object
     */
    public static RPGLItem newItem(String itemId) {
        String[] itemIdSplit = itemId.split(":");
        return DatapackLoader.DATAPACKS.get(itemIdSplit[0]).getItemTemplate(itemIdSplit[1]).newInstance();
    }

    /**
     * This method creates a new RPGLObject object.
     *
     * @param objectId an object ID (<code>namespace:objectName</code>)
     * @return a new RPGLObject object
     */
    public static RPGLObject newObject(String objectId) {
        String[] objectIdSplit = objectId.split(":");
        return DatapackLoader.DATAPACKS.get(objectIdSplit[0]).getObjectTemplate(objectIdSplit[1]).newInstance();
    }

}
