package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;

/**
 * This Subevent is dedicated to confirming that a critical hit deals critical damage.
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CriticalDamageConfirmation();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.putBoolean("canceled", false);
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
