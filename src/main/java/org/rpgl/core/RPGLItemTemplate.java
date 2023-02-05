package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class RPGLItemTemplate extends JsonObject {

    /**
     * 	<p><b><i>newInstance</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLItem newInstance()
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructs a new RPGLItem object corresponding to the contents of the RPGLItemTemplate object. The new object is
     * 	registered to the UUIDTable class when it is constructed.
     * 	</p>
     *
     * 	@return a new RPGLItem object
     */
    public RPGLItem newInstance() {
        RPGLItem item = new RPGLItem();
        item.join(this);
        item.asMap().putIfAbsent("weight",            0);
        item.asMap().putIfAbsent("attack_bonus",      0);
        item.asMap().putIfAbsent("cost",              "0g");
        item.asMap().putIfAbsent("weapon_properties", new ArrayList<>(Collections.singleton("improvised")));
        item.asMap().putIfAbsent("proficiency_tags",  new ArrayList<>(Collections.singleton("improvised")));
        item.asMap().putIfAbsent("tags",              new ArrayList<>(Collections.singleton("improvised")));
        item.asMap().putIfAbsent("range", new HashMap<String, Object>() {{
            this.put("normal", 20);
            this.put("long", 60);
        }});
        item.defaultAttackAbilities();
        processEquippedEffects(item);
        processItemDamage(item);
        UUIDTable.register(item);
        return item;
    }

    /**
     * 	<p><b><i>processWhileEquipped</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processWhileEquipped(RPGLItem item)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method converts effectId's in an RPGLItemTemplate's while_equipped array to RPGLEffects. The UUID's
     * 	of these new RPGLEffects replace the original array contents.
     * 	</p>
     *
     *  @param item an RPGLItem
     */
    static void processEquippedEffects(RPGLItem item) {
        JsonArray equippedEffectsIdArray = item.removeJsonArray("equipped_effects");
        if (equippedEffectsIdArray == null) {
            item.putJsonArray("equipped_effects", new JsonArray());
        } else {
            JsonArray equippedEffectsUuidArray = new JsonArray();
            for (int i = 0; i < equippedEffectsIdArray.size(); i++) {
                String effectId = equippedEffectsIdArray.getString(i);
                RPGLEffect effect = RPGLFactory.newEffect(effectId);
                assert effect != null; // TODO better check than this?
                equippedEffectsUuidArray.addString(effect.getUuid());
            }
            item.putJsonArray("equipped_effects", equippedEffectsUuidArray);
        }
    }

    /**
     * 	<p><b><i>processItemDamage</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processItemDamage(RPGLItem item)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method ensures that each RPGLItem object has values assigned for melee and thrown damage. If no
     *  values are assigned, the default improvised weapon damage die (1d4 bludgeoning) will be assigned.
     * 	</p>
     *
     *  @param item an RPGLItem
     */
    static void processItemDamage(RPGLItem item) {
        JsonObject damage = new JsonObject();
        damage.join(Objects.requireNonNullElse(item.getJsonObject("damage"), new JsonObject()));

        if (damage.asMap().isEmpty()) {
            setImprovisedItemDamage(damage, "melee");
            setImprovisedItemDamage(damage, "thrown");
        } else {
            JsonArray melee = damage.getJsonArray("melee");
            JsonArray thrown = damage.getJsonArray("thrown");
            if (melee == null || melee.asList().isEmpty()) {
                setImprovisedItemDamage(damage, "melee");
            }
            if (thrown == null || thrown.asList().isEmpty()) {
                setImprovisedItemDamage(damage, "thrown");
            }
        }
    }

    /**
     * 	<p><b><i>setImprovisedItemDamage</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void setImprovisedItemDamage(RPGLItem item, String attackType)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method assigns the improvised weapon damage die 1d4 to the passed attack type for the RPGLItem.
     * 	</p>
     *
     *  @param damage     a JsonObject representing an item's damage
     *  @param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     */
    static void setImprovisedItemDamage(JsonObject damage, String attackType) {
        damage.putJsonArray(attackType, new JsonArray(new ArrayList<>() {{
            this.add(new HashMap<String, Object>() {{
                this.put("type", "bludgeoning");
                this.put("dice", new ArrayList<>() {{
                    this.add(new HashMap<String, Object>() {{
                        this.put("size", 4);
                        this.put("determined", 1);
                    }});
                }});
                this.put("bonus", 0);
            }});
        }}));
    }

}
