package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This Subevent is dedicated to determining whether a contest roll should be re-rolled.
 * <br>
 * <br>
 * Source: an RPGLObject making a contest roll
 * <br>
 * Target: an RPGLObject against whom a contest roll is being made
 *
 * @author Calvin Withun
 */
public class ContestRerollChance extends Subevent {

    public static final String USE_HIGHEST = "use_highest";
    public static final String USE_LOWEST = "use_lowest";
    public static final String USE_NEW = "use_new";

    public ContestRerollChance() {
        super("contest_reroll_chance");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new ContestRerollChance();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new ContestRerollChance();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) {
        this.subeventJson.putBoolean("reroll_requested", false);
    }

    /**
     * 	<p>
     * 	<b><i>requestReroll</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void requestReroll(String rerollMode)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the subevent that a re-roll has been requested, and which type of re-roll was requested.
     * 	</p>
     *
     *  @param rerollMode the type of reroll which was requested (<code>"use_new", "use_highest", "use_lowest"</code>).
     */
    public void requestReroll(String rerollMode) {
        if (!Objects.requireNonNullElse(this.subeventJson.getBoolean("reroll_requested"), false)) {
            this.subeventJson.putBoolean("reroll_requested", true);
            this.subeventJson.putString("reroll_mode", rerollMode);
        }
    }

    /**
     * 	<p>
     * 	<b><i>prepareAttackWithoutWeapon</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean wasRerollRequested()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether a re-roll was requested.
     * 	</p>
     *
     * 	@return true if a re-roll has been requested
     */
    public boolean wasRerollRequested() {
        return Objects.requireNonNullElse(this.subeventJson.getBoolean("reroll_requested"), false);
    }

    /**
     * 	<p>
     * 	<b><i>getRerollMode</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String getRerollMode()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the requested re-roll mode.
     * 	</p>
     *
     * 	@return the requested re-roll mode (<code>"use_new", "use_highest", "use_lowest"</code>)
     */
    public String getRerollMode() {
        return this.subeventJson.getString("reroll_mode");
    }

}
