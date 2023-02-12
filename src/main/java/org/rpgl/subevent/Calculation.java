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
     * 	<p>
     * 	<b><i>addBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addBonus(int bonus)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds a bonus to the value being calculated.
     * 	</p>
     *
     *  @param bonus a bonus to be added to the calculation
     */
    public void addBonus(int bonus) {
        this.subeventJson.putInteger("bonus", this.getBonus() + bonus);
    }

    /**
     * 	<p>
     * 	<b><i>getBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int getBonus()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the bonus of the calculation.
     * 	</p>
     *
     *  @return the bonus granted to the calculation
     */
    public int getBonus() {
        return Objects.requireNonNullElse(this.subeventJson.getInteger("bonus"), 0);
    }

    /**
     * 	<p>
     * 	<b><i>setSet</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setSet(int value)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method sets the calculated value. Once the calculation is set, the returned value will not include any
     * 	bonuses which have been applied to it.
     * 	</p>
     *
     *  @param value the new set value of the calculation
     */
    public void setSet(int value) {
        this.subeventJson.putInteger("set", value);
    }

    /**
     * 	<p>
     * 	<b><i>getSet</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Integer getSet()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the set value of the calculation.
     * 	</p>
     *
     *  @return the set value of the calculation
     */
    public Integer getSet() {
        return this.subeventJson.getInteger("set");
    }

    /**
     * 	<p>
     * 	<b><i>setBase</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setBase(int value)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method sets the calculation's base value.
     * 	</p>
     *
     *  @param value the new base value of the calculation
     */
    public void setBase(int value) {
        this.subeventJson.putInteger("base", value);
    }

    /**
     * 	<p>
     * 	<b><i>getBase</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Integer getBase()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the base value of the calculation.
     * 	</p>
     *
     *  @return the base value of the calculation
     */
    public Integer getBase() {
        return this.subeventJson.getInteger("base");
    }

    /**
     * 	<p>
     * 	<b><i>get</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int get()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the result of the calculation.
     * 	</p>
     *
     *  @return the result of the calculation
     */
    public int get() {
        Integer set = this.getSet();
        if (set != null) {
            return set;
        }
        return Objects.requireNonNullElse(this.getBase(), 0) + this.getBonus();
    }

}
