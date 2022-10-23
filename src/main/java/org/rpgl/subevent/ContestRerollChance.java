package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class ContestRerollChance extends Subevent {

    private static final String SUBEVENT_ID = "contest_reroll_chance";

    static {
        Subevent.SUBEVENTS.put(SUBEVENT_ID, new ContestRerollChance());
    }

    public ContestRerollChance() {
        super(SUBEVENT_ID);
    }

    @Override
    public Subevent clone() {
        return new SavingThrow();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new ContestRerollChance();
        clone.joinSubeventJson(subeventJson);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) {
        this.subeventJson.put("allow_reroll", false);
    }

    public void triggerReroll(String rerollMode) {
        this.subeventJson.put("allow_reroll", true);
        this.subeventJson.put("reroll_mode", rerollMode);
    }

    public boolean wasRerollTriggered() {
        return (Boolean) this.subeventJson.get("allow_reroll");
    }

    public String getRerollMode() {
        return (String) this.subeventJson.get("reroll_mode");
    }

}
