package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Objects;

/**
 * This Condition is dedicated to evaluating whether an origin item has a particular tag.
 *
 * @author Calvin Withun
 */
public class OriginItemHasTag extends Condition {

    public OriginItemHasTag() {
        super("origin_item_has_tag");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        String originItemAlias = conditionJson.getString("origin_item");
        RPGLItem originItem = null;
        if (Objects.equals(originItemAlias, "subevent")) {
            originItem = UUIDTable.getItem(subevent.getOriginItem());
        } else if (Objects.equals(originItemAlias, "effect")) {
            originItem = UUIDTable.getItem(effect.getOriginItem());
        }
        if (originItem != null) {
            return originItem.hasTag(conditionJson.getString("tag"));
        }
        return false;
    }

}
