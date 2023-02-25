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
        this.subeventJson.putBoolean("half_proficient", false);
        this.subeventJson.putBoolean("proficient", false);
        this.subeventJson.putBoolean("expert", false);
        this.subeventJson.putBoolean("half_proficient_revoked", false);
        this.subeventJson.putBoolean("proficient_revoked", false);
        this.subeventJson.putBoolean("expert_revoked", false);
    }

    /**
     * This method informs the Subevent that <code>source</code> has half proficiency with whatever it is attempting to
     * use.
     */
    public void grantHalfProficiency() {
        this.subeventJson.putBoolean("is_half_proficient", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has proficiency with whatever it is attempting to use.
     * This also removes any half-proficiency previously granted.
     */
    public void grantProficiency() {
        this.revokeHalfProficiency();
        this.subeventJson.putBoolean("is_proficient", true);
    }

    /**
     * This method informs the Subevent that <code>source</code> has expertise with whatever it is attempting to use.
     * This also grants proficiency per <code>grantProficiency()</code>.
     */
    public void grantExpertise() {
        this.revokeProficiency();
        this.subeventJson.putBoolean("has_expertise", true);
    }

    void revokeHalfProficiency() {
        this.subeventJson.putBoolean("half_proficient_revoked", true);
    }

    void revokeProficiency() {
        this.revokeHalfProficiency();
        this.subeventJson.putBoolean("proficient_revoked", true);
    }

    void revokeExpertise() {
        this.subeventJson.putBoolean("expert_revoked", true);
    }

    /**
     * This method returns whether <code>source</code> has been found to be half proficient with whatever it is
     * attempting to use.
     *
     * @return true if <code>source</code> is half proficient with whatever it is attempting to use
     */
    public boolean isHalfProficient() {
        return this.subeventJson.getBoolean("half_proficient") && !this.subeventJson.getBoolean("half_proficient_revoked");
    }

    /**
     * This method returns whether <code>source</code> has been found to be proficient with whatever it is attempting
     * to use.
     *
     * @return true if <code>source</code> is proficient with whatever it is attempting to use
     */
    public boolean isProficient() {
        return this.subeventJson.getBoolean("proficient") && !this.subeventJson.getBoolean("proficient_revoked");
    }

    /**
     * This method returns whether <code>source</code> has been found to have expertise with whatever it is attempting
     * to use.
     *
     * @return true if <code>source</code> has expertise with whatever it is attempting to use
     */
    public boolean isExpert() {
        return this.subeventJson.getBoolean("expert") && !this.subeventJson.getBoolean("expert_revoked");
    }
}
