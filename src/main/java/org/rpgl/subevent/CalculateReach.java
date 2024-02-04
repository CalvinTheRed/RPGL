package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This subevent is dedicated to calculating the reach of an RPGLObject.
 * <br>
 * <br>
 * Source: the RPGLObject whose reach is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateReach extends Calculation {

    public CalculateReach() {
        super("calculate_reach");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateReach();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateReach();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public CalculateReach invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CalculateReach) super.invoke(context, originPoint);
    }

    @Override
    public CalculateReach joinSubeventData(JsonObject other) {
        return (CalculateReach) super.joinSubeventData(other);
    }

    @Override
    public CalculateReach prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint).setBase(1);
        return this;
    }

    @Override
    public CalculateReach run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CalculateReach setOriginItem(String originItem) {
        return (CalculateReach) super.setOriginItem(originItem);
    }

    @Override
    public CalculateReach setSource(RPGLObject source) {
        return (CalculateReach) super.setSource(source);
    }

    @Override
    public CalculateReach setTarget(RPGLObject target) {
        return (CalculateReach) super.setTarget(target);
    }

}
