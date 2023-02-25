package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonObject;

import java.util.Objects;

public class AbilityCheck extends Roll {

    public AbilityCheck() {
        super("ability_check");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AbilityCheck();
        clone.joinSubeventData(this.subeventJson);
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
        this.roll();
        this.addBonus(this.getSource().getAbilityModifierFromAbilityName(context, this.subeventJson.getString("ability")));

        GetAbilityCheckProficiency getAbilityCheckProficiency = new GetAbilityCheckProficiency();
        getAbilityCheckProficiency.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_ability_check_proficiency");
            this.putString("skill", Objects.requireNonNullElse(subeventJson.getString("skill"), ""));
        }});
        getAbilityCheckProficiency.setSource(this.getSource());
        getAbilityCheckProficiency.prepare(context);
        getAbilityCheckProficiency.setTarget(this.getTarget());
        getAbilityCheckProficiency.invoke(context);

        if (getAbilityCheckProficiency.isHalfProficient()) {
            this.addBonus(this.getSource().getProficiencyBonus() / 2);
        } else if (getAbilityCheckProficiency.isProficient()) {
            this.addBonus(this.getSource().getProficiencyBonus());
        } else if (getAbilityCheckProficiency.isExpert()) {
            this.addBonus(this.getSource().getProficiencyBonus() * 2);
        }
    }

}
