package org.rpgl.subevent;

import java.util.Objects;

/**
 * This abstract subevent is dedicated to performing a calculation. Subevents which extend this class can add bonuses to
 * a value, set the calculated value to a particular value, and retrieve the final calculated value.
 * <br>
 * <br>
 * Source: the RPGLObject whose base armor class is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public abstract class Calculation extends Subevent {

    public Calculation(String subeventId) {
        super(subeventId);
    }

    /**
     * This method adds a bonus to the value being calculated.
     *
     * @param bonus a bonus to be added to the calculation
     */
    public void addBonus(int bonus) {
        this.subeventJson.putInteger("bonus", this.getBonus() + bonus);
    }

    /**
     * This method returns the bonus of the calculation.
     *
     * @return the bonus granted to the calculation
     */
    public int getBonus() {
        return Objects.requireNonNullElse(this.subeventJson.getInteger("bonus"), 0);
    }

    /**
     * This method sets the calculated value. Once the calculation is set, the returned value will not include any
     * bonuses which have been applied to it.
     *
     * @param value the new set value of the calculation
     */
    public void setSet(int value) {
        this.subeventJson.putInteger("set", value);
    }

    /**
     * This method returns the set value of the calculation.
     *
     * @return the set value of the calculation
     */
    public Integer getSet() {
        return this.subeventJson.getInteger("set");
    }

    /**
     * This method sets the calculation's base value.
     *
     * @param value the new base value of the calculation
     */
    public void setBase(int value) {
        this.subeventJson.putInteger("base", value);
    }

    /**
     * This method returns the base value of the calculation.
     *
     * @return the base value of the calculation
     */
    public Integer getBase() {
        return this.subeventJson.getInteger("base");
    }

    /**
     * This method returns the result of the calculation.
     *
     * @return the result of the calculation
     */
    public int get() {
        Integer set = this.getSet();
        if (set != null) {
            return set;
        }
        return Objects.requireNonNullElse(this.getBase(), 0) + this.getBonus();
    }

}
