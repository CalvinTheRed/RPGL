package org.rpgl.subevent;

import org.rpgl.core.JsonObject;

import java.util.*;

/**
 * This abstract Subevent is dedicated to collecting unrolled damage dice and bonuses.
 * <br>
 * <br>
 * Source: an RPGLObject preparing to deal damage
 * <br>
 * Target: an RPGLObject which will later suffer the collected damage
 * //TODO change damage dice collections to just damage collections... makes more sense since it includes bonuses as well
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
        List<Object> damageDiceArray = this.subeventJson.getList("damage");
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
    public void addTypedDamage(List<Object> typedDamageArray) {
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
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
     * void addExistingTypedDamage(JsonObject typedDamage)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method adds typed damage whose type is already present in the damage dice collection.
     * 	</p>
     *
     *  @param typedDamage the typed damage to be added to the damage dice collection
     */
    void addExistingTypedDamage(JsonObject typedDamage) {
        String damageType = (String) typedDamage.get("type");
        JsonObject existingTypedDamage = JsonObject.mapToJsonObject(this.subeventJson.seekMap(String.format("""
                        damage[{"type":"%s"}]""",
                damageType
        )));

        /*
         * Add new damage dice, if any exist
         */
        List<Object> existingTypedDamageDice = existingTypedDamage.getList("dice");
        List<Object> typedDamageDice = typedDamage.getList("dice");
        if (existingTypedDamageDice == null) {
            existingTypedDamageDice = new ArrayList<>();
            existingTypedDamage.put("dice", existingTypedDamageDice);
        }
        existingTypedDamageDice.addAll(Objects.requireNonNullElse(typedDamageDice, new ArrayList<>()));

        /*
         * Add extra damage bonus, if it exists
         */
        Integer existingTypedDamageBonus = (Integer) existingTypedDamage.get("bonus");
        Integer typedDamageBonus = (Integer) typedDamage.get("bonus");
        existingTypedDamage.put(
                "bonus",
                Objects.requireNonNullElse(existingTypedDamageBonus, 0) + Objects.requireNonNullElse(typedDamageBonus, 0)
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
    void addNewTypedDamage(Map<String, Object> typedDamage) {
        List<Object> typedDamageArray = this.subeventJson.getList("damage");
        typedDamageArray.add(typedDamage);
    }

    /**
     * 	<p>
     * 	<b><i>getDamageDiceCollection</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public List&lt;Object&gt; getDamageDiceCollection()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the damage dice collection being gathered by this Subevent.
     * 	</p>
     *
     *  @return an array of typed damage dice and bonuses
     */
    public List<Object> getDamageDiceCollection() {
        return this.subeventJson.getList("damage");
    }

}
