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
 * Target: an RPGLObject attempting to deal the indicated damage type
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
        clone.joinSubeventData(this.json);
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
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putBoolean("immunity", false);
        this.json.putBoolean("resistance", false);
        this.json.putBoolean("vulnerability", false);
        this.json.putBoolean("immunity_revoked", false);
        this.json.putBoolean("resistance_revoked", false);
        this.json.putBoolean("vulnerability_revoked", false);
    }

    /**
     * This method informs the Subevent that <code>source</code> is immune to the given damage type.
     */
    public void grantImmunity() {
        this.json.putBoolean("immunity", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> is resistant to the given damage type.
     */
    public void grantResistance() {
        this.json.putBoolean("resistance", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> is vulnerable to the given damage type.
     */
    public void grantVulnerability() {
        this.json.putBoolean("vulnerability", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has had its immunity to the given damage type revoked.
     */
    public void revokeImmunity() {
        this.json.putBoolean("immunity_revoked", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has had its resistance to the given damage type revoked.
     */
    public void revokeResistance() {
        this.json.putBoolean("resistance_revoked", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has had its vulnerability to the given damage type revoked.
     */
    public void revokeVulnerability() {
        this.json.putBoolean("vulnerability_revoked", true);
    }

    /**
     * This method returns whether the source is immune to the relevant damage type.
     *
     * @return true if the source is immune to the relevant damage type.
     */
    public boolean isImmune() {
        return Objects.requireNonNullElse(this.json.getBoolean("immunity"), false)
                && !Objects.requireNonNullElse(this.json.getBoolean("immunity_revoked"), false);
    }

    /**
     * This method returns whether the source is resistant to the relevant damage type.
     *
     * @return true if the source is resistant to the relevant damage type.
     */
    public boolean isResistant() {
        return Objects.requireNonNullElse(this.json.getBoolean("resistance"), false)
                && !Objects.requireNonNullElse(this.json.getBoolean("resistance_revoked"), false);
    }

    /**
     * This method returns whether the source is vulnerable to the relevant damage type.
     *
     * @return true if the source is vulnerable to the relevant damage type.
     */
    public boolean isVulnerable() {
        return Objects.requireNonNullElse(this.json.getBoolean("vulnerability"), false)
                && !Objects.requireNonNullElse(this.json.getBoolean("vulnerability_revoked"), false);
    }

}
