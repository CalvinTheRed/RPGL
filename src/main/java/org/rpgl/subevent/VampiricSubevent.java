package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Map;

/**
 * This final class provides helper methods for subevents which deal vampiric damage.
 *
 * @author Calvin Withun
 */
public final class VampiricSubevent {

    // TODO allow multiple instances of vampirism to exist on the same Subevent?

    /**
     * This helper method precipitates all healing performed from vampirism.
     *
     * @param subevent the vampiric subevent
     * @param damageByType a JSON object indicating damage dealt by the subevent by damage type
     * @param context the context in which the vampiric subevent was invoked
     *
     * @throws Exception if an exception occurs
     */
    static void handleVampirism(Subevent subevent, JsonObject damageByType, RPGLContext context) throws Exception {
        JsonObject vampirismJson = subevent.json.getJsonObject("vampirism");
        String vampiricDamageType = vampirismJson.getString("damage_type");

        final int vampiricHealing = Calculation.scale(
                getVampiricDamage(damageByType, vampiricDamageType),
                vampirismJson
        );

        if (vampiricHealing > 0) {
            HealingCollection vampiricHealingCollection = new HealingCollection();
            vampiricHealingCollection.joinSubeventData(new JsonObject() {{
                this.putJsonArray("tags", new JsonArray() {{
                    this.asList().addAll(subevent.json.getJsonArray("tags").asList());
                    this.addString("vampiric");
                }});
            }});
            vampiricHealingCollection.setOriginItem(subevent.getOriginItem());
            vampiricHealingCollection.setSource(subevent.getSource());
            vampiricHealingCollection.prepare(context);
            vampiricHealingCollection.addHealing(new JsonObject() {{
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", vampiricHealing);
            }});
            vampiricHealingCollection.setTarget(subevent.getSource());
            vampiricHealingCollection.invoke(context);

            HealingRoll vampiricHealingRoll = new HealingRoll();
            vampiricHealingRoll.joinSubeventData(new JsonObject() {{
                this.putJsonArray("healing", vampiricHealingCollection.getHealingCollection());
                this.putJsonArray("tags", new JsonArray() {{
                    this.asList().addAll(subevent.json.getJsonArray("tags").asList());
                    this.addString("vampiric");
                }});
            }});
            vampiricHealingRoll.setOriginItem(subevent.getOriginItem());
            vampiricHealingRoll.setSource(subevent.getSource());
            vampiricHealingRoll.prepare(context);
            vampiricHealingRoll.setTarget(subevent.getSource());
            vampiricHealingRoll.invoke(context);

            HealingDelivery vampiricHealingDelivery = new HealingDelivery();
            vampiricHealingDelivery.joinSubeventData(new JsonObject() {{
                this.putJsonArray("healing", vampiricHealingRoll.getHealing());
                this.putJsonArray("tags", new JsonArray() {{
                    this.asList().addAll(subevent.json.getJsonArray("tags").asList());
                    this.addString("vampiric");
                }});
            }});
            vampiricHealingDelivery.setOriginItem(subevent.getOriginItem());
            vampiricHealingDelivery.setSource(subevent.getSource());
            vampiricHealingDelivery.prepare(context);
            vampiricHealingDelivery.setTarget(subevent.getSource());
            vampiricHealingDelivery.invoke(context);

            subevent.getSource().receiveHealing(vampiricHealingDelivery, context);
        }
    }

    /**
     * This helper method determines the total vampiric damage dealt by the subevent.
     *
     * @param damageByType a JSON object indicating damage dealt by the subevent by damage type
     * @param vampiricDamageType the damage type which is vampiric, or null if all damage is vampiric
     *
     * @return the total vampiric damage dealt by the subevent
     */
    static int getVampiricDamage(JsonObject damageByType, String vampiricDamageType) {
        if (vampiricDamageType == null) {
            int vampiricDamage = 0;
            for (Map.Entry<String, ?> entry : damageByType.asMap().entrySet()) {
                vampiricDamage += damageByType.getInteger(entry.getKey());
            }
            return vampiricDamage;
        }
        return damageByType.getInteger(vampiricDamageType);
    }

}
