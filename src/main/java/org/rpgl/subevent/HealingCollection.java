package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This Subevent is dedicated to collecting unrolled healing dice and bonuses.
 * <br>
 * <br>
 * Source: an RPGLObject preparing to perform healing
 * <br>
 * Target: an RPGLObject which will later receive the collected healing
 *
 * @author Calvin Withun
 */
public class HealingCollection extends Subevent {

    public HealingCollection() {
        super("healing_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new HealingCollection();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new HealingCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.asMap().putIfAbsent("healing", new ArrayList<>());
        this.prepareHealing(context);
    }

    /**
     * This helper method evaluates the healing formulas provided in the Subevent JSON data and stores the result.
     *
     * @param context the context in which the Subevent is being prepared
     *
     * @throws Exception if an exception occurs
     */
    void prepareHealing(RPGLContext context) throws Exception {
        JsonArray healingArray = this.json.removeJsonArray("healing");
        this.json.putJsonArray("healing", new JsonArray());
        if (healingArray != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(super.getSource());
            effect.setTarget(super.getSource());
            for (int i = 0; i < healingArray.size(); i++) {
                JsonObject healingJson = healingArray.getJsonObject(i);
                this.addHealing(Calculation.processBonusJson(effect, this, healingJson, context));
            }
        }
    }

    /**
     * Adds dice and/or a bonus to the healing collected by this Subevent.
     *
     * @param healingJson healing data to be added to the collection
     */
    public void addHealing(JsonObject healingJson) {
        this.getHealingCollection().addJsonObject(healingJson);
    }

    /**
     * Returns the collection of healing gathered by this Subevent.
     *
     * @return a JsonArray storing healing dice and a healing bonus
     */
    public JsonArray getHealingCollection() {
        return this.json.getJsonArray("healing");
    }

}
