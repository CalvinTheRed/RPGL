package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLContext;
import org.rpgl.math.Die;

public abstract class ContestRoll extends Calculation {

    private int advantageCounter = 0;
    private int disadvantageCounter = 0;

    public ContestRoll(String subeventId) {
        super(subeventId);
    }

    public void grantAdvantage() {
        this.advantageCounter++;
    }

    public void grantDisadvantage() {
        this.disadvantageCounter++;
    }

    public boolean advantageRoll() {
        return this.advantageCounter > 0 && this.disadvantageCounter == 0;
    }

    public boolean disadvantageRoll() {
        return this.disadvantageCounter > 0 && this.advantageCounter == 0;
    }

    public boolean normalRoll() {
        return (this.advantageCounter == 0 && this.disadvantageCounter == 0)
                || (this.advantageCounter > 0 && this.disadvantageCounter > 0);
    }

    public void roll() {
        long baseDieRoll = Die.roll(20L, (Long) this.subeventJson.get("determined"));
        if (this.advantageRoll()) {
            long advantageRoll = Die.roll(20L, (Long) this.subeventJson.get("determined_second"));
            if (advantageRoll > baseDieRoll) {
                baseDieRoll = advantageRoll;
            }
        } else if (this.disadvantageRoll()) {
            long disadvantageRoll = Die.roll(20L, (Long) this.subeventJson.get("determined_second"));
            if (disadvantageRoll < baseDieRoll) {
                baseDieRoll = disadvantageRoll;
            }
        }
        this.subeventJson.put("base", baseDieRoll);
    }

    public boolean checkForReroll(RPGLContext context) throws Exception {
        ContestRerollChance contestRerollChance = new ContestRerollChance();
        String contestRerollChanceJsonString = String.format("""
                        {
                            "subevent": "contest_reroll_chance",
                            "base_die_roll": %s
                        }
                        """,
                this.subeventJson.get("base").toString()
        );
        JsonObject contestRerollChanceJson = JsonParser.parseObjectString(contestRerollChanceJsonString);
        contestRerollChance.joinSubeventJson(contestRerollChanceJson);
        contestRerollChance.prepare(context);
        contestRerollChance.invoke(context);

        if (contestRerollChance.wasRerollRequested()) {
            long rerollDieValue = Die.roll(20L, (Long) this.subeventJson.get("determined_reroll"));
            String rerollMode = contestRerollChance.getRerollMode();
            switch (rerollMode) {
                case "use_new":
                    this.subeventJson.put("base", rerollDieValue);
                    break;
                case "use_highest":
                    if (rerollDieValue > (Long) this.subeventJson.get("base")) {
                        this.subeventJson.put("base", rerollDieValue);
                    }
                    break;
                case "use_lowest":
                    if (rerollDieValue < (Long) this.subeventJson.get("base")) {
                        this.subeventJson.put("base", rerollDieValue);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

}
