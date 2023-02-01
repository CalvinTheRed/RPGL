package org.rpgl.subevent;

import org.rpgl.core.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.math.Die;

import java.util.ArrayList;
import java.util.List;

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
        List<Object> typedDamageArray = this.subeventJson.getList("damage");

        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            List<Object> typedDamageDieArray = typedDamage.getList("dice");
            if (typedDamageDieArray == null) {
                typedDamageDieArray = new ArrayList<>();
            }

            for (Object typedDamageDieElement : typedDamageDieArray) {
                JsonObject typedDamageDieJson = (JsonObject) typedDamageDieElement;
                int size = (Integer) typedDamageDieJson.get("size");
                int roll = Die.roll(size, (Integer) typedDamageDieJson.get("determined"));
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
     * public void rerollTypedDiceLessThanOrEqualTo(int threshold, String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method re-rolls any dice of a given damage type whose rolled values are less than or equal to a given threshold.
     * 	</p>
     */
    public void rerollTypedDiceLessThanOrEqualTo(int threshold, String damageType) {
        List<Object> typedDamageArray = this.subeventJson.getList("damage");

        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            if (damageType == null || damageType.equals(typedDamage.get("type"))) {
                List<Object> typedDamageDieArray = typedDamage.getList("dice");
                if (typedDamageDieArray == null) {
                    typedDamageDieArray = new ArrayList<>();
                }

                for (Object typedDamageDieElement : typedDamageDieArray) {
                    JsonObject typedDamageDieJson = (JsonObject) typedDamageDieElement;
                    if ((Integer) typedDamageDieJson.get("roll") <= threshold) {
                        int size = (Integer) typedDamageDieJson.get("size");
                        int roll = Die.roll(size, (Integer) typedDamageDieJson.get("determined_reroll"));
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
     * public void setTypedDiceLessThanOrEqualTo(int threshold, int faceValue, String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method overrides the face value of all dice of a given damage type whose rolled values are less than or
     * 	equal to a given threshold.
     * 	</p>
     */
    public void setTypedDiceLessThanOrEqualTo(int threshold, int faceValue, String damageType) {
        List<Object> typedDamageArray = this.subeventJson.getList("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            if (damageType == null || damageType.equals(typedDamage.get("type"))) {
                List<Object> typedDamageDieArray = typedDamage.getList("dice");
                if (typedDamageDieArray == null) {
                    typedDamageDieArray = new ArrayList<>();
                }

                for (Object typedDamageDieElement : typedDamageDieArray) {
                    JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                    if ((Integer) typedDamageDie.get("roll") <= threshold) {
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
        List<Object> typedDamageArray = this.subeventJson.getList("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            List<Object> typedDamageDieArray = typedDamage.getList("dice");
            if (typedDamageDieArray == null) {
                typedDamageDieArray = new ArrayList<>();
            }
            Integer typedDamageBonus = (Integer) typedDamage.get("bonus");
            if (typedDamageBonus == null) {
                typedDamageBonus = 0;
            }

            int sum = typedDamageBonus;
            for (Object typedDamageDieElement : typedDamageDieArray) {
                JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                sum += (Integer) typedDamageDie.get("roll");
            }
            baseDamage.put((String) typedDamage.get("type"), sum);
        }
        return baseDamage;
    }

}
