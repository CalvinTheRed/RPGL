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
     * public void addBonus(long bonus)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds a bonus to the value being calculated.
     * 	</p>
     */
    public void addBonus(long bonus) {
        Long currentBonus = (Long) this.subeventJson.put("bonus", bonus);
        if (currentBonus == null) {
            currentBonus = 0L;
        }
        this.subeventJson.put("bonus", currentBonus + bonus);
    }

    /**
     * 	<p>
     * 	<b><i>getBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public long getBonus()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the bonus of the calculation.
     * 	</p>
     *
     *  @return the bonus granted to the calculation
     */
    public long getBonus() {
        return Objects.requireNonNullElse((Long) this.subeventJson.get("bonus"), 0L);
    }

    /**
     * 	<p>
     * 	<b><i>set</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void set(long value)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method sets the calculated value. Once the calculation is set, the returned value will not include any
     * 	bonuses which have been applied to it.
     * 	</p>
     */
    public void set(long value) {
        this.subeventJson.put("set", value);
    }

    /**
     * 	<p>
     * 	<b><i>get</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public long get()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the result of the calculation.
     * 	</p>
     *
     *  @return the result of the calculation
     */
    public long get() {
        Long set = (Long) this.subeventJson.get("set");
        Long bonus = this.getBonus();
        if (set != null) {
            return set + bonus;
        }
        return Objects.requireNonNullElse((Long) this.subeventJson.get("base"), 0L) + bonus; // TODO add methods to interface with "base"?
    }

}
