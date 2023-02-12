package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonObject;

import java.util.Objects;

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
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageAffinity();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) {
        this.subeventJson.putBoolean("immunity", false);
        this.subeventJson.putBoolean("resistance", false);
        this.subeventJson.putBoolean("vulnerability", false);
        this.subeventJson.putBoolean("immunity_revoked", false);
        this.subeventJson.putBoolean("resistance_revoked", false);
        this.subeventJson.putBoolean("vulnerability_revoked", false);
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
        this.subeventJson.putBoolean("immunity", true);
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
        this.subeventJson.putBoolean("resistance", true);
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
        this.subeventJson.putBoolean("vulnerability", true);
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
        this.subeventJson.putBoolean("immunity_revoked", true);
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
        this.subeventJson.putBoolean("resistance_revoked", true);
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
        this.subeventJson.putBoolean("vulnerability_revoked", true);
    }

    /**
     * 	<p>
     * 	<b><i>isImmune</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isImmune()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether the source is immune to the relevant damage type.
     * 	</p>
     *
     *  @return true if the source is immune to the relevant damage type.
     */
    public boolean isImmune() {
        return Objects.requireNonNullElse(this.subeventJson.getBoolean("immunity"), false)
                && !Objects.requireNonNullElse(this.subeventJson.getBoolean("immunity_revoked"), false);
    }

    /**
     * 	<p>
     * 	<b><i>isResistant</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isResistant()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether the source is resistant to the relevant damage type.
     * 	</p>
     *
     *  @return true if the source is resistant to the relevant damage type.
     */
    public boolean isResistant() {
        return Objects.requireNonNullElse(this.subeventJson.getBoolean("resistance"), false)
                && !Objects.requireNonNullElse(this.subeventJson.getBoolean("resistance_revoked"), false);
    }

    /**
     * 	<p>
     * 	<b><i>isVulnerable</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isVulnerable()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether the source is vulnerable to the relevant damage type.
     * 	</p>
     *
     *  @return true if the source is vulnerable to the relevant damage type.
     */
    public boolean isVulnerable() {
        return Objects.requireNonNullElse(this.subeventJson.getBoolean("vulnerability"), false)
                && !Objects.requireNonNullElse(this.subeventJson.getBoolean("vulnerability_revoked"), false);
    }

}
