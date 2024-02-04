package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
    public CriticalDamageConfirmation invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CriticalDamageConfirmation) super.invoke(context, originPoint);
    }

    @Override
    public CriticalDamageConfirmation joinSubeventData(JsonObject other) {
        return (CriticalDamageConfirmation) super.joinSubeventData(other);
    }

    @Override
    public CriticalDamageConfirmation prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        return this;
    }

    @Override
    public CriticalDamageConfirmation run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CriticalDamageConfirmation setOriginItem(String originItem) {
        return (CriticalDamageConfirmation) super.setOriginItem(originItem);
    }

    @Override
    public CriticalDamageConfirmation setSource(RPGLObject source) {
        return (CriticalDamageConfirmation) super.setSource(source);
    }

    @Override
    public CriticalDamageConfirmation setTarget(RPGLObject target) {
        return (CriticalDamageConfirmation) super.setTarget(target);
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
