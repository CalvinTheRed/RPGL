package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Objects;

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
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        if (this.json.getJsonArray("dice") == null) {
            this.json.putJsonArray("dice", new JsonArray());
        }
        if (this.json.getInteger("bonus") == null) {
            this.json.putInteger("bonus", 0);
        }
    }

    /**
     * Adds dice and/or a bonus to the healing collected by this Subevent.
     *
     * @param healingJson healing data to be added to the collection
     */
    public void addHealing(JsonObject healingJson) {
        this.json.getJsonArray("dice").asList().addAll(healingJson.getJsonArray("dice").asList());
        this.json.putInteger("bonus", this.json.getInteger("bonus") + Objects.requireNonNullElse(healingJson.getInteger("bonus"), 0));
    }

    /**
     * Returns the collection of healing gathered by this Subevent.
     *
     * @return an object storing healing dice and a healing bonus
     */
    public JsonObject getHealingCollection() {
        return new JsonObject() {{
           this.putJsonArray("dice", json.getJsonArray("dice"));
           this.putInteger("bonus", json.getInteger("bonus"));
        }};
    }

}
