package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

import java.util.Map;

/**
 * This Subevent is dedicated to delivering a quantity of typed damage to an RPGLObject.
 * <br>
 * <br>
 * Source: an RPGLObject dealing damage
 * <br>
 * Target: an RPGLObject suffering damage
 *
 * @author Calvin Withun
 */
public class DamageDelivery extends Subevent {

    public DamageDelivery() {
        super("damage_delivery");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageDelivery();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageDelivery();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
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
     * 	This method returns the typed damage being delivered to <code>target</code>.
     * 	</p>
     *
     *  @return an object of damage types and values
     */
    public Map<String, Object> getDamage() {
        return this.subeventJson.getJsonObject("damage").asMap();
    }

}
