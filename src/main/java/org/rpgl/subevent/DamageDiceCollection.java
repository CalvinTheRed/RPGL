package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;

import java.util.Objects;

/**
 * This abstract Subevent is dedicated to collecting unrolled damage dice and bonuses.
 * <br>
 * <br>
 * Source: an RPGLObject preparing to deal damage
 * <br>
 * Target: an RPGLObject which will later suffer the collected damage
 *
 * @author Calvin Withun
 */
public abstract class DamageDiceCollection extends Subevent {

    public DamageDiceCollection(String subeventId) {
        super(subeventId);
    }

    /**
     * 	<p>
     * 	<b><i>includesDamageType</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean includesDamageType(String damageType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether a given damage type is present in the damage dice collection.
     * 	</p>
     *
     *  @param damageType the damage type being searched for
     *  @return true if the passed damage type is present in the damage dice collection
     */
    public boolean includesDamageType(String damageType) {
        JsonArray damageDiceArray = (JsonArray) this.subeventJson.get("damage");
        if (damageDiceArray != null) {
            for (Object damageDiceElement : damageDiceArray) {
                JsonObject damageDice = (JsonObject) damageDiceElement;
                if (damageDice.get("type").equals(damageType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 	<p>
     * 	<b><i>addTypedDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addTypedDamage(JsonArray typedDamageArray)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds typed damage to the damage dice collection.
     * 	</p>
     *
     *  @param typedDamageArray the typed damage to be included in the damage dice collection
     */
    public void addTypedDamage(JsonArray typedDamageArray) {
        try {
            for (Object typedDamageElement : typedDamageArray) {
                JsonObject typedDamage = (JsonObject) typedDamageElement;
                if (this.includesDamageType((String) typedDamage.get("type"))) {
                    this.addExistingTypedDamage(typedDamage);
                } else {
                    this.addNewTypedDamage(typedDamage);
                }
            }
        } catch (JsonFormatException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    /**
     * 	<p>
     * 	<b><i>addExistingTypedDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void addExistingTypedDamage(JsonObject typedDamage)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method adds typed damage whose type is already present in the damage dice collection.
     * 	</p>
     *
     *  @param typedDamage the typed damage to be added to the damage dice collection
     *
     *  @throws JsonFormatException if the damage type thought to be present is absent
     */
    void addExistingTypedDamage(JsonObject typedDamage) throws JsonFormatException {
        String damageType = (String) typedDamage.get("type");
        JsonObject existingTypedDamage = (JsonObject) this.subeventJson.seek(String.format("""
                        damage[{"type":"%s"}]""",
                damageType
        ));

        /*
         * Add new damage dice, if any exist
         */
        JsonArray existingTypedDamageDice = (JsonArray) existingTypedDamage.get("dice");
        JsonArray typedDamageDice = (JsonArray) typedDamage.get("dice");
        if (existingTypedDamageDice == null) {
            existingTypedDamageDice = new JsonArray();
            existingTypedDamage.put("dice", existingTypedDamageDice);
        }
        existingTypedDamageDice.addAll(Objects.requireNonNullElse(typedDamageDice, new JsonArray()));

        /*
         * Add extra damage bonus, if it exists
         */
        Long existingTypedDamageBonus = (Long) existingTypedDamage.get("bonus");
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        existingTypedDamage.put(
                "bonus",
                Objects.requireNonNullElse(existingTypedDamageBonus, 0L) + Objects.requireNonNullElse(typedDamageBonus, 0L)
        );
    }

    /**
     * 	<p>
     * 	<b><i>addNewTypedDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void addNewTypedDamage(JsonObject typedDamage)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method adds typed damage whose type is already present in the damage dice collection.
     * 	</p>
     *
     *  @param typedDamage the typed damage to be added to the damage dice collection
     */
    void addNewTypedDamage(JsonObject typedDamage) {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        typedDamageArray.add(typedDamage);
    }

    /**
     * 	<p>
     * 	<b><i>getDamageDiceCollection</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonArray getDamageDiceCollection()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the damage dice collection being gathered by this Subevent.
     * 	</p>
     *
     *  @return an array of typed damage dice and bonuses
     */
    public JsonArray getDamageDiceCollection() {
        return (JsonArray) this.subeventJson.get("damage");
    }

}
