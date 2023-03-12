package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition evaluates true if the subevent contains a specified tag.
 *
 * @author Calvin Withun
 */
public class SubeventHasTag extends Condition {

    public SubeventHasTag() {
        super("subevent_has_tag");
    }

    @Override
    public boolean evaluate(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        this.verifyCondition(conditionJson);
        return subevent.hasTag(conditionJson.getString("tag"));
    }

}
