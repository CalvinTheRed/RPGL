package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Objects;

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

    public void addHealing(JsonObject healingJson) {
        this.json.getJsonArray("dice").asList().addAll(healingJson.getJsonArray("dice").asList());
        this.json.putInteger("bonus", this.json.getInteger("bonus") + Objects.requireNonNullElse(healingJson.getInteger("bonus"), 0));
    }

    public JsonObject getHealingCollection() {
        return new JsonObject() {{
           this.putJsonArray("dice", json.getJsonArray("dice"));
           this.putInteger("bonus", json.getInteger("bonus"));
        }};
    }

}
