package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to handling RPGLObject movement.
 * <br>
 * <br>
 * Source: an RPGLObject prompting movement
 * <br>
 * Target: an RPGLObject being moved
 *
 * @author Calvin Withun
 */
public class Movement extends Subevent {

    public Movement() {
        super("movement");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new Movement();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new Movement();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Movement invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (Movement) super.invoke(context, originPoint);
    }

    @Override
    public Movement joinSubeventData(JsonObject other) {
        return (Movement) super.joinSubeventData(other);
    }

    @Override
    public Movement prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        return (Movement) super.prepare(context, originPoint);
    }

    @Override
    public Movement run(RPGLContext context, JsonArray originPoint) throws Exception {
        this.getTarget().setPosition(originPoint.deepClone());
        return this;
    }

    @Override
    public Movement setOriginItem(String originItem) {
        return (Movement) super.setOriginItem(originItem);
    }

    @Override
    public Movement setSource(RPGLObject source) {
        return (Movement) super.setSource(source);
    }

    @Override
    public Movement setTarget(RPGLObject target) {
        return (Movement) super.setTarget(target);
    }

}
