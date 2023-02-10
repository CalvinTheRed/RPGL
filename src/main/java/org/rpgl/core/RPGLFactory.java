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
     * <p><b><i>newEffect</i></b></p>
     * <p>
     * <pre class="tab"><code>
     * public static RPGLEffect newEffect(String effectId)
     * 	</code></pre>
     * </p>
     * <p>
     * This method creates a new RPGLEffect instance according to template data stored at the given effect ID.
     * </p>
     *
     *  @param effectId an effect ID <code>(namespace:name)</code>
     *  @return a new RPGLEffect object
     */
    public static RPGLEffect newEffect(String effectId) {
        String[] effectIdSplit = effectId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(effectIdSplit[0])
                    .getEffectTemplate(effectIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            throw new RuntimeException("Encountered an error building a new RPGLEffect", e);
        }
    }

    /**
     * <p><b><i>newEvent</i></b></p>
     * <p>
     * <pre class="tab"><code>
     * public static RPGLEvent newEvent(String eventId)
     * 	</code></pre>
     * </p>
     * <p>
     * This method creates a new RPGLEvent instance according to template data stored at the given event ID.
     * </p>
     *
     *  @param eventId an event ID <code>(namespace:name)</code>
     *  @return a new RPGLEvent object
     */
    public static RPGLEvent newEvent(String eventId) {
        String[] eventIdSplit = eventId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(eventIdSplit[0])
                    .getEventTemplate(eventIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            throw new RuntimeException("Encountered an error building a new RPGLEvent", e);
        }
    }

    /**
     * 	<p><b><i>newItem</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static RPGLItem newItem(String itemId)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method creates a new RPGLItem instance according to template data stored at the given item ID.
     * 	</p>
     *
     *  @param itemId an item ID <code>(namespace:name)</code>
     * 	@return a new RPGLItem object
     */
    public static RPGLItem newItem(String itemId) {
        String[] itemIdSplit = itemId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(itemIdSplit[0])
                    .getItemTemplate(itemIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            throw new RuntimeException("Encountered an error building a new RPGLItem", e);
        }
    }

    /**
     * <p><b><i>newObject</i></b></p>
     * <p>
     * <pre class="tab"><code>
     * public static RPGLObject newObject(String objectId)
     * 	</code></pre>
     * </p>
     * <p>
     * This method creates a new RPGLObject instance according to template data stored at the given object ID.
     * </p>
     *
     *  @param objectId an object ID <code>(namespace:name)</code>
     *  @return a new RPGLObject object
     */
    public static RPGLObject newObject(String objectId) {
        String[] objectIdSplit = objectId.split(":");
        try {
            return DatapackLoader.DATAPACKS
                    .get(objectIdSplit[0])
                    .getObjectTemplate(objectIdSplit[1])
                    .newInstance();
        } catch (NullPointerException e) {
            throw new RuntimeException("Encountered an error building a new RPGLObject", e);
        }
    }

}
