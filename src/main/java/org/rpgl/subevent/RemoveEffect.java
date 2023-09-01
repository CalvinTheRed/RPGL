package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Objects;

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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new RemoveEffect();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putBoolean("cancel", false);
    }

    @Override
    public void run(RPGLContext context) {
        if (this.isNotCanceled()) {
            JsonArray effectTags = this.json.getJsonArray("effect_tags");
            JsonArray effects = this.getTarget().getEffects().deepClone();
            RPGLObject target = this.getTarget();
            for (int i = 0; i < effects.size(); i++) {
                String effectUuid = effects.getString(i);
                RPGLEffect effect = UUIDTable.getEffect(effectUuid);
                if (effect.hasTag("temporary") && effect.getTags().asList().containsAll(effectTags.asList())) {
                    target.removeEffect(effectUuid);
                }
            }
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
