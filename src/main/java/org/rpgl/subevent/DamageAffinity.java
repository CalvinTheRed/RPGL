package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This Subevent is dedicated to determining the affinity an RPGLObject has for a particular damage type (<code>"normal",
 * "immunity", "resistance", "vulnerability"</code>).
 * <br>
 * <br>
 * Source: an RPGLObject attempting to deal the indicated damage type
 * <br>
 * Target: an RPGLObject being targeted by typed damage
 *
 * @author Calvin Withun
 */
public class DamageAffinity extends Subevent implements DamageTypeSubevent {

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
        this.json.asMap().putIfAbsent("affinities", new ArrayList<>());
    }

    @Override
    public boolean includesDamageType(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            if (Objects.equals(affinities.getJsonObject(i).getString("damage_type"), damageType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a damage type to the DamageAffinity for evaluation. If the damage type is already present in the
     * DamageAffinity, this method does nothing.
     *
     * @param damageType a damage type
     */
    public void addDamageType(String damageType) {
        this.json.asMap().putIfAbsent("affinities", new ArrayList<>());
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (Objects.equals(affinity.getString("damage_type"), damageType)) {
                // short-circuit if the damage type is already included
                return;
            }
        }

        // add new damage type to affinities array
        this.getAffinities().addJsonObject(new JsonObject() {{
            this.putString("damage_type", damageType);
            this.putBoolean("immunity", false);
            this.putBoolean("resistance", false);
            this.putBoolean("vulnerability", false);
            this.putBoolean("immunity_revoked", false);
            this.putBoolean("resistance_revoked", false);
            this.putBoolean("vulnerability_revoked", false);
        }});
    }

    /**
     * This helper method returns the affinities of all involved damage types in the DamageAffinity.
     *
     * @return a JsonArray indicating the Subevent's current affinities
     */
    JsonArray getAffinities() {
        return this.json.getJsonArray("affinities");
    }

    /**
     * This method informs the Subevent that <code>source</code> is immune to the given damage type. If the damage type
     * is null, the immunity is applied to all included damage types.
     *
     * @param damageType a damage type
     */
    public void grantImmunity(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (damageType == null || Objects.equals(affinity.getString("damage_type"), damageType)) {
                affinity.putBoolean("immunity", true);
            }
        }
    }

    /**
     * This method informs the Subevent that <code>source</code> is resistant to the given damage type. If the damage
     * type is null, the resistance is applied to all included damage types.
     *
     * @param damageType a damage type
     */
    public void grantResistance(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (damageType == null || Objects.equals(affinity.getString("damage_type"), damageType)) {
                affinity.putBoolean("resistance", true);
            }
        }
    }

    /**
     * This method informs the Subevent that <code>source</code> is vulnerable to the given damage type. If the damage
     * type is null, the vulnerability is applied to all included damage types.
     *
     * @param damageType a damage type
     */
    public void grantVulnerability(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (damageType == null || Objects.equals(affinity.getString("damage_type"), damageType)) {
                affinity.putBoolean("vulnerability", true);
            }
        }
    }

    /**
     * This method informs the Subevent that <code>source</code> has had its immunity to the given damage type revoked.
     * If the damage type is null, the revocation is applied to all included damage types.
     *
     * @param damageType a damage type
     */
    public void revokeImmunity(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (damageType == null || Objects.equals(affinity.getString("damage_type"), damageType)) {
                affinity.putBoolean("immunity_revoked", true);
            }
        }
    }

    /**
     * This method informs the Subevent that <code>source</code> has had its resistance to the given damage type
     * revoked. If the damage type is null, the revocation is applied to all included damage types.
     *
     * @param damageType a damage type
     */
    public void revokeResistance(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (damageType == null || Objects.equals(affinity.getString("damage_type"), damageType)) {
                affinity.putBoolean("resistance_revoked", true);
            }
        }
    }

    /**
     * This method informs the Subevent that <code>source</code> has had its vulnerability to the given damage type
     * revoked. If the damage type is null, the revocation is applied to all included damage types.
     *
     * @param damageType a damage type
     */
    public void revokeVulnerability(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (damageType == null || Objects.equals(affinity.getString("damage_type"), damageType)) {
                affinity.putBoolean("vulnerability_revoked", true);
            }
        }
    }

    /**
     * This method returns whether the source is immune to the provided damage type.
     *
     * @param damageType a damage type
     * @return true if the source is immune to the provided damage type.
     */
    public boolean isImmune(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (Objects.equals(affinity.getString("damage_type"), damageType)) {
                return affinity.getBoolean("immunity") && !affinity.getBoolean("immunity_revoked");
            }
        }
        return false;
    }

    /**
     * This method returns whether the source is resistant to the provided damage type.
     *
     * @param damageType a damage type
     * @return true if the source is resistant to the provided damage type.
     */
    public boolean isResistant(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (Objects.equals(affinity.getString("damage_type"), damageType)) {
                return affinity.getBoolean("resistance") && !affinity.getBoolean("resistance_revoked");
            }
        }
        return false;
    }

    /**
     * This method returns whether the source is vulnerable to the provided damage type.
     *
     * @param damageType a damage type
     * @return true if the source is vulnerable to the provided damage type.
     */
    public boolean isVulnerable(String damageType) {
        JsonArray affinities = this.getAffinities();
        for (int i = 0; i < affinities.size(); i++) {
            JsonObject affinity = affinities.getJsonObject(i);
            if (Objects.equals(affinity.getString("damage_type"), damageType)) {
                return affinity.getBoolean("vulnerability") && !affinity.getBoolean("vulnerability_revoked");
            }
        }
        return false;
    }

}
