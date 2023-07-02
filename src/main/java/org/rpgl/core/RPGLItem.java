package org.rpgl.core;

import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AttackAbilityCollection;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents any artifact which might appear in an RPGLObject object's inventory. Examples of this include
 * longswords, teacups, and suits of plate armor.
 *
 * @author Calvin Withun
 */
public class RPGLItem extends RPGLTaggable {

    /**
     * Returns the weight of the RPGLItem.
     *
     * @return a Integer representing the RPGLItem's weight
     */
    public int getWeight() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.WEIGHT_ALIAS), 0);
    }

    /**
     * Setter for weight.
     *
     * @param weight a new weight int
     */
    public void setWeight(int weight) {
        this.putInteger(RPGLItemTO.WEIGHT_ALIAS, weight);
    }

    /**
     * Returns the cost of the RPGLItem.
     *
     * @return a Integer representing the RPGLItem's cost
     */
    public int getCost() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.COST_ALIAS), 0);
    }

    /**
     * Setter for cost.
     *
     * @param cost a new cost int
     */
    public void setCost(int cost) {
        this.putInteger(RPGLItemTO.COST_ALIAS, cost);
    }

    /**
     * Returns the event IDs offered by the RPGLItem.
     *
     * @return a JsonObject containing lists of event ID's
     */
    public JsonObject getEvents() {
        return this.getJsonObject(RPGLItemTO.EVENTS_ALIAS);
    }

    /**
     * Setter for events.
     *
     * @param events a new events JsonObject
     */
    public void setEvents(JsonObject events) {
        this.putJsonObject(RPGLItemTO.EVENTS_ALIAS, events);
    }

    /**
     * Returns the item's attack bonus (typically reflective of a magic weapon's bonus to attack rolls). This bonus does
     * not apply to improvised attacks made with the item.
     *
     * @return the item's attack bonus
     */
    public int getAttackBonus() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.ATTACK_BONUS_ALIAS), 0);
    }

    /**
     * Setter for attack bonus.
     *
     * @param attackBonus a new attack bonus int
     */
    public void setAttackBonus(int attackBonus) {
        this.putInteger(RPGLItemTO.ATTACK_BONUS_ALIAS, attackBonus);
    }

    /**
     * Returns the item's damage bonus (typically reflective of a magic weapon's bonus to damage rolls). This bonus will
     * be applied to the first damage type indicated by the weapon's damage rolls, unless it is the damage roll of an
     * improvised attack made using the item.
     *
     * @return the item's damage bonus
     */
    public int getDamageBonus() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.DAMAGE_BONUS_ALIAS), 0);
    }

    /**
     * Setter for damage bonus.
     *
     * @param damageBonus a new attack bonus int
     */
    public void setDamageBonus(int damageBonus) {
        this.putInteger(RPGLItemTO.DAMAGE_BONUS_ALIAS, damageBonus);
    }

    /**
     * Returns a list of effects which will be shared with the item's user when this item is equipped.
     *
     * @return a list of effect UUIDs
     */
    public JsonArray getEquippedEffects() {
        return Objects.requireNonNullElse(this.getJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS), new JsonArray());
    }

    /**
     * Setter for the item's equipped effects.
     *
     * @param equippedEffects a new list of effect UUIDs
     */
    public void setEquippedEffects(JsonArray equippedEffects) {
        this.putJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS, equippedEffects);
    }

    /**
     * Returns a list of resources made available to any object that equips this item.
     *
     * @return a list of resource UUIDs
     */
    public JsonArray getEquippedResources() {
        return Objects.requireNonNullElse(this.getJsonArray(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS), new JsonArray());
    }

    /**
     * Setter for this item's resources.
     *
     * @param equippedResources a new list of resource UUIDs
     */
    public void setEquippedResources(JsonArray equippedResources) {
        this.putJsonArray(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS, equippedResources);
    }

    /**
     * Returns the base armor class value of the RPGLItem. This does not include any dexterity bonuses which are allowed.
     *
     * @return the base armor class of the RPGLItem
     */
    public Integer getArmorClassBase() {
        return this.getInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS);
    }

    /**
     * Setter for armor class base.
     *
     * @param armorClassBase a new armor class base int
     */
    public void setArmorClassBase(int armorClassBase) {
        this.putInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS, armorClassBase);
    }

    /**
     * Returns the maximum dex bonus which can be applied to an RPGLObject's armor class while wearing this RPGLItem. If
     * null, there is no upper limit which can be applied in this way.
     *
     * @return the maximum armor class bonus which can be applied from an RPGLObject's dex modifier
     */
    public Integer getArmorClassDexLimit() {
        return this.getInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS);
    }

    /**
     * Setter for armor class dex limit.
     *
     * @param armorClassDexLimit a new armor class dex limit int
     */
    public void setArmorClassDexLimit(int armorClassDexLimit) {
        this.putInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
    }

    /**
     * Returns the bonus to an RPGLObject's armor class when wielding the RPGLItem as a shield.
     *
     * @return an integer bonus to armor class
     */
    public Integer getArmorClassBonus() {
        return this.getInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS);
    }

    /**
     * Setter for armor class bonus.
     *
     * @param armorClassBonus a new armor class bonus int
     */
    public void setArmorClassBonus(int armorClassBonus) {
        this.putInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * This method returns all RPGLEffect objects which are meant to apply to whichever RPGLObject is wielding the
     * RPGLItem.
     *
     * @return a List of RPGLEffect objects
     */
    public List<RPGLEffect> getEquippedEffectsObjects() {
        JsonArray equippedEffectsUuids = this.getEquippedEffects();
        List<RPGLEffect> effects = new ArrayList<>();
        for (int i = 0; i < equippedEffectsUuids.size(); i++) {
            effects.add(UUIDTable.getEffect(equippedEffectsUuids.getString(i)));
        }
        return effects;
    }

    /**
     * This method returns all RPGLResource objects which are meant to be available to whichever RPGLObject is wielding
     * the RPGLItem.
     *
     * @return a List of RPGLResource objects
     */
    public List<RPGLResource> getEquippedResourcesObjects() {
        JsonArray equippedResourcesUuids = this.getEquippedResources();
        List<RPGLResource> resources = new ArrayList<>();
        for (int i = 0; i < equippedResourcesUuids.size(); i++) {
            resources.add(UUIDTable.getResource(equippedResourcesUuids.getString(i)));
        }
        return resources;
    }

    /**
     * Returns a list of events provided in virtue of wielding this item in one hand.
     *
     * @param wielder the object wielding this item
     * @param context the context in which the item's events are being retrieved
     * @return a list of events
     *
     * @throws Exception if an exception occurs
     */
    public List<RPGLEvent> getOneHandedEventObjects(RPGLObject wielder, RPGLContext context) throws Exception {
        JsonArray eventIds = this.getEvents().getJsonArray("one_hand");
        List<RPGLEvent> events = new ArrayList<>();
        for (int i = 0; i < eventIds.size(); i++) {
            // construct specified events
            RPGLEvent event = RPGLFactory.newEvent(eventIds.getString(i));
            JsonObject subevent = event.getSubevents().getJsonObject(0);
            JsonArray tags = subevent.getJsonArray("tags");
            if (tags == null) {
                tags = new JsonArray();
                subevent.putJsonArray("tags", tags);
            }
            if (!tags.asList().contains("improvised")) {
                tags.asList().addAll(this.getTags().asList());
            }
            event.setOriginItem(this.getUuid());
            events.add(event);

            // construct derived events
            events.addAll(this.getDerivedEvents(event, wielder, context));
        }
        return events;
    }

    /**
     * Returns a list of events provided in virtue of wielding this item in multiple hands.
     *
     * @param wielder the object wielding this item
     * @param context the context in which the item's events are being retrieved
     * @return a list of events
     *
     * @throws Exception if an exception occurs
     */
    public List<RPGLEvent> getMultiHandedEventObjects(RPGLObject wielder, RPGLContext context) throws Exception {
        JsonArray eventIds = this.getEvents().getJsonArray("multiple_hands");
        // TODO redundant code here... helper method?
        List<RPGLEvent> events = new ArrayList<>();
        for (int i = 0; i < eventIds.size(); i++) {
            // construct specified events
            RPGLEvent event = RPGLFactory.newEvent(eventIds.getString(i));
            JsonObject subevent = event.getSubevents().getJsonObject(0);
            JsonArray tags = subevent.getJsonArray("tags");
            if (tags == null) {
                tags = new JsonArray();
                subevent.putJsonArray("tags", tags);
            }
            if (!tags.asList().contains("improvised")) {
                tags.asList().addAll(this.getTags().asList());
            }
            event.setOriginItem(this.getUuid());
            events.add(event);

            // construct derived events
            events.addAll(this.getDerivedEvents(event, wielder, context));
        }
        return events;
    }

    /**
     * Returns a list of events provided in virtue of having equipped this item at all.
     *
     * @return a list of events
     */
    public List<RPGLEvent> getSpecialEventObjects() {
        JsonArray eventIds = this.getEvents().getJsonArray("special");
        List<RPGLEvent> events = new ArrayList<>();
        for (int i = 0; i < eventIds.size(); i++) {
            RPGLEvent event = RPGLFactory.newEvent(eventIds.getString(i));
            event.setOriginItem(this.getUuid());
            events.add(event);
        }
        return events;
    }

    /**
     * Updates the source and target of RPGLEffect objects associated with this RPGLItem. This is to be used whenever a
     * RPGLObject equips the RPGLItem.
     *
     * @param wielder an RPGLObject which has equipped this RPGLItem
     */
    public void updateEquippedEffects(RPGLObject wielder) {
        JsonArray whileEquippedEffects = this.getEquippedEffects();
        for (int i = 0; i < whileEquippedEffects.size(); i++) {
            String effectUuid = whileEquippedEffects.getString(i);
            RPGLEffect effect = UUIDTable.getEffect(effectUuid);
            effect.setSource(wielder);
            effect.setTarget(wielder);
        }
    }

    /**
     * This helper method returns a list of events derived from a common event. This method is intended to be used to
     * collect weapon attack events which use non-standard ability scores to make their attack roll.
     *
     * @param event the base event to derive other events from
     * @param wielder the object wielding this item
     * @param context the context in which the derived events are being retrieved
     * @return a list of events
     *
     * @throws Exception if an exception occurs
     */
    List<RPGLEvent> getDerivedEvents(RPGLEvent event, RPGLObject wielder, RPGLContext context) throws Exception {
        List<RPGLEvent> derivedEvents = new ArrayList<>();

        AttackAbilityCollection attackAbilityCollection = new AttackAbilityCollection();
        attackAbilityCollection.setOriginItem(this.getUuid());
        attackAbilityCollection.setSource(wielder);
        attackAbilityCollection.prepare(context);
        attackAbilityCollection.setTarget(wielder);
        attackAbilityCollection.invoke(context);
        JsonArray attackAbilities = attackAbilityCollection.getAbilities();

        for (int i = 0; i < attackAbilities.size(); i++) {
            RPGLEvent derivedEvent = new RPGLEvent();
            derivedEvent.join(event.deepClone());
            JsonArray subevents = derivedEvent.getSubevents();
            if (subevents.size() == 1) {
                JsonObject subeventJson = subevents.getJsonObject(0);
                if (Objects.equals("attack_roll", subeventJson.getString("subevent"))
                        && Objects.equals("str", subeventJson.getString("attack_ability"))) {
                    subeventJson.putString("attack_ability", attackAbilities.getString(i));
                    derivedEvents.add(derivedEvent);
                }
            }
        }
        return derivedEvents;
    }

}
