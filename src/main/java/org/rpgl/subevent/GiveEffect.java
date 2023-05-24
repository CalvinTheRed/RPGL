package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This Subevent is dedicated to assigning an RPGLEffect to an RPGLObject.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to apply an RPGLEffect to another RPGLObject
 * <br>
 * Target: an RPGLObject to whom an RPGLEffect is being applied
 *
 * @author Calvin Withun
 */
public class GiveEffect extends Subevent implements CancelableSubevent {

    public GiveEffect() {
        super("give_effect");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GiveEffect();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GiveEffect();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context) {
        if (this.isNotCanceled()) {
            RPGLEffect effect = RPGLFactory.newEffect(this.json.getString("effect"));
            effect.setSource(this.getSource());
            effect.setTarget(this.getTarget());
            this.getTarget().addEffect(effect);
        }
    }

    @Override
    public void cancel() {
        this.json.putBoolean("cancel", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !Objects.requireNonNullElse(this.json.getBoolean("cancel"), false);
    }

}
