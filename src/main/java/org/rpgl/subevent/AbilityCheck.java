package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
                this.putInteger("bonus", getSource().getAbilityModifierFromAbilityName(getAbility(context), context));
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

    public String getSkill() {
        return this.json.getString("skill");
    }

    public void giveHalfProficiency() {
        this.json.putBoolean("has_half_proficiency", true);
    }

    public void giveProficiency() {
        this.json.putBoolean("has_proficiency", true);
    }

    public void giveExpertise() {
        this.json.putBoolean("has_expertise", true);
    }

    public boolean hasHalfProficiency() {
        return this.json.getBoolean("has_half_proficiency")
                && !this.json.getBoolean("has_proficiency")
                && !this.json.getBoolean("has_expertise");
    }

    public boolean hasProficiency() {
        return this.json.getBoolean("has_proficiency") && !this.json.getBoolean("has_expertise");
    }

    public boolean hasExpertise() {
        return this.json.getBoolean("has_expertise");
    }

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
