package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to evaluating whether a particular RPGLObject has a particular tag.
 *
 * @author Calvin Withun
 */
public class ObjectHasTag extends Condition {

    public ObjectHasTag() {
        super("object_has_tag");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        RPGLObject object = RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object"));
        return object.getAllTags(context).contains(conditionJson.getString("tag"));
    }

}
