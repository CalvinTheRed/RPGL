package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.function.AddBonus;
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateMaximumHitPoints();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        RPGLObject source = super.getSource();
        super.setBase(source.getHealthData().getInteger("base"));
        new AddBonus().execute(null, this, new JsonObject() {{
                /*{
                    "function": "add_bonus",
                    "bonus": [
                        {
                            "formula": "range",
                            "dice": [ ],
                            "bonus": <con health contribution>
                        }
                    ]
                }*/
            this.putString("function", "add_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", source.getAbilityModifierFromAbilityName("con", context) * source.getLevel());
                }});
            }});
        }}, context, originPoint);
    }

}
