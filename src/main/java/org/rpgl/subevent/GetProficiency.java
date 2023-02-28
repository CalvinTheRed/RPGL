package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;

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

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putBoolean("is_half_proficient", false);
        this.json.putBoolean("is_proficient", false);
        this.json.putBoolean("has_expertise", false);
        this.json.putBoolean("half_proficient_revoked", false);
        this.json.putBoolean("proficient_revoked", false);
        this.json.putBoolean("expert_revoked", false);
    }

    /**
     * This method informs the Subevent that <code>source</code> has half proficiency with whatever it is attempting to
     * use.
     */
    public void grantHalfProficiency() {
        this.json.putBoolean("is_half_proficient", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has proficiency with whatever it is attempting to use.
     * This also removes any half-proficiency previously granted.
     */
    public void grantProficiency() {
        this.revokeHalfProficiency();
        this.json.putBoolean("is_proficient", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has expertise with whatever it is attempting to use.
     * This also grants proficiency per <code>grantProficiency()</code>.
     */
    public void grantExpertise() {
        this.revokeProficiency();
        this.json.putBoolean("has_expertise", true);
    }

    /**
     * This helper method revokes half proficiency, if it would normally be granted through this Subevent.
     */
    void revokeHalfProficiency() {
        this.json.putBoolean("half_proficient_revoked", true);
    }

    /**
     * This helper method revokes proficiency and half proficiency, if either would normally be granted through this Subevent.
     */
    void revokeProficiency() {
        this.revokeHalfProficiency();
        this.json.putBoolean("proficient_revoked", true);
    }

    /**
     * This helper method revokes expertise, proficiency, and half proficiency, if any would normally be granted through
     * this Subevent.
     */
    void revokeExpertise() {
        this.revokeProficiency();
        this.json.putBoolean("expert_revoked", true);
    }

    /**
     * This method returns whether <code>source</code> has been found to be half proficient with whatever it is
     * attempting to use.
     *
     * @return true if <code>source</code> is half proficient with whatever it is attempting to use
     */
    public boolean isHalfProficient() {
        return this.json.getBoolean("is_half_proficient") && !this.json.getBoolean("half_proficient_revoked");
    }

    /**
     * This method returns whether <code>source</code> has been found to be proficient with whatever it is attempting
     * to use.
     *
     * @return true if <code>source</code> is proficient with whatever it is attempting to use
     */
    public boolean isProficient() {
        return this.json.getBoolean("is_proficient") && !this.json.getBoolean("proficient_revoked");
    }

    /**
     * This method returns whether <code>source</code> has been found to have expertise with whatever it is attempting
     * to use.
     *
     * @return true if <code>source</code> has expertise with whatever it is attempting to use
     */
    public boolean isExpert() {
        return this.json.getBoolean("has_expertise") && !this.json.getBoolean("expert_revoked");
    }

}
