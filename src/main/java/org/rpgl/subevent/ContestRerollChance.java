package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;

import java.util.Map;
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
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new ContestRerollChance();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) {
        this.subeventJson.put("reroll_requested", false);
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
        if (!(Boolean) this.subeventJson.get("reroll_requested")) {
            this.subeventJson.put("reroll_requested", true);
            this.subeventJson.put("reroll_mode", rerollMode);
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
        return Objects.requireNonNullElse((Boolean) this.subeventJson.get("reroll_requested"), false);
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
        return (String) this.subeventJson.get("reroll_mode");
    }

}
