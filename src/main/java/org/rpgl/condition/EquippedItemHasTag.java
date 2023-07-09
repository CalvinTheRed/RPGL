package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This Condition is dedicated to evaluating whether an origin item has a particular tag.
 *
 * @author Calvin Withun
 */
public class EquippedItemHasTag extends Condition {

    public EquippedItemHasTag() {
        super("equipped_item_has_tag");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        RPGLObject object = RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object"));
        RPGLItem item = UUIDTable.getItem(object.getEquippedItems().getString(conditionJson.getString("slot")));
        return item != null && item.hasTag(conditionJson.getString("tag"));
    }

}
