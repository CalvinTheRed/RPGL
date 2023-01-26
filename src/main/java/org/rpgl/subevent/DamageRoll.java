package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.math.Die;

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
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");

        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
            if (typedDamageDieArray == null) {
                typedDamageDieArray = new JsonArray();
            }

            for (Object typedDamageDieElement : typedDamageDieArray) {
                JsonObject typedDamageDieJson = (JsonObject) typedDamageDieElement;
                long size = (Long) typedDamageDieJson.get("size");
                long roll = Die.roll(size, (Long) typedDamageDieJson.get("determined"));
                typedDamageDieJson.put("roll", roll);
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>rerollTypedDiceLessThanOrEqualTo</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void rerollTypedDiceLessThanOrEqualTo(long threshold, String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method re-rolls any dice of a given damage type whose rolled values are less than or equal to a given threshold.
     * 	</p>
     */
    public void rerollTypedDiceLessThanOrEqualTo(long threshold, String damageType) {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");

        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            if (damageType == null || damageType.equals(typedDamage.get("type"))) {
                JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
                if (typedDamageDieArray == null) {
                    typedDamageDieArray = new JsonArray();
                }

                for (Object typedDamageDieElement : typedDamageDieArray) {
                    JsonObject typedDamageDieJson = (JsonObject) typedDamageDieElement;
                    if ((Long) typedDamageDieJson.get("roll") <= threshold) {
                        long size = (Long) typedDamageDieJson.get("size");
                        long roll = Die.roll(size, (Long) typedDamageDieJson.get("determined_reroll"));
                        typedDamageDieJson.put("roll", roll);
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
     * public void setTypedDiceLessThanOrEqualTo(long threshold, long faceValue, String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method overrides the face value of all dice of a given damage type whose rolled values are less than or
     * 	equal to a given threshold.
     * 	</p>
     */
    public void setTypedDiceLessThanOrEqualTo(long threshold, long faceValue, String damageType) {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            if (damageType == null || damageType.equals(typedDamage.get("type"))) {
                JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
                if (typedDamageDieArray == null) {
                    typedDamageDieArray = new JsonArray();
                }

                for (Object typedDamageDieElement : typedDamageDieArray) {
                    JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                    if ((Long) typedDamageDie.get("roll") <= threshold) {
                        typedDamageDie.put("roll", faceValue);
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
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
            if (typedDamageDieArray == null) {
                typedDamageDieArray = new JsonArray();
            }
            Long typedDamageBonus = (Long) typedDamage.get("bonus");
            if (typedDamageBonus == null) {
                typedDamageBonus = 0L;
            }

            long sum = typedDamageBonus;
            for (Object typedDamageDieElement : typedDamageDieArray) {
                JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                sum += (Long) typedDamageDie.get("roll");
            }
            baseDamage.put((String) typedDamage.get("type"), sum);
        }
        return baseDamage;
    }

}
