package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class ContestRerollChance extends Subevent {

    public ContestRerollChance() {
        super("contest_reroll_chance");
    }

    @Override
    public Subevent clone() {
        return new SavingThrow();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new ContestRerollChance();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) {
        this.subeventJson.put("reroll_requested", false);
    }

    public void requestReroll(String rerollMode) {
        if (!(Boolean) this.subeventJson.get("reroll_requested")) {
            this.subeventJson.put("reroll_requested", true);
            this.subeventJson.put("reroll_mode", rerollMode);
        }
    }

    public boolean wasRerollRequested() {
        return (Boolean) this.subeventJson.get("reroll_requested");
    }

    public String getRerollMode() {
        return (String) this.subeventJson.get("reroll_mode");
    }

}
