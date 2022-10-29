package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

public class DamageAffinity extends Subevent {

    public DamageAffinity() {
        super("damage_affinity");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageAffinity();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new DamageAffinity();
        clone.joinSubeventJson(subeventJson);
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

    public void giveImmunity() {
        this.subeventJson.put("immunity", true);
    }

    public void giveResistance() {
        this.subeventJson.put("resistance", true);
    }

    public void giveVulnerability() {
        this.subeventJson.put("vulnerability", true);
    }

    public void revokeImmunity() {
        this.subeventJson.put("immunity_revoked", true);
    }

    public void revokeResistance() {
        this.subeventJson.put("resistance_revoked", true);
    }

    public void revokeVulnerability() {
        this.subeventJson.put("vulnerability_revoked", true);
    }

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
