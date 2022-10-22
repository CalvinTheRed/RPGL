package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This class contains a JSON template defining a particular type of RPGLItem. It is not intended to be used for any
 * purpose other than constructing new RPGLItem objects.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplate extends JsonObject {

    /**
     * A copy-constructor for the RPGLItemTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLItemTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLItem object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLItem object
     */
    public RPGLItem newInstance() {
        RPGLItem item = new RPGLItem(this);
        processWhenEquipped(item);
        UUIDTable.register(item);
        return item;
    }

    /**
     * This helper method converts effectId's in an RPGLItemTemplate's when_equipped array to RPGLEffects. The UUID's of
     * these new RPGLEffects replace the original array contents.
     *
     * @param item the item being processed.
     */
    private static void processWhenEquipped(RPGLItem item) {
        Object keyValue = item.remove("when_equipped");
        if (keyValue instanceof JsonArray whenEquipped) {
            while (whenEquipped.get(0) instanceof String) {
                String effectId = (String) whenEquipped.remove(0);
                RPGLEffect effect = RPGLFactory.newEffect(effectId);
                whenEquipped.add(effect.get("uuid"));
            }
            item.put("when_equipped", whenEquipped);
        }
    }

}
