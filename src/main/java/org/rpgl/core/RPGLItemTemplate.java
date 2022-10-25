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
        processWhileEquipped(item);
        UUIDTable.register(item);
        return item;
    }

    /**
     * This helper method converts effectId's in an RPGLItemTemplate's while_equipped array to RPGLEffects. The UUID's of
     * these new RPGLEffects replace the original array contents.
     *
     * @param item the item being processed.
     */
    private static void processWhileEquipped(RPGLItem item) {
        Object keyValue = item.remove("while_equipped");
        JsonArray whileEquippedIdArray = (JsonArray) keyValue;
        JsonArray whileEquippedUuidArray = new JsonArray();
        for (Object whileEquippedIdElement : whileEquippedIdArray) {
            String effectId = (String) whileEquippedIdElement;
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            whileEquippedUuidArray.add(effect.get("uuid"));
        }
        item.put("while_equipped", whileEquippedUuidArray);
    }

}
