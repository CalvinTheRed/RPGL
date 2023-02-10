package org.rpgl.subevent;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
public abstract class DamageCollection extends Subevent {

    public DamageCollection(String subeventId) {
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
        JsonArray damageDiceArray = this.subeventJson.getJsonArray("damage");
        if (damageDiceArray != null) {
            for (Object damageDiceElement : damageDiceArray.asList()) {
                JsonObject damageDice = (JsonObject) damageDiceElement;
                if (damageDice.getString("type").equals(damageType)) {
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
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (this.includesDamageType(typedDamage.getString("type"))) {
                this.addExistingTypedDamage(typedDamage);
            } else {
                this.addNewTypedDamage(typedDamage);
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>addExistingTypedDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void addExistingTypedDamage(JsonObject typedDamageToBeAdded)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method adds typed damage whose type is already present in the damage dice collection.
     * 	</p>
     *
     *  @param typedDamageToBeAdded the typed damage to be added to the damage dice collection
     */
    void addExistingTypedDamage(JsonObject typedDamageToBeAdded) {
        String damageTypeToBeAdded = typedDamageToBeAdded.getString("type");
        JsonObject typedDamage = this.subeventJson.getJsonArray("damage").getJsonObjectMatching("type", damageTypeToBeAdded);

        /*
         * Add new damage dice, if any exist
         */
        JsonArray typedDamageDice = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
        JsonArray typedDamageDiceToBeAdded = Objects.requireNonNullElse(typedDamageToBeAdded.getJsonArray("dice"), new JsonArray());
        typedDamageDice.asList().addAll(typedDamageDiceToBeAdded.asList());

        /*
         * Add extra damage bonus, if it exists
         */
        Integer typedDamageBonus = Objects.requireNonNullElse(typedDamage.getInteger("bonus"), 0);
        Integer typedDamageBonusToBeAdded = Objects.requireNonNullElse(typedDamageToBeAdded.getInteger("bonus"), 0);
        typedDamage.putInteger(
                "bonus",
                typedDamageBonus + typedDamageBonusToBeAdded
        );
    }

    /**
     * 	<p>
     * 	<b><i>addNewTypedDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void addNewTypedDamage(Map&lt;Object&gt; typedDamage)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method adds typed damage whose type is already present in the damage dice collection.
     * 	</p>
     *
     *  @param typedDamage the typed damage to be added to the damage dice collection
     */
    void addNewTypedDamage(JsonObject typedDamage) {
        JsonArray typedDamageArray = this.subeventJson.getJsonArray("damage");
        typedDamageArray.addJsonObject(typedDamage);
    }

    /**
     * 	<p>
     * 	<b><i>getDamageCollection</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonArray getDamageCollection()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the damage dice collection being gathered by this Subevent.
     * 	</p>
     *
     *  @return an array of typed damage dice and bonuses
     */
    public JsonArray getDamageCollection() {
        return this.subeventJson.getJsonArray("damage");
    }

}
