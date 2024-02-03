package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.CalculateAbilityScore;
import org.rpgl.subevent.CalculateBaseArmorClass;
import org.rpgl.subevent.CalculateMaximumHitPoints;
import org.rpgl.subevent.CalculateProficiencyBonus;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.GetEvents;
import org.rpgl.subevent.GetObjectTags;
import org.rpgl.subevent.HealingDelivery;
import org.rpgl.subevent.InfoSubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointsDelivery;
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
        return super.getJsonObject(RPGLObjectTO.ABILITY_SCORES_ALIAS);
    }

    /**
     * Setter for ability scores.
     *
     * @param abilityScores a new ability scores JsonObject
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setAbilityScores(JsonObject abilityScores) {
        super.putJsonObject(RPGLObjectTO.ABILITY_SCORES_ALIAS, abilityScores);
        return this;
    }

    /**
     * Returns the RPGLObject's health data. This includes hit dice, base health, max health, current health, and
     * temporary health.
     *
     * @return a JsonObject containing health data for the RPGLObject
     */
    public JsonObject getHealthData() {
        return super.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS);
    }

    /**
     * Setter for health data.
     *
     * @param healthData a new health data JsonObject
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setHealthData(JsonObject healthData) {
        super.putJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS, healthData);
        return this;
    }

    /**
     * Returns all items currently equipped to the RPGLObject, mapped to their corresponding equipment slots.
     *
     * @return a JsonObject containing RPGLItems and their equipment slots
     */
    public JsonObject getEquippedItems() {
        return super.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
    }

    /**
     * Setter for equipped items.
     *
     * @param equippedItems a new equipped items JsonObject
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setEquippedItems(JsonObject equippedItems) {
        super.putJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, equippedItems);
        return this;
    }

    /**
     * Returns the UUIDs of all items held by the RPGLObject.
     *
     * @return a JsonArray of RPGLItem UUIDs
     */
    public JsonArray getInventory() {
        return super.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
    }

    /**
     * Setter for inventory.
     *
     * @param inventory a new inventory JsonArray
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setInventory(JsonArray inventory) {
        super.putJsonArray(RPGLObjectTO.INVENTORY_ALIAS, inventory);
        return this;
    }

    /**
     * Returns the IDs (not UUIDs) of all RPGLEvents innately provided to the RPGLObject.
     *
     * @return a JsonArray of RPGLEffect IDs
     */
    public JsonArray getEvents() {
        return super.getJsonArray(RPGLObjectTO.EVENTS_ALIAS);
    }

    /**
     * Setter for events.
     *
     * @param events a new events JsonArray
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setEvents(JsonArray events) {
        super.putJsonArray(RPGLObjectTO.EVENTS_ALIAS, events);
        return this;
    }

    /**
     * Returns the UUIDs of all RPGLEffects applied to the RPGLObject, not including any provided through equipped items.
     *
     * @return a JsonArray of RPGLItem UUIDs
     */
    public JsonArray getEffects() {
        return super.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS);
    }

    /**
     * Setter for effects.
     *
     * @param effects a new effects JsonArray
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setEffects(JsonArray effects) {
        super.putJsonArray(RPGLObjectTO.EFFECTS_ALIAS, effects);
        return this;
    }

    /**
     * Returns the base proficiency bonus of the RPGLObject, not modified by any effects.
     *
     * @return the RPGLObject's base proficiency bonus
     */
    public Integer getProficiencyBonus() {
        return super.getInteger(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS);
    }

    /**
     * Setter for proficiency bonus.
     *
     * @param proficiencyBonus a new proficiency bonus int
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setProficiencyBonus(int proficiencyBonus) {
        super.putInteger(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        return this;
    }

    /**
     * Returns the resources UUID array of the RPGLObject. This will include any RPGLResource UUIDs granted temporarily.
     *
     * @return the RPGLObject's resources array
     */
    public JsonArray getResources() {
        return super.getJsonArray(RPGLObjectTO.RESOURCES_ALIAS);
    }

    /**
     * Setter for resources.
     *
     * @param resources a new resources UUID array
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setResources(JsonArray resources) {
        super.putJsonArray(RPGLObjectTO.RESOURCES_ALIAS, resources);
        return this;
    }

    /**
     * Getter for classes.
     *
     * @return an array of class and level data
     */
    public JsonArray getClasses() {
        return super.getJsonArray(RPGLObjectTO.CLASSES_ALIAS);
    }

    /**
     * Setter for classes.
     *
     * @param classes a new array of class and level data
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setClasses(JsonArray classes) {
        super.putJsonArray(RPGLObjectTO.CLASSES_ALIAS, classes);
        return this;
    }

    /**
     * Getter for races.
     *
     * @return an array of race IDs
     */
    public JsonArray getRaces() {
        return super.getJsonArray(RPGLObjectTO.RACES_ALIAS);
    }

    /**
     * Setter for races.
     *
     * @param races a new array of race IDs
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setRaces(JsonArray races) {
        super.putJsonArray(RPGLObjectTO.RACES_ALIAS, races);
        return this;
    }

    /**
     * Getter for challenge rating.
     *
     * @return the object's challenge rating
     */
    public Double getChallengeRating() {
        return super.getDouble(RPGLObjectTO.CHALLENGE_RATING_ALIAS);
    }

    /**
     * Setter for challenge rating.
     *
     * @param challengeRating a new challenge rating
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setChallengeRating(double challengeRating) {
        super.putDouble(RPGLObjectTO.CHALLENGE_RATING_ALIAS, challengeRating);
        return this;
    }

    /**
     * Getter for the user id (this is the user id for the user which controls this object).
     *
     * @return a user id
     */
    public String getUserId() {
        return super.getString(RPGLObjectTO.USER_ID);
    }

    /**
     * Setter for the user id.
     *
     * @param userId a new user id (this is the user id for the new user which will control this object)
     * @return this RPGLObject
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setUserId(String userId) {
        super.putString(RPGLObjectTO.USER_ID, userId);
        return this;
    }

    /**
     * Getter for origin object.
     *
     * @return the object's origin object id
     */
    public String getOriginObject() {
        return super.getString(RPGLObjectTO.ORIGIN_OBJECT_ALIAS);
    }

    /**
     * Setter for origin object.
     *
     * @param originObject a new origin object id
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setOriginObject(String originObject) {
        super.putString(RPGLObjectTO.ORIGIN_OBJECT_ALIAS, originObject);
        return this;
    }

    /**
     * Getter for proxy.
     *
     * @return true if the object is a proxy for a different object, or false if it is not
     */
    public Boolean getProxy() {
        return super.getBoolean(RPGLObjectTO.PROXY_ALIAS);
    }

    /**
     * Setter for proxy.
     *
     * @param proxy a new proxy value
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setProxy(Boolean proxy) {
        super.putBoolean(RPGLObjectTO.PROXY_ALIAS, proxy);
        return this;
    }

    /**
     * Getter for position.
     *
     * @return the object's position array
     */
    public JsonArray getPosition() {
        return super.getJsonArray(RPGLObjectTO.POSITION_ALIAS);
    }

    /**
     * Setter for position.
     *
     * @param position a new position array
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setPosition(JsonArray position) {
        super.putJsonArray(RPGLObjectTO.POSITION_ALIAS, position);
        return this;
    }

    /**
     * Getter for rotation.
     *
     * @return the object's rotation array
     */
    public JsonArray getRotation() {
        return super.getJsonArray(RPGLObjectTO.ROTATION_ALIAS);
    }

    /**
     * Setter for rotation.
     *
     * @param rotation a new rotation array
     */
    @SuppressWarnings("UnusedReturnValue")
    public RPGLObject setRotation(JsonArray rotation) {
        super.putJsonArray(RPGLObjectTO.ROTATION_ALIAS, rotation);
        return this;
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
        getEvents.prepare(context, this.getPosition());
        getEvents.setTarget(this);
        getEvents.invoke(context, this.getPosition());
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

        // add proxy resources if applicable
        String originObjectUuid = this.getOriginObject();
        if (this.getProxy() && originObjectUuid != null) {
            RPGLObject originObject = UUIDTable.getObject(originObjectUuid);
            resources.addAll(originObject.getResourceObjects());
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
    public void invokeEvent(JsonArray originPoint, RPGLObject[] targets, RPGLEvent event, List<RPGLResource> resources, RPGLContext context) throws Exception {
        for (RPGLResource resource : resources) {
            resource.exhaust();
        }
        event.scale(resources);

        RPGLObject source;
        if (event.getString("source") != null) {
            // events with a source pre-assigned via AddEvent take priority
            source = UUIDTable.getObject(event.getString("source"));
        } else if (this.getProxy()) {
            // proxy objects set their origin object as the source for any events they invoke
            source = UUIDTable.getObject(this.getOriginObject());
        } else {
            // ordinary event invocation sets the calling object as the source
            source = this;
        }

        JsonArray subeventJsonArray = event.getJsonArray("subevents");
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
            String subeventId = subeventJson.getString("subevent");
            Subevent subevent = Subevent.SUBEVENTS.get(subeventId).clone(subeventJson);
            subevent.setSource(source);
            subevent.setOriginItem(event.getOriginItem());
            subevent.prepare(context, originPoint);
            for (RPGLObject target : targets) {
                Subevent subeventClone = subevent.clone();
                subeventClone.setTarget(target);
                subeventClone.invoke(context, originPoint);
            }
        }
    }

    /**
     * This method presents a Subevent to the RPGLObject's RPGLEffects in order to influence the result of the Subevent.
     *
     * @param subevent a Subevent being invoked
     * @param context the context in which the Subevent is being processed
     * @param originPoint the point from which the passed subevent emanates
     * @return true if one of the RPGLObject's RPGLEffects modified the passed Subevent
     *
     * @throws Exception if an exception occurs
     */
    public boolean processSubevent(Subevent subevent, RPGLContext context, JsonArray originPoint) throws Exception {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffectObjects()) {
            wasSubeventProcessed |= effect.processSubevent(subevent, context, originPoint);
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
    public int getEffectiveProficiencyBonus(RPGLContext context, JsonArray originPoint) throws Exception {
        CalculateProficiencyBonus calculateProficiencyBonus = new CalculateProficiencyBonus();
        calculateProficiencyBonus.setSource(this);
        calculateProficiencyBonus.prepare(context, originPoint);
        calculateProficiencyBonus.setTarget(this);
        calculateProficiencyBonus.invoke(context, originPoint);
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
        calculateAbilityScore.prepare(context, this.getPosition());
        calculateAbilityScore.setTarget(this);
        calculateAbilityScore.invoke(context, this.getPosition());
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
     *
     * @throws Exception if an exception occurs.
     */
    public void receiveDamage(DamageDelivery damageDelivery, RPGLContext context) throws Exception {
        JsonObject damageJson = damageDelivery.getDamage();
        int damage = 0;
        for (Map.Entry<String, ?> entry : damageJson.asMap().entrySet()) {
            damage += damageJson.getInteger(entry.getKey());
        }
        this.reduceHitPoints(damage, context);
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
     * @param temporaryHitPointsDelivery a TemporaryHitPointsDelivery Subevent containing a quantity of temporary hit
     *                                   points to apply to the RPGLObject
     * @param riderEffects a list of effects to be applied if the temporary hit points from temporaryHitPointsDelivery
     *                     are applied
     */
    public void receiveTemporaryHitPoints(TemporaryHitPointsDelivery temporaryHitPointsDelivery, JsonArray riderEffects) {
        JsonObject healthData = this.getHealthData();
        if (healthData.getInteger("temporary") < temporaryHitPointsDelivery.getTemporaryHitPoints()) {
            healthData.putInteger("temporary", temporaryHitPointsDelivery.getTemporaryHitPoints());
            for (int i = 0; i < riderEffects.size(); i++) {
                RPGLEffect effect = RPGLFactory.newEffect(riderEffects.getString(i));
                effect.setSource(temporaryHitPointsDelivery.getSource());
                effect.setTarget(temporaryHitPointsDelivery.getTarget());
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
        calculateMaximumHitPoints.prepare(context, this.getPosition());
        calculateMaximumHitPoints.setTarget(this);
        calculateMaximumHitPoints.invoke(context, this.getPosition());
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
        infoSubevent.prepare(context, this.getPosition());
        infoSubevent.setTarget(this);
        infoSubevent.invoke(context, this.getPosition());
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
        calculateBaseArmorClass.prepare(context, this.getPosition());
        calculateBaseArmorClass.setTarget(this);
        calculateBaseArmorClass.invoke(context, this.getPosition());
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
    public ArrayList<String> getAllTags(RPGLContext context, JsonArray originPoint) throws Exception {
        ArrayList<String> tagsList = new ArrayList<>();
        JsonArray tags;

        tags = super.getTags();
        for (int i = 0; i < tags.size(); i++) {
            tagsList.add(tags.getString(i));
        }

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.setSource(this);
        getObjectTags.prepare(context, originPoint);
        getObjectTags.setTarget(this);
        getObjectTags.invoke(context, originPoint);

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
        abilityCheck.prepare(context, this.getPosition());
        abilityCheck.setTarget(this);
        abilityCheck.invoke(context, this.getPosition());
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
