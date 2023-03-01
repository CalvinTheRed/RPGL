package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract Subevent is dedicated to performing rolls. This includes ability checks, attack rolls, and saving throws.
 * //TODO create a "fail" method to cause the contest roll to automatically fail (Dex saves while asleep for example)
 * <br>
 * <br>
 * Source: an RPGLObject making a roll
 * <br>
 * Target: an RPGLObject against whom a roll is being made
 *
 * @author Calvin Withun
 */
public abstract class Roll extends Calculation implements AbilitySubevent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Roll.class);

    public Roll(String subeventId) {
        super(subeventId);
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putBoolean("has_advantage", false);
        this.json.putBoolean("has_disadvantage", false);
    }

    /**
     * This method informs the subevent that advantage has been granted to the contest roll.
     */
    public void grantAdvantage() {
        this.json.putBoolean("has_advantage", true);
    }

    /**
     * This method informs the subevent that disadvantage has been granted to the contest roll.
     */
    public void grantDisadvantage() {
        this.json.putBoolean("has_disadvantage", true);
    }

    /**
     * This method returns true if the contest roll is being made with advantage. This only returns true if there is at
     * least one source of advantage and no sources of disadvantage.
     *
     * @return true if the contest roll is being made with advantage
     */
    public boolean isAdvantageRoll() {
        return this.json.getBoolean("has_advantage") && !this.json.getBoolean("has_disadvantage");
    }

    /**
     * This method returns true if the contest roll is being made with disadvantage. This only returns true if there is
     * at least one source of disadvantage and no sources of advantage.
     *
     * @return true if the contest roll is being made with disadvantage
     */
    public boolean isDisadvantageRoll() {
        return this.json.getBoolean("has_disadvantage") && !this.json.getBoolean("has_advantage");
    }

    /**
     * This method returns true if the contest roll is being made with neither advantage nor disadvantage. This
     * requires that there be no sources of advantage or disadvantage, or that there are at least one source for both
     * advantage and disadvantage.
     *
     * @return true if the contest roll is being made with neither advantage nor disadvantage
     */
    public boolean isNormalRoll() {
        return this.json.getBoolean("has_advantage") == this.json.getBoolean("has_disadvantage");
    }

    /**
     * This method rolls the d20 die (or dice) involved with the contest roll. The resulting roll is stored in the
     * Subevent json data, and it accounts for advantage and disadvantage.
     */
    public void roll() {
        JsonArray determined = this.json.getJsonArray("determined");
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
        RollRerollChance rollRerollChance = new RollRerollChance();
        Integer base = super.getBase();
        rollRerollChance.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_reroll_chance");
            this.putInteger("base_die_roll", base);
        }});
        rollRerollChance.setSource(this.getSource());
        rollRerollChance.prepare(context);
        rollRerollChance.setTarget(this.getTarget());
        rollRerollChance.invoke(context);

        if (rollRerollChance.wasRerollRequested()) {
            int rerollDieValue = Die.roll(20, this.json.getJsonArray("determined_reroll").asList());
            String rerollMode = rollRerollChance.getRerollMode();
            switch (rerollMode) {
                case RollRerollChance.USE_NEW:
                    super.setBase(rerollDieValue);
                    break;
                case RollRerollChance.USE_HIGHEST:
                    if (rerollDieValue > super.getBase()) {
                        super.setBase(rerollDieValue);
                    }
                    break;
                case RollRerollChance.USE_LOWEST:
                    if (rerollDieValue < super.getBase()) {
                        super.setBase(rerollDieValue);
                    }
                    break;
                default:
                    Exception e = new Exception("ContestRerollChance reroll_mode invalid: " + rerollMode);
                    LOGGER.error(e.getMessage());
                    throw e;
            }
        }
    }

}
