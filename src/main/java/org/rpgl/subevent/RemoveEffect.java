package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This Subevent is dedicated to removing an RPGLEffect from an RPGLObject.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to remove an RPGLEffect from a RPGLObject
 * <br>
 * Target: an RPGLObject from whom an RPGLEffect is being removed
 *
 * @author Calvin Withun
 */
public class RemoveEffect extends Subevent implements CancelableSubevent {

    public RemoveEffect() {
        super("remove_effect");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new RemoveEffect();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new RemoveEffect();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public RemoveEffect invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (RemoveEffect) super.invoke(context, originPoint);
    }

    @Override
    public RemoveEffect joinSubeventData(JsonObject other) {
        return (RemoveEffect) super.joinSubeventData(other);
    }

    @Override
    public RemoveEffect prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("canceled", false);
        return this;
    }

    @Override
    public RemoveEffect run(RPGLContext context, JsonArray originPoint) {
        if (this.isNotCanceled()) {
            JsonArray effectTags = this.json.getJsonArray("effect_tags");
            JsonArray effects = super.getTarget().getEffects().deepClone();
            RPGLObject target = super.getTarget();
            for (int i = 0; i < effects.size(); i++) {
                String effectUuid = effects.getString(i);
                RPGLEffect effect = UUIDTable.getEffect(effectUuid);
                if (effect.hasTag("temporary") && effect.getTags().asList().containsAll(effectTags.asList())) {
                    target.removeEffect(effectUuid);
                }
            }
        }
        return this;
    }

    @Override
    public RemoveEffect setOriginItem(String originItem) {
        return (RemoveEffect) super.setOriginItem(originItem);
    }

    @Override
    public RemoveEffect setSource(RPGLObject source) {
        return (RemoveEffect) super.setSource(source);
    }

    @Override
    public RemoveEffect setTarget(RPGLObject target) {
        return (RemoveEffect) super.setTarget(target);
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
