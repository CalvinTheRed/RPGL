package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLObject;

public class AttackRoll extends ContestRoll {

    public AttackRoll() {
        super("attack_roll");
    }

    @Override
    public Subevent clone() {
        return new SavingThrow();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new AttackRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) throws Exception {
        super.prepare(source);
        this.addProficiencyIfApplicable(source);
    }

    @Override
    public void invoke(RPGLObject source, RPGLObject target) throws Exception {
        super.invoke(source, target);
        this.calculateArmorClass(source, target);
        this.roll();
        this.checkForReroll(source, target);
        // TODO calculate crit threshold here
        if (this.getRoll() < (Long) this.subeventJson.get("save_difficulty_class")) {
            this.resolve(source, target, "miss");
        } else {
            this.resolve(source, target, "hit");
        }
    }

    void calculateArmorClass(RPGLObject source, RPGLObject target) throws Exception {
        CalculateArmorClass calculateArmorClass = new CalculateArmorClass();
        String calculateArmorClassJsonString = "{" +
                "\"subevent\":\"calculate_armor_class\"" +
                "}";
        JsonObject calculateArmorClassJson = JsonParser.parseObjectString(calculateArmorClassJsonString);
        calculateArmorClass.joinSubeventJson(calculateArmorClassJson);
        calculateArmorClass.prepare(source);
        calculateArmorClass.invoke(source, target);
        this.subeventJson.put("armor_class", calculateArmorClass.get());
    }

    void resolve(RPGLObject source, RPGLObject target, String resolution) throws Exception {
        JsonArray subeventJsonArray = (JsonArray) this.subeventJson.get(resolution);
        if (subeventJsonArray != null) {
            for (Object subeventJsonElement : subeventJsonArray) {
                JsonObject subeventJson = (JsonObject) subeventJsonElement;
                String subeventId = (String) subeventJson.get("subevent");
                Subevent subevent = Subevent.SUBEVENTS.get(subeventId);
                subevent.clone(subeventJson).invoke(source, target);
            }
        }
    }

    void addProficiencyIfApplicable(RPGLObject source) {
        // TODO write this method...
        /*
         * Proficiency is applied to an attack roll if any of the following conditions are met:
         * 1. the attack is an unarmed strike (selected weapon is null)
         * 2. the attack is made using a weapon with which the RPGLObject is proficient
         *     - note that all RPGLObjects are proficient with natural weapons
         *     - (the selected weapon's weapon group is one for which the RPGLObject has proficiency)
         * 3. the attack is a spell attack
         */
    }
}
