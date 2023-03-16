package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
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
        this.addTag("ability_check");
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        if (this.isNotCanceled()) {
            this.roll();
            this.addBonus(new JsonObject() {{
                this.putString("name", "Ability Modifier");
                this.putString("effect", null);
                this.putInteger("bonus", getSource().getAbilityModifierFromAbilityName(getAbility(context), context));
                this.putJsonArray("dice", new JsonArray());
                this.putBoolean("optional", false);
            }});
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
