package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to rolling all dice which will be copied to all targets of a damaging RPGLEvent. This
 * Subevent is typically only created within a SavingThrow Subevent.
 * <br>
 * <br>
 * Source: the RPGLObject whose ability score is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateAbilityScore extends Calculation implements AbilitySubevent {

    public CalculateAbilityScore() {
        super("calculate_ability_score");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.setBase(new JsonObject() {{
            this.putInteger("value", getSource().getJsonObject(RPGLObjectTO.ABILITY_SCORES_ALIAS).getInteger(getAbility(context)));
        }});
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("ability");
    }

}
