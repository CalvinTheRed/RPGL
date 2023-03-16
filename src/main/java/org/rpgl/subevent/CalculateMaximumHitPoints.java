package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        RPGLObject source = this.getSource();
        JsonObject sourceHealthData = source.getHealthData();
        int sourceConModifier = source.getAbilityModifierFromAbilityName("con", context);
        int sourceHitDiceCount = sourceHealthData.getJsonArray("hit_dice").size();
        super.setBase(new JsonObject() {{
            this.putString("name", "Base Hit Point Maximum");
            this.putString("effect", null);
            this.putInteger("value", sourceHealthData.getInteger("base"));
        }});
        super.addBonus(new JsonObject() {{
            this.putString("name", "Constitution Bonus");
            this.putString("effect", null);
            this.putInteger("bonus", sourceConModifier * sourceHitDiceCount);
            this.putJsonArray("dice", new JsonArray());
            this.putBoolean("optional", false);
        }});
    }

}
