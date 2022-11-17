package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;

import java.util.Objects;

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
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        if (!this.isCancelled()) {
            RPGLEffect effect = RPGLFactory.newEffect((String) this.subeventJson.get("effect"));
            if (effect != null) {
                this.getTarget().addEffect(effect);
            }
        }
    }

    public void cancel() {
        this.subeventJson.put("cancel", true);
    }

    boolean isCancelled() {
        return Objects.requireNonNullElse((Boolean) this.subeventJson.get("cancel"), false);
    }

}
