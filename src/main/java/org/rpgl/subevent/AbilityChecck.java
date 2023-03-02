package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This subevent is dedicated to performing an ability check. AbilityCheck by itself does not cause anything to happen,
 * it is just a roll calculation. It must be used as a part of a Contest to cause something to happen.
 * <br>
 * <br>
 * Source: an RPGLObject performing an ability check
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class AbilityChecck extends Roll {

    public AbilityChecck() {
        super("ability_check");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AbilityChecck();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilityChecck();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.addTag("ability_check");
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        this.roll();
        this.addBonus(this.getSource().getAbilityModifierFromAbilityName(context, this.json.getString("ability")));

        GetAbilityCheckProficiency getAbilityCheckProficiency = new GetAbilityCheckProficiency();
        getAbilityCheckProficiency.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_ability_check_proficiency");
            this.putString("skill", Objects.requireNonNullElse(json.getString("skill"), "")); // TODO accommodate tools?
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        getAbilityCheckProficiency.setSource(this.getSource());
        getAbilityCheckProficiency.prepare(context);
        getAbilityCheckProficiency.setTarget(this.getSource());
        getAbilityCheckProficiency.invoke(context);

        if (getAbilityCheckProficiency.isHalfProficient()) {
            this.addBonus(this.getSource().getProficiencyBonus() / 2);
        } else if (getAbilityCheckProficiency.isProficient()) {
            this.addBonus(this.getSource().getProficiencyBonus());
        } else if (getAbilityCheckProficiency.isExpert()) {
            this.addBonus(this.getSource().getProficiencyBonus() * 2);
        }
    }

    @Override
    public String getAbility(RPGLContext context) {
        return super.json.getString("ability");
    }

    /**
     * Returns the skill used in the ability check, or an empty string if no skill was used.
     *
     * @return a String indicating a skill, or an empty string
     */
    public String getSkill() {
        return Objects.requireNonNullElse(this.json.getString("skill"), "");
    }

}
