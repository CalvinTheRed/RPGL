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
     */
    public void addBonus(int bonus) {
        this.subeventJson.putInteger("bonus", Objects.requireNonNullElse(this.subeventJson.getInteger("bonus"), 0) + bonus);
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
     * 	<b><i>set</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void set(int value)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method sets the calculated value. Once the calculation is set, the returned value will not include any
     * 	bonuses which have been applied to it.
     * 	</p>
     */
    public void set(int value) {
        this.subeventJson.putInteger("set", value);
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
        Integer set = this.subeventJson.getInteger("set");
        Integer bonus = this.getBonus();
        if (set != null) {
            return set + bonus;
        }
        return Objects.requireNonNullElse(this.subeventJson.getInteger("base"), 0) + bonus; // TODO add methods to interface with "base"?
    }

}
