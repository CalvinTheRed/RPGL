package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.Objects;

/**
 * This abstract Subevent is dedicated to rolling damage dice.
 * <br>
 * <br>
 * Source: an RPGLObject rolling damage
 * <br>
 * Target: an RPGLObject which will later suffer the rolled damage
 *
 * @author Calvin Withun
 */
public abstract class DamageRoll extends Subevent {

    public DamageRoll(String subeventId) {
        super(subeventId);
    }

    @Override
    public void prepare(RPGLContext context) {
        this.roll();
    }

    /**
     * 	<p>
     * 	<b><i>roll</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void roll()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method rolls all dice associated with the Subevent.
     * 	</p>
     */
    public void roll() {
        JsonArray typedDamageArray = this.subeventJson.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamageArray.getJsonObject(i).getJsonArray("dice"), new JsonArray());
            for (int j = 0; j < typedDamageDieArray.size(); j++) {
                JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                int size = typedDamageDie.getInteger("size");
                int roll = Die.roll(size, typedDamageDie.getInteger("determined"));
                typedDamageDie.putInteger("roll", roll);
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>rerollTypedDiceLessThanOrEqualTo</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void rerollTypedDiceLessThanOrEqualTo(int threshold, String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method re-rolls any dice of a given damage type whose rolled values are less than or equal to a given threshold.
     * 	</p>
     */
    public void rerollTypedDiceLessThanOrEqualTo(int threshold, String damageType) {
        JsonArray typedDamageArray = this.subeventJson.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || damageType.equals(typedDamage.getString("type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    if (typedDamageDie.getInteger("roll") <= threshold) {
                        int size = typedDamageDie.getInteger("size");
                        int roll = Die.roll(size, typedDamageDie.getInteger("determined_reroll"));
                        typedDamageDie.putInteger("roll", roll);
                    }
                }
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>setTypedDiceLessThanOrEqualTo</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setTypedDiceLessThanOrEqualTo(int threshold, int faceValue, String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method overrides the face value of all dice of a given damage type whose rolled values are less than or
     * 	equal to a given threshold.
     * 	</p>
     */
    public void setTypedDiceLessThanOrEqualTo(int threshold, int faceValue, String damageType) {
        JsonArray typedDamageArray = this.subeventJson.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || damageType.equals(typedDamage.getString("type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    if (typedDamageDie.getInteger("roll") <= threshold) {
                        typedDamageDie.putInteger("roll", faceValue);
                    }
                }
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>getDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonObject getDamage()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the damage dice collection associated with the Subevent.
     * 	</p>
     *
     *  @return a collection of damage dice and bonuses
     */
    public JsonObject getDamage() {
        JsonObject baseDamage = new JsonObject();
        JsonArray typedDamageArray = this.subeventJson.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
            int bonus = Objects.requireNonNullElse(typedDamage.getInteger("bonus"), 0);
            for (int j = 0; j < typedDamageDieArray.size(); j++) {
                JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                bonus += typedDamageDie.getInteger("roll");
            }
            baseDamage.putInteger(typedDamage.getString("type"), bonus);
        }
        return baseDamage;
    }

}
