package org.rpgl.subevent;

import java.util.Objects;

/**
 * This abstract Subevent is dedicated to determining whether an RPGLObject is proficient with something.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to determine if it is proficient with something
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public abstract class GetProficiency extends Subevent {

    public GetProficiency(String subeventId) {
        super(subeventId);
    }

    /**
     * 	<p>
     * 	<b><i>prepareAttackWithoutWeapon</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void grantProficiency()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> has proficiency with whatever it is attempting to use.
     * 	</p>
     */
    public void grantProficiency() {
        this.subeventJson.put("is_proficient", true);
    }

    /**
     * 	<p>
     * 	<b><i>getIsProficient</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean getIsProficient()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether <code>source</code> has been found to be proficient with whatever it is attempting
     * 	to use.
     * 	</p>
     *
     *  @return true if <code>source</code> is proficient with whatever it is attempting to use
     */
    public boolean getIsProficient() {
        return Objects.requireNonNullElse((Boolean) this.subeventJson.get("is_proficient"), false);
    }
}
