package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.math.Die;

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
public abstract class Roll extends Calculation implements AbilitySubevent, CancelableSubevent {

    public Roll(String subeventId) {
        super(subeventId);
    }

    @Override
    public Roll prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        this.json.putBoolean("has_advantage", false);
        this.json.putBoolean("has_disadvantage", false);
        return this;
    }

    @Override
    public void cancel() {
        this.json.putBoolean("canceled", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !this.json.getBoolean("canceled");
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
        int baseDieRoll = Die.roll(20, determined);
        if (this.isAdvantageRoll()) {
            int advantageRoll = Die.roll(20, determined);
            if (advantageRoll > baseDieRoll) {
                baseDieRoll = advantageRoll;
            }
        } else if (this.isDisadvantageRoll()) {
            int disadvantageRoll = Die.roll(20, determined);
            if (disadvantageRoll < baseDieRoll) {
                baseDieRoll = disadvantageRoll;
            }
        }
        super.setBase(baseDieRoll);
    }

}
