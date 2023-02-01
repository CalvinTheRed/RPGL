package org.rpgl.core;

import org.rpgl.uuidtable.UUIDTable;

import java.util.*;

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
        item.putIfAbsent("weight",            0);
        item.putIfAbsent("attack_bonus",      0);
        item.putIfAbsent("cost",              "0g");
        item.putIfAbsent("weapon_properties", new ArrayList<>(Collections.singleton("improvised")));
        item.putIfAbsent("proficiency_tags",  new ArrayList<>(Collections.singleton("improvised")));
        item.putIfAbsent("tags",              new ArrayList<>(Collections.singleton("improvised")));
        item.putIfAbsent("thrown_range", new HashMap<String, Object>() {{
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
        List<Object> equippedEffectsIdArray = (List<Object>) item.remove("equipped_effects");
        if (equippedEffectsIdArray == null) {
            item.put("equipped_effects", new ArrayList<>());
        } else {
            List<Object> equippedEffectsUuidArray = new ArrayList<>();
            for (Object equippedEffectIdElement : equippedEffectsIdArray) {
                String effectId = (String) equippedEffectIdElement;
                RPGLEffect effect = RPGLFactory.newEffect(effectId);
                assert effect != null;
                equippedEffectsUuidArray.add(effect.getUuid());
            }
            item.put("equipped_effects", equippedEffectsUuidArray);
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
        damage.join(item.getMap("damage"));

        if (damage.isEmpty()) {
            setImprovisedItemDamage(item, "melee");
            setImprovisedItemDamage(item, "thrown");
        } else {
            List<Object> melee = damage.getList("melee");
            List<Object> thrown = damage.getList("thrown");
            if (melee == null || melee.isEmpty()) {
                setImprovisedItemDamage(item, "melee");
            }
            if (thrown == null || thrown.isEmpty()) {
                setImprovisedItemDamage(item, "thrown");
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
     *  @param item       an RPGLItem
     *  @param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     */
    static void setImprovisedItemDamage(RPGLItem item, String attackType) {
        Map<String, Object> damage = item.getMap("damage");
        damage.put(attackType, new ArrayList<>() {{
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
        }});
    }

}
