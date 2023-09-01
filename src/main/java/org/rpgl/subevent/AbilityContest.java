package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to resolving ability contests between two objects.
 * <br>
 * <br>
 * Source: an RPGLObject initiating an ability check
 * <br>
 * Target: an RPGLObject against whom an ability check is being initiated
 *
 * @author Calvin Withun
 */
public class AbilityContest extends Subevent {

    public AbilityContest() {
        super("ability_contest");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AbilityContest();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilityContest();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);

        // Add tag so nested subevents such as DamageCollection can know they hail from an ability save.
        this.addTag("ability_contest");
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        int sourceAbilityCheck = this.getSourceAbilityCheck(context);
        int targetAbilityCheck = this.getTargetAbilityCheck(context);

        if (sourceAbilityCheck < targetAbilityCheck) {
            this.resolveNestedSubevents("fail", context);
        } else if (sourceAbilityCheck > targetAbilityCheck) {
            this.resolveNestedSubevents("pass", context);
        }
    }

    int getSourceAbilityCheck(RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.seekString("source_check.ability"));
            this.putString("skill", json.seekString("source_check.skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.seekJsonArray("source_check.determined"));
        }});
        abilityCheck.setSource(this.getTarget());
        abilityCheck.prepare(context);
        abilityCheck.setTarget(this.getSource());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    int getTargetAbilityCheck(RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.seekString("target_check.ability"));
            this.putString("skill", json.seekString("target_check.skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.seekJsonArray("target_check.determined"));
        }});
        abilityCheck.setSource(this.getSource());
        abilityCheck.prepare(context);
        abilityCheck.setTarget(this.getTarget());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether
     * <code>source</code> passed or failed its ability contest.
     *
     * @param passOrFail a String indicating whether the ability contest was passed or failed
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String passOrFail, RPGLContext context) throws Exception {
        JsonArray subeventJsonArray = this.json.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent subevent = Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson);
                subevent.setSource(this.getSource());
                subevent.prepare(context);
                subevent.setTarget(this.getTarget());
                subevent.invoke(context);
            }
        }
    }

}
