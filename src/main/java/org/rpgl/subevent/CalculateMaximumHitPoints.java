package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;

/**
 * This Subevent is dedicated to calculating the maximum hit points of an RPGLObject.
 * <br>
 * <br>
 * Source: the RPGLObject whose maximum hit point value is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateMaximumHitPoints extends Calculation {

    public CalculateMaximumHitPoints() {
        super("calculate_maximum_hit_points");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateMaximumHitPoints();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateMaximumHitPoints();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        RPGLObject source = this.getSource();
        int sourceConModifier = source.getAbilityModifierFromAbilityName("con", context);
        super.setBase(source.getHealthData().getInteger("base"));
        super.addBonus(new JsonObject() {{
            this.putInteger("bonus", sourceConModifier * source.getLevel());
            this.putJsonArray("dice", new JsonArray());
        }});
    }

}
