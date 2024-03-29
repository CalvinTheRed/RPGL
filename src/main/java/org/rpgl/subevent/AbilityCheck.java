package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.function.AddBonus;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to performing ability checks and skill checks.
 * <br>
 * <br>
 * Source: an RPGLObject initiating an ability check
 * <br>
 * Target: same as source, or an RPGLObject against whom the ability check is being made if different
 *
 * @author Calvin Withun
 */
public class AbilityCheck extends Roll {

    public AbilityCheck() {
        super("ability_check");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AbilityCheck();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilityCheck();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public AbilityCheck invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (AbilityCheck) super.invoke(context, originPoint);
    }

    @Override
    public AbilityCheck joinSubeventData(JsonObject other) {
        return (AbilityCheck) super.joinSubeventData(other);
    }

    @Override
    public AbilityCheck prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("has_half_proficiency", false);
        this.json.putBoolean("has_proficiency", false);
        this.json.putBoolean("has_expertise", false);
        return this;
    }

    @Override
    public AbilityCheck run(RPGLContext context, JsonArray originPoint) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            new AddBonus().execute(null, this, new JsonObject() {{
                /*{
                    "function": "add_bonus",
                    "bonus": [
                        {
                            "formula": "modifier",
                            "ability": <getAbility>
                            "object": {
                                "from": "subevent",
                                "object": "source"
                            }
                        },
                        {
                            "formula": "proficiency",
                            "object": {
                                "from": "subevent",
                                "object": "source"
                            },
                            "scale": {
                                "numerator": 0 | 1 | 2,
                                "denominator": 1 | 2,
                                "round_up": false
                            }
                        }
                    ]
                }*/
                this.putString("function", "add_bonus");
                this.putJsonArray("bonus", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "modifier");
                        this.putString("ability", getAbility(context));
                        this.putJsonObject("object", new JsonObject() {{
                            this.putString("from", "subevent");
                            this.putString("object", "source");
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "proficiency");
                        this.putJsonObject("object", new JsonObject() {{
                            this.putString("from", "subevent");
                            this.putString("object", "source");
                        }});
                        this.putJsonObject("scale", new JsonObject() {{
                            this.putInteger("numerator", 0);
                            this.putInteger("denominator", 1);
                            this.putBoolean("round_up", false);
                            if (json.getBoolean("has_expertise")) {
                                this.putInteger("numerator", 2);
                            } else if (json.getBoolean("has_proficiency")) {
                                this.putInteger("numerator", 1);
                            } else if (json.getBoolean("has_half_proficiency")) {
                                this.putInteger("numerator", 1);
                                this.putInteger("denominator", 2);
                            }
                        }});
                    }});
                }});
            }}, context, originPoint);
        }
        return this;
    }

    @Override
    public AbilityCheck setOriginItem(String originItem) {
        return (AbilityCheck) super.setOriginItem(originItem);
    }

    @Override
    public AbilityCheck setSource(RPGLObject source) {
        return (AbilityCheck) super.setSource(source);
    }

    @Override
    public AbilityCheck setTarget(RPGLObject target) {
        return (AbilityCheck) super.setTarget(target);
    }

    @Override
    public AbilityCheck grantAdvantage() {
        return (AbilityCheck) super.grantAdvantage();
    }

    @Override
    public AbilityCheck grantDisadvantage() {
        return (AbilityCheck) super.grantDisadvantage();
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("ability");
    }

    /**
     * Returns the skill used by the ability check.
     *
     * @return the skill used by the subevent, or null if no skill is used
     */
    public String getSkill() {
        return this.json.getString("skill");
    }

    /**
     * Indicate that the check should benefit from half proficiency.
     */
    public void giveHalfProficiency() {
        this.json.putBoolean("has_half_proficiency", true);
    }

    /**
     * Indicate that the check should benefit from proficiency.
     */
    public void giveProficiency() {
        this.json.putBoolean("has_proficiency", true);
    }

    /**
     * Indicate that the check should benefit from expertise.
     */
    public void giveExpertise() {
        this.json.putBoolean("has_expertise", true);
    }

    // TODO are the below even used for anything?

    /**
     * Returns whether the subevent should add half proficiency as a bonus for the ability check.
     *
     * @return if half proficiency should be added as a bonus to the check
     */
    public boolean hasHalfProficiency() {
        return this.json.getBoolean("has_half_proficiency")
                && !this.json.getBoolean("has_proficiency")
                && !this.json.getBoolean("has_expertise");
    }

    /**
     * Returns whether the subevent should add proficiency as a bonus for the ability check.
     *
     * @return if proficiency should be added as a bonus to the check
     */
    public boolean hasProficiency() {
        return this.json.getBoolean("has_proficiency") && !this.json.getBoolean("has_expertise");
    }

    /**
     * Returns whether the subevent should add expertise as a bonus for the ability check.
     *
     * @return if expertise should be added as a bonus to the check
     */
    public boolean hasExpertise() {
        return this.json.getBoolean("has_expertise");
    }

}
