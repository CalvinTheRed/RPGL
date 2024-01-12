package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;

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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilityContest();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        int sourceAbilityCheck = this.getSourceAbilityCheck(context, resources);
        int targetAbilityCheck = this.getTargetAbilityCheck(context, resources);

        if (sourceAbilityCheck < targetAbilityCheck) {
            this.resolveNestedSubevents("fail", context, resources);
        } else if (sourceAbilityCheck > targetAbilityCheck) {
            this.resolveNestedSubevents("pass", context, resources);
        }
    }

    /**
     * This helper method calculates the source object's ability check and returns the result.
     *
     * @param context the context in which the object makes its ability check.
     * @param resources a list of resources used to produce this subevent
     * @return the result of the ability check
     *
     * @throws Exception if an exception occurs
     */
    int getSourceAbilityCheck(RPGLContext context, List<RPGLResource> resources) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.seekString("source_check.ability"));
            this.putString("skill", json.seekString("source_check.skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.seekJsonArray("source_check.determined"));
        }});
        abilityCheck.setSource(super.getSource());
        abilityCheck.prepare(context, resources);
        abilityCheck.setTarget(super.getTarget());
        abilityCheck.invoke(context, resources);
        return abilityCheck.get();
    }

    /**
     * This helper method calculates the target object's ability check and returns the result.
     *
     * @param context the context in which the object makes its ability check.
     * @param resources a list of resources used to produce this subevent
     * @return the result of the ability check
     *
     * @throws Exception if an exception occurs
     */
    int getTargetAbilityCheck(RPGLContext context, List<RPGLResource> resources) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.seekString("target_check.ability"));
            this.putString("skill", json.seekString("target_check.skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.seekJsonArray("target_check.determined"));
        }});
        abilityCheck.setSource(super.getTarget());
        abilityCheck.prepare(context, resources);
        abilityCheck.setTarget(super.getSource());
        abilityCheck.invoke(context, resources);
        return abilityCheck.get();
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether
     * <code>source</code> passed or failed its ability contest.
     *
     * @param passOrFail a String indicating whether the ability contest was passed or failed
     * @param context the context this Subevent takes place in
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String passOrFail, RPGLContext context, List<RPGLResource> resources) throws Exception {
        JsonArray subeventJsonArray = this.json.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent subevent = Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson);
                subevent.setSource(super.getSource());
                subevent.prepare(context, resources);
                subevent.setTarget(super.getTarget());
                subevent.invoke(context, resources);
            }
        }
    }

}
