package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This final class provides helper methods for subevents which deal vampiric damage.
 *
 * @author Calvin Withun
 */
public final class VampiricSubevent {

    /**
     * This helper method precipitates all healing performed from vampirism.
     *
     * @param subevent the vampiric subevent
     * @param damageByType a JSON object indicating damage dealt by the subevent by damage type
     * @param context the context in which the vampiric subevent was invoked
     * @param resources a list of resources used to produce the passed subevent
     *
     * @throws Exception if an exception occurs
     */
    static void handleVampirism(Subevent subevent, JsonObject damageByType, RPGLContext context, List<RPGLResource> resources) throws Exception {
        JsonObject vampirismJson = subevent.json.getJsonObject("vampirism");
        String vampiricDamageType = vampirismJson.getString("damage_type");

        final int vampiricHealing = calculateVampiricHealing(
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
            vampiricHealingCollection.prepare(context, resources);
            vampiricHealingCollection.addHealing(new JsonObject() {{
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", vampiricHealing);
            }});
            vampiricHealingCollection.setTarget(subevent.getSource());
            vampiricHealingCollection.invoke(context, resources);

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
            vampiricHealingRoll.prepare(context, resources);
            vampiricHealingRoll.setTarget(subevent.getSource());
            vampiricHealingRoll.invoke(context, resources);

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
            vampiricHealingDelivery.prepare(context, resources);
            vampiricHealingDelivery.setTarget(subevent.getSource());
            vampiricHealingDelivery.invoke(context, resources);

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

    /**
     * This helper method determines how much healing is granted by the vampiric subevent.
     *
     * @param vampiricDamage a JSON object indicating damage dealt by the subevent by damage type
     * @param vampirismJson the JSON object defining how much healing to grant from vampirism
     * @return the total healing granted by the vampiric subevent
     */
    static int calculateVampiricHealing(int vampiricDamage, JsonObject vampirismJson) {
        return Objects.requireNonNullElse(vampirismJson.getBoolean("round_up"), false)
                ? (int) Math.ceil((double) vampiricDamage
                * (double) Objects.requireNonNullElse(vampirismJson.getInteger("numerator"), 1)
                / (double) Objects.requireNonNullElse(vampirismJson.getInteger("denominator"), 2))
                : vampiricDamage
                * Objects.requireNonNullElse(vampirismJson.getInteger("numerator"), 1)
                / Objects.requireNonNullElse(vampirismJson.getInteger("denominator"), 2);
    }

}
