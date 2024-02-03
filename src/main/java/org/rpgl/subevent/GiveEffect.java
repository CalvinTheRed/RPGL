package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;

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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GiveEffect();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        this.json.asMap().putIfAbsent("effect_bonuses", new ArrayList<>());
    }

    @Override
    public void run(RPGLContext context, JsonArray originPoint) {
        if (this.isNotCanceled()) {
            RPGLEffect effect = RPGLFactory
                    .newEffect(this.json.getString("effect"), this.json.getJsonArray("effect_bonuses"))
                    .setOriginItem(super.getOriginItem())
                    .setSource(super.getSource())
                    .setTarget(super.getTarget())
                    .setOriginItem(super.getOriginItem());
            effect.addTag("temporary");
            super.getTarget().addEffect(effect);
        }
    }

    @Override
    public void cancel() {
        this.json.putBoolean("canceled", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !this.json.getBoolean("canceled");
    }

}
