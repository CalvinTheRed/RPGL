package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to performing ability checks and skill checks.
 * <br>
 * <br>
 * Source: an RPGLObject initiating an ability check
 * <br>
 * Target: an RPGLObject being required to make an ability check
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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilityCheck();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putBoolean("has_half_proficiency", false);
        this.json.putBoolean("has_proficiency", false);
        this.json.putBoolean("has_expertise", false);
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            this.addBonus(new JsonObject() {{
                this.putInteger("bonus", getTarget().getAbilityModifierFromAbilityName(getAbility(context), context));
                this.putJsonArray("dice", new JsonArray());
            }});
            this.addBonus(new JsonObject() {{
                this.putInteger("bonus", getProficiencyBonus());
                this.putJsonArray("dice", new JsonArray());
            }});
        }
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

    /**
     * This helper method returns the proficiency bonus which should be applied to the check, after considering half
     * proficiency and expertise.
     *
     * @return a proficiency bonus
     */
    int getProficiencyBonus() {
        if (this.json.getBoolean("has_expertise")) {
            return this.getSource().getProficiencyBonus() * 2;
        } else if (this.json.getBoolean("has_proficiency")) {
            return this.getSource().getProficiencyBonus();
        } else if (this.json.getBoolean("has_half_proficiency")) {
            return this.getSource().getProficiencyBonus() / 2;
        } else {
            return 0;
        }
    }

}
