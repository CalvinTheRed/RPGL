package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.CalculateAbilityScore;
import org.rpgl.subevent.CalculateBaseArmorClass;
import org.rpgl.subevent.CalculateMaximumHitPoints;
import org.rpgl.subevent.CalculateProficiencyBonus;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.GetEvents;
import org.rpgl.subevent.GetObjectTags;
import org.rpgl.subevent.HealingDelivery;
import org.rpgl.subevent.InfoSubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointDelivery;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents anything which might appear on a battle map. Examples of this include buildings, Goblins, and
 * discarded items.
 *
 * @author Calvin Withun
 */
public class RPGLObject extends RPGLTaggable {

    /**
     * Returns the RPGLObject's ability scores.
     *
     * @return a JsonObject containing the RPGLObject's ability scores
     */
    public JsonObject getAbilityScores() {
        return this.getJsonObject(RPGLObjectTO.ABILITY_SCORES_ALIAS);
    }

    /**
     * Setter for ability scores.
     *
     * @param abilityScores a new ability scores JsonObject
     */
    public void setAbilityScores(JsonObject abilityScores) {
        this.putJsonObject(RPGLObjectTO.ABILITY_SCORES_ALIAS, abilityScores);
    }

    /**
     * Returns the RPGLObject's health data. This includes hit dice, base health, max health, current health, and
     * temporary health.
     *
     * @return a JsonObject containing health data for the RPGLObject
     */
    public JsonObject getHealthData() {
        return this.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS);
    }

    /**
     * Setter for health data.
     *
     * @param healthData a new health data JsonObject
     */
    public void setHealthData(JsonObject healthData) {
        this.putJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS, healthData);
    }

    /**
     * Returns all items currently equipped to the RPGLObject, mapped to their corresponding equipment slots.
     *
     * @return a JsonObject containing RPGLItems and their equipment slots
     */
    public JsonObject getEquippedItems() {
        return this.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
    }

    /**
     * Setter for equipped items.
     *
     * @param equippedItems a new equipped items JsonObject
     */
    public void setEquippedItems(JsonObject equippedItems) {
        this.putJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, equippedItems);
    }

    /**
     * Returns the UUIDs of all items held by the RPGLObject.
     *
     * @return a JsonArray of RPGLItem UUIDs
     */
    public JsonArray getInventory() {
        return this.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
    }

    /**
     * Setter for inventory.
     *
     * @param inventory a new inventory JsonArray
     */
    public void setInventory(JsonArray inventory) {
        this.putJsonArray(RPGLObjectTO.INVENTORY_ALIAS, inventory);
    }

    /**
     * Returns the IDs (not UUIDs) of all RPGLEvents innately provided to the RPGLObject.
     *
     * @return a JsonArray of RPGLEffect IDs
     */
    public JsonArray getEvents() {
        return this.getJsonArray(RPGLObjectTO.EVENTS_ALIAS);
    }

    /**
     * Setter for events.
     *
     * @param events a new events JsonArray
     */
    public void setEvents(JsonArray events) {
        this.putJsonArray(RPGLObjectTO.EVENTS_ALIAS, events);
    }

    /**
     * Returns the UUIDs of all RPGLEffects applied to the RPGLObject, not including any provided through equipped items.
     *
     * @return a JsonArray of RPGLItem UUIDs
     */
    public JsonArray getEffects() {
        return this.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS);
    }

    /**
     * Setter for effects.
     *
     * @param effects a new effects JsonArray
     */
    public void setEffects(JsonArray effects) {
        this.putJsonArray(RPGLObjectTO.EFFECTS_ALIAS, effects);
    }

    /**
     * Returns the base proficiency bonus of the RPGLObject, not modified by any effects.
     *
     * @return the RPGLObject's base proficiency bonus
     */
    public Integer getProficiencyBonus() {
        return this.getInteger(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS);
    }

    /**
     * Setter for proficiency bonus.
     *
     * @param proficiencyBonus a new proficiency bonus int
     */
    public void setProficiencyBonus(int proficiencyBonus) {
        this.putInteger(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
    }

    /**
     * Returns the resources UUID array of the RPGLObject. This will include any RPGLResource UUIDs granted temporarily.
     *
     * @return the RPGLObject's resources array
     */
    public JsonArray getResources() {
        return this.getJsonArray(RPGLObjectTO.RESOURCES_ALIAS);
    }

    /**
     * Setter for resources.
     *
     * @param resources a new resources UUID array
     */
    public void setResources(JsonArray resources) {
        this.putJsonArray(RPGLObjectTO.RESOURCES_ALIAS, resources);
    }

    /**
     * Getter for classes.
     *
     * @return an array of class and level data
     */
    public JsonArray getClasses() {
        return this.getJsonArray(RPGLObjectTO.CLASSES_ALIAS);
    }

    /**
     * Setter for classes.
     *
     * @param classes a new array of class and level data
     */
    public void setClasses(JsonArray classes) {
        this.putJsonArray(RPGLObjectTO.CLASSES_ALIAS, classes);
    }

    /**
     * Getter for races.
     *
     * @return an array of race IDs
     */
    public JsonArray getRaces() {
        return this.getJsonArray(RPGLObjectTO.RACES_ALIAS);
    }

    /**
     * Setter for races.
     *
     * @param races a new array of race IDs
     */
    public void setRaces(JsonArray races) {
        this.putJsonArray(RPGLObjectTO.RACES_ALIAS, races);
    }

    /**
     * Getter for challenge rating.
     *
     * @return the object's challenge rating
     */
    public Double getChallengeRating() {
        return this.getDouble(RPGLObjectTO.CHALLENGE_RATING_ALIAS);
    }

    /**
     * Setter for challenge rating.
     *
     * @param challengeRating a new challenge rating
     */
    public void setChallengeRating(double challengeRating) {
        this.putDouble(RPGLObjectTO.CHALLENGE_RATING_ALIAS, challengeRating);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * Returns a List of all RPGLEvent objects associated with the RPGLObject. This includes RPGLEvents granted by
     * effects.
     *
     * @param context the context in which the RPGLEvents are being collected
     * @return a List of RPGLEvent objects
     *
     * @throws Exception if an exception occurs
     */
    public List<RPGLEvent> getEventObjects(RPGLContext context) throws Exception {
        List<RPGLEvent> events = new ArrayList<>();
        JsonArray eventsArray;

        // get innate events
        eventsArray = this.getEvents();
        for (int i = 0; i < eventsArray.size(); i++) {
            events.add(RPGLFactory.newEvent(eventsArray.getString(i)));
        }

        // get events from effects
        GetEvents getEvents = new GetEvents();
        getEvents.setSource(this);
        getEvents.prepare(context, List.of());
        getEvents.setTarget(this);
        getEvents.invoke(context, List.of());
        events.addAll(getEvents.getEvents());

        // get events granted by equipped items
        JsonObject equippedItems = this.getEquippedItems();
        HashMap<RPGLItem, Integer> wieldedItemsHandedness = new HashMap<>();
        for (Map.Entry<String, Object> entry : equippedItems.asMap().entrySet()) {
            String key = entry.getKey();
            RPGLItem item = UUIDTable.getItem(equippedItems.getString(key));
            if (key.toLowerCase().contains("hand")) {
                // item is wielded and must be checked for handedness
                if (wieldedItemsHandedness.containsKey(item)) {
                    wieldedItemsHandedness.put(item, wieldedItemsHandedness.get(item) + 1);
                } else {
                    wieldedItemsHandedness.put(item, 1);
                }
            } else {
                // special events only
                events.addAll(item.getSpecialEventObjects());
            }
        }
        for (Map.Entry<RPGLItem, Integer> entry : wieldedItemsHandedness.entrySet()) {
            if (entry.getValue() == 1) {
                events.addAll(entry.getKey().getOneHandedEventObjects(this, context));
            } else {
                events.addAll(entry.getKey().getMultiHandedEventObjects(this, context));
            }
        }

        return events;
    }

    /**
     * Returns a List of all RPGLEffect objects associated with the RPGLObject. This includes RPGLEffects granted by
     * equipped items.
     *
     * @return a List of RPGLEffect objects
     */
    public List<RPGLEffect> getEffectObjects() {
        List<RPGLEffect> effects = new ArrayList<>();

        JsonArray effectUuids = this.getEffects();
        for (int i = 0; i < effectUuids.size(); i++) {
            effects.add(UUIDTable.getEffect(effectUuids.getString(i)));
        }

        JsonObject equippedItems = this.getEquippedItems();
        for (Map.Entry<String, ?> equippedItemEntry : equippedItems.asMap().entrySet()) {
            String equippedItemUuid = equippedItems.getString(equippedItemEntry.getKey());
            RPGLItem equippedItem = UUIDTable.getItem(equippedItemUuid);
            effects.addAll(equippedItem.getEquippedEffectsObjects());
        }

        return effects;
    }

    /**
     * Returns a List of all RPGLResource objects associated with the RPGLObject. This includes temporary RPGLResources
     * granted by effects.
     *
     * @return a List of RPGLResource objects
     */
    public List<RPGLResource> getResourceObjects() {
        List<RPGLResource> resources = new ArrayList<>();

        // get personal resources
        JsonArray resourceUuids = this.getResources();
        for (int i = 0; i < resourceUuids.size(); i++) {
            resources.add(UUIDTable.getResource(resourceUuids.getString(i)));
        }

        // get resources from equipped items
        JsonObject equippedItems = this.getEquippedItems();
        for (Map.Entry<String, Object> equippedItemEntry : equippedItems.asMap().entrySet()) {
            RPGLItem equippedItem = UUIDTable.getItem(equippedItems.getString(equippedItemEntry.getKey()));
            resources.addAll(equippedItem.getEquippedResourcesObjects());
        }

        return resources;
    }

    /**
     * This method exhausts resources and then precipitates the process of invoking an RPGLEvent.
     *
     * @param targets an array of RPGLObjects targeted by the RPGLEvent being invoked
     * @param event the RPGLEvent being invoked
     * @param resources a list of resources to be exhausted through the invocation of the passed event
     * @param context the RPGLContext in which the RPGLEvent is invoked
     *
     * @throws Exception if an exception occurs.
     */
    public void invokeEvent(RPGLObject[] targets, RPGLEvent event, List<RPGLResource> resources, RPGLContext context) throws Exception {
        for (RPGLResource resource : resources) {
            resource.exhaust();
        }
        event.scale(resources);

        String sourceUuid = event.getString("source");
        RPGLObject source = sourceUuid != null
                ? UUIDTable.getObject(sourceUuid)
                : this;

        JsonArray subeventJsonArray = event.getJsonArray("subevents");
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
            String subeventId = subeventJson.getString("subevent");
            Subevent subevent = Subevent.SUBEVENTS.get(subeventId).clone(subeventJson);
            subevent.setSource(source);
            subevent.setOriginItem(event.getOriginItem());
            subevent.prepare(context, resources);
            for (RPGLObject target : targets) {
                Subevent subeventClone = subevent.clone();
                subeventClone.setTarget(target);
                subeventClone.invoke(context, resources);
            }
        }
    }

    /**
     * This method presents a Subevent to the RPGLObject's RPGLEffects in order to influence the result of the Subevent.
     *
     * @param subevent a Subevent being invoked
     * @param context the context in which the Subevent is being processed
     * @param resources a list of resources used to produce the passed subevent
     * @return true if one of the RPGLObject's RPGLEffects modified the passed Subevent
     *
     * @throws Exception if an exception occurs
     */
    public boolean processSubevent(Subevent subevent, RPGLContext context, List<RPGLResource> resources) throws Exception {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffectObjects()) {
            wasSubeventProcessed |= effect.processSubevent(subevent, context, resources);
        }
        for (RPGLResource resource : getResourceObjects()) {
            resource.processSubevent(subevent, this);
        }
        return wasSubeventProcessed;
    }

    /**
     * This method adds an RPGLEffect to the RPGLObject. This will do nothing if the object already possesses the passed
     * RPGLEffect.
     * <br>
     * TODO should effects be restricted if they are of the same type? double-dipping
     *
     * @param effect a RPGLEffect
     */
    public void addEffect(RPGLEffect effect) {
        if (!this.getEffects().asList().contains(effect.getUuid())) {
            this.getEffects().addString(effect.getUuid());
        }
    }

    /**
     * Removes a RPGLEffect from the object. Note that this does NOT cause the RPGLResource to be unregistered from
     * UUIDTable.
     *
     * @param effectUuid the UUID for a RPGLEffect
     * @return true if the effect was removed, false otherwise
     */
    public boolean removeEffect(String effectUuid) {
        return this.getEffects().asList().remove(effectUuid);
    }

    /**
     * Gives a new RPGLResource to the object. This will do nothing if the object already possesses the passed
     * RPGLResource.
     *
     * @param resource a RPGLResource
     */
    public void addResource(RPGLResource resource) {
        if (!this.getResources().asList().contains(resource.getUuid())) {
            this.getResources().addString(resource.getUuid());
        }
    }

    /**
     * Removes a RPGLResource from the object. Note that this causes the RPGLResource to be unregistered from UUIDTable
     * as well.
     *
     * @param resourceUuid the UUID for a RPGLResource
     */
    public void removeResource(String resourceUuid) {
        if (this.getResources().asList().remove(resourceUuid)) {
            UUIDTable.unregister(resourceUuid);
        }
    }

    /**
     * This method determines the proficiency bonus of the RPGLObject.
     *
     * @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     * @return this RPGLObject's proficiency bonus
     *
     * @throws Exception if an exception occurs.
     */
    public int getEffectiveProficiencyBonus(RPGLContext context) throws Exception {
        CalculateProficiencyBonus calculateProficiencyBonus = new CalculateProficiencyBonus();
        calculateProficiencyBonus.setSource(this);
        calculateProficiencyBonus.prepare(context, List.of());
        calculateProficiencyBonus.setTarget(this);
        calculateProficiencyBonus.invoke(context, List.of());
        return calculateProficiencyBonus.get();
    }

    /**
     * This method returns the RPGLObject's ability score matching the ability name provided.
     *
     * @param ability the name of an ability score
     * @param context the context in which this ability score is being calculated
     * @return a numerical ability score
     *
     * @throws Exception if an exception occurs
     */
    public int getAbilityScoreFromAbilityName(String ability, RPGLContext context) throws Exception {
        CalculateAbilityScore calculateAbilityScore = new CalculateAbilityScore();
        calculateAbilityScore.joinSubeventData(new JsonObject() {{
            this.putString("ability", ability);
        }});
        calculateAbilityScore.setSource(this);
        calculateAbilityScore.prepare(context, List.of());
        calculateAbilityScore.setTarget(this);
        calculateAbilityScore.invoke(context, List.of());
        return calculateAbilityScore.get();
    }

    /**
     * This method determines the RPGLObject's ability score modifier for a specified ability score.
     *
     * @param ability the ability score whose modifier will be determined
     * @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     * @return the modifier of the target ability
     *
     * @throws Exception if an exception occurs.
     */
    public int getAbilityModifierFromAbilityName(String ability, RPGLContext context) throws Exception {
        return getAbilityModifierFromAbilityScore(this.getAbilityScoreFromAbilityName(ability, context));
    }

    /**
     * This method determines the modifier for an ability score.
     *
     * @param abilityScore an ability score number
     * @return the modifier for the passed score
     */
    static int getAbilityModifierFromAbilityScore(int abilityScore) {
        if (abilityScore < 10) {
            // integer division rounds toward zero, so abilityScore must be
            // adjusted to calculate the correct values for negative modifiers
            abilityScore--;
        }
        return (abilityScore - 10) / 2;
    }

    /**
     * This method is how a RPGLObject is intended to take damage.
     *
     * @param damageDelivery a DamageDelivery object containing damage data
     * @param context the RPGLContext in which the RPGLObject takes damage
     * @return a JSON object indicating the final damage suffered by the target by damage type
     *
     * @throws Exception if an exception occurs.
     */
    public JsonObject receiveDamage(DamageDelivery damageDelivery, RPGLContext context) throws Exception {
        JsonObject damageJson = damageDelivery.getDamage();
        DamageAffinity damageAffinity = new DamageAffinity();
        for (Map.Entry<String, ?> entry : damageJson.asMap().entrySet()) {
            damageAffinity.addDamageType(entry.getKey());
        }
        damageAffinity.setSource(damageDelivery.getSource());
        damageAffinity.prepare(context, List.of());
        damageAffinity.setTarget(this);
        damageAffinity.invoke(context, List.of());

        JsonObject finalDamageByType = new JsonObject();
        for (Map.Entry<String, ?> entry : damageJson.asMap().entrySet()) {
            String damageType = entry.getKey();
            int typedDamage = damageJson.getInteger(entry.getKey());

            if (!damageAffinity.isImmune(damageType)) {
                if (damageAffinity.isResistant(damageType)) {
                    typedDamage /= 2;
                }
                if (damageAffinity.isVulnerable(damageType)) {
                    typedDamage *= 2;
                }
                if (typedDamage > 0) {
                    if (finalDamageByType.asMap().containsKey(damageType)) {
                        finalDamageByType.putInteger(damageType, finalDamageByType.getInteger(damageType) + typedDamage);
                    } else {
                        finalDamageByType.putInteger(damageType, typedDamage);
                    }
                }
            }
        }

        int damage = 0;
        for (Map.Entry<String, ?> entry : finalDamageByType.asMap().entrySet()) {
            damage += finalDamageByType.getInteger(entry.getKey());
        }
        this.reduceHitPoints(damage, context);

        return finalDamageByType;
    }

    /**
     * This method accepts a HealingDelivery Subevent to provide healing to the RPGLObject. This method cannot be used
     * to heal the object beyond its hit point maximum.
     *
     * @param healingDelivery a HealingDelivery Subevent containing a quantity of healing to apply to the RPGLObject
     * @param context the context in which the RPGLObject is receiving healing.
     *
     * @throws Exception if an exception occurs
     */
    public void receiveHealing(HealingDelivery healingDelivery, RPGLContext context) throws Exception {
        JsonObject healthData = this.getHealthData();
        healthData.putInteger("current", healthData.getInteger("current") + healingDelivery.getHealing());
        int maximumHitPoints = this.getMaximumHitPoints(context);
        if (healthData.getInteger("current") > maximumHitPoints) {
            healthData.putInteger("current", maximumHitPoints);
        }
    }

    /**
     * This method accepts a HealingDelivery Subevent to provide healing to the RPGLObject. This method cannot be used
     * to heal the object beyond its hit point maximum.
     *
     * @param temporaryHitPointDelivery a TemporaryHitPointsDelivery Subevent containing a quantity of temporary hit
     *                                   points to apply to the RPGLObject
     * @param riderEffects a list of effects to be applied if the temporary hit points from temporaryHitPointsDelivery
     *                     are applied
     */
    public void receiveTemporaryHitPoints(TemporaryHitPointDelivery temporaryHitPointDelivery, JsonArray riderEffects) {
        JsonObject healthData = this.getHealthData();
        if (healthData.getInteger("temporary") < temporaryHitPointDelivery.getTemporaryHitPoints()) {
            healthData.putInteger("temporary", temporaryHitPointDelivery.getTemporaryHitPoints());
            for (int i = 0; i < riderEffects.size(); i++) {
                RPGLEffect effect = RPGLFactory.newEffect(riderEffects.getString(i));
                effect.setSource(temporaryHitPointDelivery.getSource());
                effect.setTarget(temporaryHitPointDelivery.getTarget());
                this.addEffect(effect);
            }
        }
    }

    /**
     * This method calculates the RPGLObject's maximum hit points.
     *
     * @param context the context in which the RPGLObject's maximum hit points are being calculated
     * @return the RPGLObject's maximum hit points
     *
     * @throws Exception if an exception occurs
     */
    public int getMaximumHitPoints(RPGLContext context) throws Exception {
        CalculateMaximumHitPoints calculateMaximumHitPoints = new CalculateMaximumHitPoints();
        calculateMaximumHitPoints.setSource(this);
        calculateMaximumHitPoints.prepare(context, List.of());
        calculateMaximumHitPoints.setTarget(this);
        calculateMaximumHitPoints.invoke(context, List.of());
        return calculateMaximumHitPoints.get();
    }

    /**
     * This helper method directly reduces the hit points of the RPGLObject. This is not intended to be called directly.
     *
     * @param amount a quantity of damage
     * @param context the context in which the RPGLObject's hit points are reduced
     *
     * @throws Exception if an exception occurs
     */
    void reduceHitPoints(int amount, RPGLContext context) throws Exception {
        Map<String, Object> healthData = this.getHealthData().asMap();
        Integer temporaryHitPoints = (Integer) healthData.get("temporary");
        Integer currentHitPoints = (Integer) healthData.get("current");
        if (amount >= temporaryHitPoints) {
            if (temporaryHitPoints > 0) {
                amount -= temporaryHitPoints;
                temporaryHitPoints = 0;
                currentHitPoints -= amount;
                healthData.put("temporary", temporaryHitPoints);
                healthData.put("current", currentHitPoints);
                this.invokeInfoSubevent(context, "reduced_to_zero_temporary_hit_points");
            } else {
                currentHitPoints -= amount;
                healthData.put("current", currentHitPoints);
            }
        } else {
            temporaryHitPoints -= amount;
            healthData.put("temporary", temporaryHitPoints);
        }
        if (currentHitPoints <= -this.getMaximumHitPoints(context)) {
            healthData.put("current", 0);
            this.invokeInfoSubevent(context, "reduced_to_zero_hit_points", "killed"); // TODO is there a more elegant way to do this?
        } else if (currentHitPoints < 0) {
            healthData.put("current", 0);
            this.invokeInfoSubevent(context, "reduced_to_zero_hit_points");
        }
    }

    /**
     * This method causes the RPGLObject to invoke an InfoSubevent using the passed tags.
     *
     * @param tags the tags to be stored in the InfoSubevent
     * @param context the context in which the InfoSubevent is invoked
     * @return the InfoSubevent which was invoked
     *
     * @throws Exception if an exception occurs
     */
    public InfoSubevent invokeInfoSubevent(RPGLContext context, String... tags) throws Exception {
        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.joinSubeventData(new JsonObject() {{
            /*{
                "tags": [ <tags> ]
            }*/
            JsonArray subeventTags = new JsonArray();
            for (String tag : tags) {
                subeventTags.addString(tag);
            }
            this.putJsonArray("tags", subeventTags);
        }});
        infoSubevent.setSource(this);
        infoSubevent.prepare(context, List.of());
        infoSubevent.setTarget(this);
        infoSubevent.invoke(context, List.of());
        return infoSubevent;
    }

    /**
     * This method determines the base armor class of the RPGLObject.
     *
     * @param context the RPGLContext in which the RPGLObject's base armor class is determined
     * @return the RPGLObject's base armor class
     *
     * @throws Exception if an exception occurs.
     */
    public int getBaseArmorClass(RPGLContext context) throws Exception {
        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();
        calculateBaseArmorClass.setSource(this);
        calculateBaseArmorClass.prepare(context, List.of());
        calculateBaseArmorClass.setTarget(this);
        calculateBaseArmorClass.invoke(context, List.of());
        return calculateBaseArmorClass.get();
    }

    /**
     * This method adds a RPGLItem to the RPGLObject's inventory.
     *
     * @param itemUuid a RPGLItem's UUID String
     */
    public void giveItem(String itemUuid) {
        JsonArray inventory = this.getInventory();
        if (!inventory.asList().contains(itemUuid)) {
            inventory.addString(itemUuid);
        }
    }

    /**
     * This method causes the RPGLObject to equip a RPGLItem in a specified equipment slot. The RPGLItem will not be
     * equipped if it is not already in the RPGLObject's inventory.
     *
     * @param itemUuid a RPGLItem's UUID String
     * @param equipmentSlot an equipment slot name (can be anything other than <code>"inventory"</code>)
     */
    public void equipItem(String itemUuid, String equipmentSlot) {
        JsonArray inventory = this.getInventory();
        if (inventory.asList().contains(itemUuid)) {
            JsonObject equippedItems = this.getEquippedItems();
            equippedItems.putString(equipmentSlot, itemUuid);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            item.updateEquippedEffects(this);
            // TODO account for 2-handed items...
        }
    }

    /**
     * Un-equips an RPGLItem from an inventory slot, unless an InfoSubevent created to announce this is canceled.
     *
     * @param equipmentSlot an equipment slot String
     * @param force if true, the RPGLItem will be removed regardless of whether the associated InfoSubevent is canceled
     * @param context the context in which the RPGLItem is being un-equipped
     *
     * @throws Exception if an exception occurs
     */
    public void unequipItem(String equipmentSlot, boolean force, RPGLContext context) throws Exception {
        JsonObject equippedItems = this.getEquippedItems();
        JsonArray itemTags = UUIDTable.getItem(equippedItems.getString(equipmentSlot)).getTags();
        String[] infoSubeventTags = new String[itemTags.size() + 1];
        infoSubeventTags[0] = "unequip_item";
        for (int i = 0; i < itemTags.size() ; i++) {
            infoSubeventTags[i + 1] = itemTags.getString(i);
        }
        if (force || this.invokeInfoSubevent(context, infoSubeventTags).isNotCanceled()) {
            this.getEquippedItems().removeString(equipmentSlot);
        }
    }

    /**
     * This method returns all tags which are currently applied to the RPGLObject. This includes any tags granted
     * through RPGLEffects which are not supplied by the RPGLObject itself.
     *
     * @param context the context in which the RPGLObject's tags are being listed
     * @return a list of tags applied to the RPGLObject
     *
     * @throws Exception if an exception occurs
     */
    public ArrayList<String> getAllTags(RPGLContext context) throws Exception {
        ArrayList<String> tagsList = new ArrayList<>();
        JsonArray tags;

        tags = this.getTags();
        for (int i = 0; i < tags.size(); i++) {
            tagsList.add(tags.getString(i));
        }

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.setSource(this);
        getObjectTags.prepare(context, List.of());
        getObjectTags.setTarget(this);
        getObjectTags.invoke(context, List.of());

        tags = getObjectTags.getObjectTags();
        for (int i = 0; i < tags.size(); i++) {
            tagsList.add(tags.getString(i));
        }

        return tagsList;
    }

    /**
     * Returns the object's level in a given class.
     *
     * @param classId a class ID
     * @return the object's level in the passed class
     */
    public Integer getLevel(String classId) {
        JsonArray classes = this.getClasses();
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classData = classes.getJsonObject(i);
            if (classData.getString("id").equals(classId)) {
                return classData.getInteger("level");
            }
        }
        return 0;
    }

    /**
     * Returns the object's level.
     *
     * @return the object's level
     */
    public int getLevel() {
        JsonArray classes = this.getClasses();
        ArrayList<String> classIds = new ArrayList<>();
        ArrayList<String> nestedClassIds = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classData = classes.getJsonObject(i);
            String classId = classData.getString("id");
            classIds.add(classId);
            for (Map.Entry<String, ?> nestedClassEntry : RPGLFactory.getClass(classId).getNestedClasses().asMap().entrySet()) {
                nestedClassIds.add(nestedClassEntry.getKey());
            }
            for (Map.Entry<String, ?> additionalNestedClassEntry : classData.getJsonObject("additional_nested_classes").asMap().entrySet()) {
                nestedClassIds.add(additionalNestedClassEntry.getKey());
            }
        }
        classIds.removeAll(nestedClassIds);
        int level = 0;
        for (String classId : classIds) {
            level += this.getLevel(classId);
        }
        return level;
    }

    /**
     * Levels up the object for the passed class.
     *
     * @param classId a class ID
     * @param choices a JSON object indicating any choices required to level up in the passed class
     */
    public void levelUp(String classId, JsonObject choices) {
        RPGLClass rpglClass = RPGLFactory.getClass(classId);
        if (this.getLevel() == 0) {
            rpglClass.grantStartingFeatures(this, choices);
        } else {
            rpglClass.levelUpRPGLObject(this, choices);
        }
        this.levelUpNestedClasses(classId, choices);
        this.levelUpRaces(choices, this.getLevel());
    }

    /**
     * This helper method updates race-granted features upon level-up.
     *
     * @param choices a JSON object indicating any choices required to level up, given the object's races
     * @param level the object's new level
     */
    void levelUpRaces(JsonObject choices, int level) {
        JsonArray races = this.getRaces();
        for (int i = 0; i < races.size(); i++) {
            String raceId = races.getString(i);
            RPGLRace race = RPGLFactory.getRace(raceId);
            race.levelUpRPGLObject(this, choices, level);
        }
    }

    /**
     * Levels up a class's nested classes.
     *
     * @param classId a class ID whose nested classes must be leveled up
     * @param choices a JSON object indicating any choices required to level up the object's nested classes
     */
    public void levelUpNestedClasses(String classId, JsonObject choices) {
        for (String nestedClassId : this.getNestedClassIds(classId)) {
            RPGLClass rpglClass = RPGLFactory.getClass(nestedClassId);
            int intendedLevel = this.calculateLevelForNestedClass(nestedClassId);
            int currentLevel = this.getLevel(nestedClassId);
            while (currentLevel < intendedLevel) {
                rpglClass.levelUpRPGLObject(this, choices);
                currentLevel = this.getLevel(nestedClassId);
            }
        }
    }

    /**
     * This helper method returns a lost of class IDs for all nested classes indicated by a given class.
     *
     * @param classId a class
     * @return a list of class IDs
     */
    List<String> getNestedClassIds(String classId) {
        RPGLClass rpglClass = RPGLFactory.getClass(classId);
        JsonObject nestedClasses = rpglClass.getNestedClasses();
        ArrayList<String> nestedClassIds = new ArrayList<>(nestedClasses.asMap().keySet());
        JsonArray classes = this.getClasses();
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classData = classes.getJsonObject(i);
            if (classData.getString("id").equals(classId)) {
                nestedClassIds.addAll(classData.getJsonObject("additional_nested_classes").asMap().keySet());
                break;
            }
        }
        return nestedClassIds;
    }

    /**
     * This helper method calculates what level the object should be in a nested class, accounting for all non-nested
     * classes which contribute to it.
     *
     * @param nestedClassId a nested class's class ID
     * @return the level the object should be in the passed nested class
     */
    int calculateLevelForNestedClass(String nestedClassId) {
        int nestedClassLevel = 0;
        JsonArray classes = this.getClasses();
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classData = classes.getJsonObject(i);
            RPGLClass rpglClass = RPGLFactory.getClass(classData.getString("id"));
            JsonObject nestedClasses = rpglClass.getNestedClasses();
            JsonObject additionalNestedClasses = classData.getJsonObject("additional_nested_classes");
            JsonObject nestedClassData = null;
            if (nestedClasses.asMap().containsKey(nestedClassId)) {
                nestedClassData = nestedClasses.getJsonObject(nestedClassId);
            } else if (additionalNestedClasses.asMap().containsKey(nestedClassId)) {
                nestedClassData = additionalNestedClasses.getJsonObject(nestedClassId);
            }
            if (nestedClassData != null) {
                int classLevel = classData.getInteger("level");
                int scale = nestedClassData.getInteger("scale");
                boolean roundUp = Objects.requireNonNullElse(nestedClassData.getBoolean("round_up"), false);
                if (roundUp) {
                    nestedClassLevel += Math.ceil(classLevel / (double) scale);
                } else {
                    nestedClassLevel += classLevel / (double) scale;
                }
            }
        }
        return nestedClassLevel;
    }

    /**
     * Adds a given class to the nested class list of another class.
     *
     * @param classId the class ID of the class to be given another nested class
     * @param additionalNestedClassId the class ID of the class to be added as a nested class
     * @param scale the scale of how many levels it takes to increment the nested class level by 1
     * @param roundUp whether <code>scale</code> should round up when evaluating the nested class's intended level
     */
    public void addAdditionalNestedClass(String classId, String additionalNestedClassId, int scale, boolean roundUp) {
        JsonArray classes = this.getClasses();
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classData = classes.getJsonObject(i);
            if (classData.getString("id").equals(classId)) {
                classData.getJsonObject("additional_nested_classes").putJsonObject(additionalNestedClassId, new JsonObject() {{
                    this.putInteger("scale", scale);
                    this.putBoolean("round_up", roundUp);
                }});
            }
        }
    }

    /**
     * Calculates the object's proficiency bonus by level.
     *
     * @return the object's proficiency bonus according to its level
     */
    public int getProficiencyBonusByLevel() {
        return (int) (1 + Math.ceil(this.getLevel() / 4.0));
    }

    /**
     * Returns a list of all resources possessed by this object which contain a given tag.
     *
     * @param tag a resource tag
     * @return a list of RPGLResource objects
     */
    public List<RPGLResource> getResourcesWithTag(String tag) {
        return this.getResourceObjects().stream().filter(resource -> resource.hasTag(tag)).toList();
    }

    /**
     * Cause the object to make an ability check and return the result.
     *
     * @param ability an ability to use for the check
     * @param skill a skill to use for the check
     * @param context the context in which the check takes place
     * @return the result of the ability check
     *
     * @throws Exception if an exception occurs
     */
    public int abilityCheck(String ability, String skill, RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", ability);
            this.putString("skill", skill);
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});

        abilityCheck.setSource(this);
        abilityCheck.prepare(context, List.of());
        abilityCheck.setTarget(this);
        abilityCheck.invoke(context, List.of());
        return abilityCheck.get();
    }

    /**
     * Give a new event to the object.
     *
     * @param eventId an event ID
     */
    public void giveEvent(String eventId) {
        this.getEvents().addString(eventId);
    }

    /**
     * Remove an existing event from the object.
     *
     * @param eventId an event ID
     */
    public void removeEvent(String eventId) {
        this.getEvents().asList().remove(eventId);
    }

}
