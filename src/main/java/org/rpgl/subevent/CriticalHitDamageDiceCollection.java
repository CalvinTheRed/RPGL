package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;

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
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CriticalHitDamageDiceCollection();
        clone.joinSubeventJson(subeventJson);
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
     *
     * 	@throws JsonFormatException if there is a JSON formatting error.
     */
    void doubleDice() throws JsonFormatException {
        for (Object typedDamageObjectElement : this.getDamageDiceCollection()) {
            JsonObject typedDamageObject = (JsonObject) typedDamageObjectElement;
            JsonArray typedDamageDice = (JsonArray) typedDamageObject.get("dice");
            typedDamageDice.addAll(JsonParser.parseArrayString(typedDamageDice.toString()));
        }
    }

}
