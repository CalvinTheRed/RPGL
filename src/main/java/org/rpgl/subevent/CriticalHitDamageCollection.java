package org.rpgl.subevent;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to representing a collection of damage dice to be rolled when a critical hit occurs.
 * <br>
 * <br>
 * Source: an RPGLObject delivering a critical hit attack
 * <br>
 * Target: an RPGLObject targeted by a critical hit attack
 *
 * @author Calvin Withun
 */
public class CriticalHitDamageCollection extends DamageCollection {

    public CriticalHitDamageCollection() {
        super("critical_hit_damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalHitDamageCollection();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CriticalHitDamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    /**
     * 	<p>
     * 	<b><i>doubleDice</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void doubleDice()
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method doubles the number of dice in the damage dice collection.
     * 	</p>
     */
    void doubleDice() {
        JsonArray damageCollection = this.getDamageCollection();
        for (int i = 0; i < damageCollection.size(); i++) {
            JsonArray typedDamageDice = damageCollection.getJsonObject(i).getJsonArray("dice");
            typedDamageDice.asList().addAll(typedDamageDice.deepClone().asList());
        }
    }

}
