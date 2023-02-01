package org.rpgl.subevent;

import org.rpgl.core.JsonObject;

import java.util.List;
import java.util.Map;

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
public class CriticalHitDamageDiceCollection extends DamageDiceCollection {

    public CriticalHitDamageDiceCollection() {
        super("critical_hit_damage_dice_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalHitDamageDiceCollection();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new CriticalHitDamageDiceCollection();
        clone.joinSubeventData(subeventDataMap);
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
        for (Object typedDamageObjectElement : this.getDamageDiceCollection()) {
            JsonObject typedDamageObject = (JsonObject) typedDamageObjectElement;
            List<Object> typedDamageDice = typedDamageObject.getList("dice");
            typedDamageDice.addAll(JsonObject.deepClone(typedDamageDice));
        }
    }

}
