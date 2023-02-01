package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;

import java.util.Map;

/**
 * This Subevent is dedicated to determining the affinity an RPGLObject has for a particular damage type (<code>"normal",
 * "immunity", "resistance", "vulnerability"</code>).
 * <br>
 * <br>
 * Source: an RPGLObject being targeted by typed damage
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class DamageAffinity extends Subevent {

    public DamageAffinity() {
        super("damage_affinity");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageAffinity();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new DamageAffinity();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) {
        this.subeventJson.put("immunity", false);
        this.subeventJson.put("resistance", false);
        this.subeventJson.put("vulnerability", false);
        this.subeventJson.put("immunity_revoked", false);
        this.subeventJson.put("resistance_revoked", false);
        this.subeventJson.put("vulnerability_revoked", false);
    }

    /**
     * 	<p>
     * 	<b><i>grantImmunity</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void grantImmunity()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> is immune to the given damage type.
     * 	</p>
     */
    public void grantImmunity() {
        this.subeventJson.put("immunity", true);
    }

    /**
     * 	<p>
     * 	<b><i>grantResistance</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void grantResistance()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> is resistant to the given damage type.
     * 	</p>
     */
    public void grantResistance() {
        this.subeventJson.put("resistance", true);
    }

    /**
     * 	<p>
     * 	<b><i>grantVulnerability</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void grantVulnerability()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> is vulnerable to the given damage type.
     * 	</p>
     */
    public void grantVulnerability() {
        this.subeventJson.put("vulnerability", true);
    }

    /**
     * 	<p>
     * 	<b><i>revokeImmunity</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void revokeImmunity()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> has had its immunity to the given damage type revoked.
     * 	</p>
     */
    public void revokeImmunity() {
        this.subeventJson.put("immunity_revoked", true);
    }

    /**
     * 	<p>
     * 	<b><i>revokeResistance</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void revokeResistance()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> has had its resistance to the given damage type revoked.
     * 	</p>
     */
    public void revokeResistance() {
        this.subeventJson.put("resistance_revoked", true);
    }

    /**
     * 	<p>
     * 	<b><i>revokeVulnerability</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void revokeVulnerability()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method informs the Subevent that <code>source</code> has had its vulnerability to the given damage type revoked.
     * 	</p>
     */
    public void revokeVulnerability() {
        this.subeventJson.put("vulnerability_revoked", true);
    }

    /**
     * 	<p>
     * 	<b><i>getAffinity</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String getAffinity()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the affinity <code>source</code> has for the given damage type.
     * 	</p>
     *
     *  @return the affinity <code>source</code> has for the given damage type (<code>"normal", "immunity", "resistance",
     *  "vulnerability"</code>.).
     */
    public String getAffinity() {
        boolean immunity = (Boolean) this.subeventJson.get("immunity")
                && !(Boolean) this.subeventJson.get("immunity_revoked");
        boolean resistance = (Boolean) this.subeventJson.get("resistance")
                && !(Boolean) this.subeventJson.get("resistance_revoked");
        boolean vulnerability = (Boolean) this.subeventJson.get("vulnerability")
                && !(Boolean) this.subeventJson.get("vulnerability_revoked");

        if (immunity) {
            return "immunity";
        } else if (resistance && !vulnerability) {
            return "resistance";
        } else if (vulnerability && !resistance) {
            return "vulnerability";
        } else {
            return "normal";
        }
    }

}
