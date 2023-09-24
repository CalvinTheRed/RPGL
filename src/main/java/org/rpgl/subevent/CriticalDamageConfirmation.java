package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * This Subevent is dedicated to confirming that a critical hit deals critical damage.
 * TODO can this be a specialized InfoSubevent?
 * <br>
 * <br>
 * Source: an RPGLObject scoring a critical hit
 * <br>
 * Target: an RPGLObject suffering a critical hit
 *
 * @author Calvin Withun
 */
public class CriticalDamageConfirmation extends Subevent implements CancelableSubevent {

    public CriticalDamageConfirmation() {
        super("critical_damage_confirmation");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalDamageConfirmation();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CriticalDamageConfirmation();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.putBoolean("cancel", false);
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
