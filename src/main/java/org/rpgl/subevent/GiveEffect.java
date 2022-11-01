package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;

public class GiveEffect extends Subevent {

    public GiveEffect() {
        super("give_effect");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GiveEffect();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new GiveEffect();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) {
        this.subeventJson.put("cancel", false);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        if (!(Boolean) this.subeventJson.get("cancel")) {
            RPGLEffect effect = RPGLFactory.newEffect((String) this.subeventJson.get("effect"));
            this.getTarget().addEffect(effect);
        }
    }

    public void cancel() {
        this.subeventJson.put("cancel", true);
    }

}
