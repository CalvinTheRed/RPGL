package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition returns true if the indicated object is wielding the origin item of the effect, if the effect has an
 * origin item.
 *
 * @author Calvin Withun
 */
public class ObjectWieldingOriginItem extends Condition {

    public ObjectWieldingOriginItem() {
        super("object_wielding_origin_item");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        RPGLObject object = RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object"));
        JsonObject equippedItems = object.getEquippedItems();
        String originItem = effect.getOriginItem();
        return originItem != null && equippedItems.asMap().containsValue(originItem);
    }

}
