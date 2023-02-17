package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

/**
 * This abstract Subevent is dedicated to performing contest rolls. This includes ability checks, attack rolls, and
 * saving throws.
 * //TODO create a "fail" method to cause the contest roll to automatically fail (Dex saves while asleep for example)
 * <br>
 * <br>
 * Source: an RPGLObject making a contest roll
 * <br>
 * Target: an RPGLObject against whom a contest roll is being made
 *
 * @author Calvin Withun
 */
public abstract class ContestRoll extends Calculation {

    private int advantageCounter = 0;
    private int disadvantageCounter = 0;

    public ContestRoll(String subeventId) {
        super(subeventId);
    }

    /**
     * This method informs the subevent that advantage has been granted to the contest roll.
     */
    public void grantAdvantage() {
        this.advantageCounter++;
    }

    /**
     * This method informs the subevent that disadvantage has been granted to the contest roll.
     */
    public void grantDisadvantage() {
        this.disadvantageCounter++;
    }

    /**
     * This method returns true if the contest roll is being made with advantage. This only returns true if there is at
     * least one source of advantage and no sources of disadvantage.
     *
     * @return true if the contest roll is being made with advantage
     */
    public boolean isAdvantageRoll() {
        return this.advantageCounter > 0 && this.disadvantageCounter == 0;
    }

    /**
     * This method returns true if the contest roll is being made with disadvantage. This only returns true if there is
     * at least one source of disadvantage and no sources of advantage.
     *
     * @return true if the contest roll is being made with disadvantage
     */
    public boolean isDisadvantageRoll() {
        return this.disadvantageCounter > 0 && this.advantageCounter == 0;
    }

    /**
     * This method returns true if the contest roll is being made with neither advantage nor disadvantage. This
     * requires that there be no sources of advantage or disadvantage, or that there are at least one source for both
     * advantage and disadvantage.
     *
     * @return true if the contest roll is being made with neither advantage nor disadvantage
     */
    public boolean isNormalRoll() {
        return (this.advantageCounter == 0 && this.disadvantageCounter == 0)
                || (this.advantageCounter > 0 && this.disadvantageCounter > 0);
    }

    /**
     * This method rolls the d20 die (or dice) involved with the contest roll. The resulting roll is stored in the
     * Subevent json data, and it accounts for advantage and disadvantage.
     */
    public void roll() {
        JsonArray determined = this.subeventJson.getJsonArray("determined");
        int baseDieRoll = Die.roll(20, determined.asList());
        if (this.isAdvantageRoll()) {
            int advantageRoll = Die.roll(20, determined.asList());
            if (advantageRoll > baseDieRoll) {
                baseDieRoll = advantageRoll;
            }
        } else if (this.isDisadvantageRoll()) {
            int disadvantageRoll = Die.roll(20, determined.asList());
            if (disadvantageRoll < baseDieRoll) {
                baseDieRoll = disadvantageRoll;
            }
        }
        super.setBase(baseDieRoll);
    }

    /**
     * This method checks if the contest needs to be re-rolled for any reason, and performs the re-roll if it does.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    public void checkForReroll(RPGLContext context) throws Exception {
        ContestRerollChance contestRerollChance = new ContestRerollChance();
        Integer base = super.getBase();
        contestRerollChance.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_reroll_chance");
            this.putInteger("base_die_roll", base);
        }});
        contestRerollChance.setSource(this.getSource());
        contestRerollChance.prepare(context);
        contestRerollChance.setTarget(this.getTarget());
        contestRerollChance.invoke(context);

        if (contestRerollChance.wasRerollRequested()) {
            int rerollDieValue = Die.roll(20, this.subeventJson.getJsonArray("determined_reroll").asList());
            String rerollMode = contestRerollChance.getRerollMode();
            switch (rerollMode) {
                case ContestRerollChance.USE_NEW:
                    super.setBase(rerollDieValue);
                    break;
                case ContestRerollChance.USE_HIGHEST:
                    if (rerollDieValue > super.getBase()) {
                        super.setBase(rerollDieValue);
                    }
                    break;
                case ContestRerollChance.USE_LOWEST:
                    if (rerollDieValue < super.getBase()) {
                        super.setBase(rerollDieValue);
                    }
                    break;
                default:
                    // TODO log error here
                    throw new Exception("ContestRerollChance reroll_mode invalid: " + rerollMode);
            }
        }
    }

}
