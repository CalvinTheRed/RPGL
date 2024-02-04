package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Objects;

/**
 * This Condition is dedicated to evaluating whether any of an indicated set of equipment slots contain an item with a
 * given tag, or an item without that tag when inverted.
 *
 * @author Calvin Withun
 */
public class EquippedItemHasTag extends Condition {

    public EquippedItemHasTag() {
        super("equipped_item_has_tag");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        RPGLObject object = RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object"));
        JsonArray slots = conditionJson.getJsonArray("slot");
        boolean invert = Objects.requireNonNullElse(conditionJson.getBoolean("invert"), false);
        for (int i = 0; i < slots.size(); i++) {
            RPGLItem item = UUIDTable.getItem(object.getEquippedItems().getString(slots.getString(i)));
            if (item != null && item.hasTag(conditionJson.getString("tag")) != invert) {
                return true;
            }
        }
        return false;
    }

}
