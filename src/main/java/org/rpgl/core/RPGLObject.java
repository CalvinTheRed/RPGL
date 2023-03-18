package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
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
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        eventsArray = this.getEvents();
        for (int i = 0; i < eventsArray.size(); i++) {
            events.add(RPGLFactory.newEvent(eventsArray.getString(i)));
        }

        GetEvents getEvents = new GetEvents();
        getEvents.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_events");
        }});
        getEvents.setSource(this);
        getEvents.prepare(context);
        getEvents.setTarget(this);
        getEvents.invoke(context);

        eventsArray = getEvents.getEvents();
        for (int i = 0; i < eventsArray.size(); i++) {
            events.add(RPGLFactory.newEvent(eventsArray.getString(i)));
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
            effects.addAll(equippedItem.getWhileEquippedEffectObjects());
        }

        return effects;
    }

    /**
     * This method precipitates the process of invoking an RPGLEvent.
     *
     * @param targets an array of RPGLObjects targeted by the RPGLEvent being invoked
     * @param event   the RPGLEvent being invoked
     * @param context the RPGLContext in which the RPGLEvent is invoked
     *
     * @throws Exception if an exception occurs.
     */
    public void invokeEvent(RPGLObject[] targets, RPGLEvent event, RPGLContext context) throws Exception {
        // assume that any necessary resources have already been spent
        JsonArray subeventJsonArray = event.getJsonArray("subevents");
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
            String subeventId = subeventJson.getString("subevent");
            Subevent subevent = Subevent.SUBEVENTS.get(subeventId).clone(subeventJson);
            subevent.setSource(this);
            subevent.prepare(context);
            for (RPGLObject target : targets) {
                Subevent targetClone = subevent.clone();
                targetClone.setTarget(target);
                targetClone.invoke(context);
            }
        }
    }

    /**
     * This method presents a Subevent to the RPGLObject's RPGLEffects in order to influence the result of the Subevent.
     *
     * @param subevent a Subevent being invoked
     * @param context the context in which the Subevent is being processed
     * @return true if one of the RPGLObject's RPGLEffects modified the passed Subevent
     *
     * @throws Exception if an exception occurs
     */
    public boolean processSubevent(Subevent subevent, RPGLContext context) throws Exception {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffectObjects()) {
            wasSubeventProcessed |= effect.processSubevent(subevent, context);
        }
        return wasSubeventProcessed;
    }

    /**
     * This method adds an RPGLEffect to the RPGLObject.
     * <br>
     * TODO should effects be restricted if they are the same? double-dipping
     *
     * @param effect a RPGLEffect to be assigned to the RPGLObject
     */
    public void addEffect(RPGLEffect effect) {
        this.getEffects().addString(effect.getUuid());
    }

    /**
     * This method adds an RPGLEffect to the RPGLObject.
     *
     * @param effectUuid the UUID for a RPGLEffect to be removed from the RPGLObject
     * @return true if the RPGLObject's RPGLEffect collection contained the specified element
     */
    public boolean removeEffect(String effectUuid) {
        return this.getEffects().asList().remove(effectUuid);
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
        calculateProficiencyBonus.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_proficiency_bonus");
        }});
        calculateProficiencyBonus.setSource(this);
        calculateProficiencyBonus.prepare(context);
        calculateProficiencyBonus.setTarget(this);
        calculateProficiencyBonus.invoke(context);
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
            this.putString("subevent", "calculate_ability_score");
            this.putString("ability", ability);
        }});
        calculateAbilityScore.setSource(this);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.setTarget(this);
        calculateAbilityScore.invoke(context);
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
     * @param context        the RPGLContext in which the RPGLObject takes damage
     *
     * @throws Exception if an exception occurs.
     */
    public void receiveDamage(DamageDelivery damageDelivery, RPGLContext context) throws Exception {
        JsonObject damageJson = damageDelivery.getDamage();
        int damage = 0;
        for (Map.Entry<String, ?> damageJsonEntry : damageJson.asMap().entrySet()) {
            String damageType = damageJsonEntry.getKey();
            Integer typedDamage = damageJson.getInteger(damageJsonEntry.getKey());

            // TODO make DamageAffinity contain a list of all present damage types...

            DamageAffinity damageAffinity = new DamageAffinity();
            damageAffinity.joinSubeventData(new JsonObject() {{
                this.putString("subevent", "damage_affinity");
                this.putString("damage_type", damageType);
            }});
            damageAffinity.setSource(damageDelivery.getSource());
            damageAffinity.prepare(context);
            damageAffinity.setTarget(this);
            damageAffinity.invoke(context);

            if (!damageAffinity.isImmune()) {
                if (damageAffinity.isResistant()) {
                    typedDamage /= 2;
                }
                if (damageAffinity.isVulnerable()) {
                    typedDamage *= 2;
                }
                if (typedDamage > 0) {
                    damage += typedDamage;
                }
            }
        }
        if (damage > 0) {
            this.reduceHitPoints(damage, context);
        }
    }

    /**
     * This method accepts a HealingDelivery Subevent to provide healing to the RPGLObject. This method cannot be used
     * to heal the object beyond its hit point maximum.
     *
     * @param healingDelivery a HealingDelivery Subevent containing a quantity of healing to apply to the RPGLObject
     * @param context         the context in which the RPGLObject is receiving healing.
     *
     * @throws Exception if an exception occurs
     */
    public void receiveHealing(HealingDelivery healingDelivery, RPGLContext context) throws Exception {
        System.out.println(healingDelivery);
        JsonObject healthData = this.getHealthData();
        healthData.putInteger("current", healthData.getInteger("current") + healingDelivery.getHealing());
        int maximumHitPoints = this.getMaximumHitPoints(context);
        if (healthData.getInteger("current") > maximumHitPoints) {
            healthData.putInteger("current", maximumHitPoints);
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
        calculateMaximumHitPoints.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_maximum_hit_points");
        }});
        calculateMaximumHitPoints.setSource(this);
        calculateMaximumHitPoints.prepare(context);
        calculateMaximumHitPoints.setTarget(this);
        calculateMaximumHitPoints.invoke(context);
        return calculateMaximumHitPoints.get();
    }

    /**
     * This helper method directly reduces the hit points of the RPGLObject. This is not intended to be called directly.
     *
     * @param amount  a quantity of damage
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
                this.invokeInfoSubevent(new String[] { "reduced_to_zero_temporary_hit_points" }, context);
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
            this.invokeInfoSubevent(new String[] { "reduced_to_zero_hit_points", "killed" }, context); // TODO is there a more elegant way to do this?
        } else if (currentHitPoints < 0) {
            healthData.put("current", 0);
            this.invokeInfoSubevent(new String[] { "reduced_to_zero_hit_points" }, context);
        }
    }

    /**
     * This helper method causes the RPGLObject to invoke an InfoSubevent using the passed tags.
     *
     * @param tags    the tags to be stored in the InfoSubevent
     * @param context the context in which the InfoSubevent is invoked
     * @return the InfoSubevent which was invoked
     *
     * @throws Exception if nan exception occurs
     */
    InfoSubevent invokeInfoSubevent(String[] tags, RPGLContext context) throws Exception {
        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "info_subevent",
                "tags": [ <tags> ]
            }*/
            this.putString("subevent", "info_subevent");
            JsonArray subeventTags = infoSubevent.json.getJsonArray("tags");
            for (String tag : tags) {
                subeventTags.addString(tag);
            }
        }});
        infoSubevent.setSource(this);
        infoSubevent.prepare(context);
        infoSubevent.setTarget(this);
        infoSubevent.invoke(context);
        return infoSubevent;
    }

    /**
     * Precipitates an InfoSubevent indicating that the RPGLObject's turn has started.
     *
     * @param context the context in which the RPGLObject starts its turn
     *
     * @throws Exception if an exception occurs
     */
    public void startTurn(RPGLContext context) throws Exception {
        this.invokeInfoSubevent(new String[] { "starting_turn" }, context);
    }

    /**
     * Precipitates an InfoSubevent indicating that the RPGLObject's turn has ended.
     *
     * @param context the context in which the RPGLObject ends its turn
     *
     * @throws Exception if an exception occurs
     */
    public void endTurn(RPGLContext context) throws Exception {
        this.invokeInfoSubevent(new String[] { "ending_turn" }, context);
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
        calculateBaseArmorClass.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_base_armor_class");
        }});
        calculateBaseArmorClass.setSource(this);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.setTarget(this);
        calculateBaseArmorClass.invoke(context);
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
     * @param itemUuid      a RPGLItem's UUID String
     * @param equipmentSlot an equipment slot name (can be anything other than <code>"inventory"</code>)
     */
    public void equipItem(String itemUuid, String equipmentSlot) {
        JsonArray inventory = this.getInventory();
        if (inventory.asList().contains(itemUuid)) {
            JsonObject equippedItems = this.getEquippedItems();
            equippedItems.putString(equipmentSlot, itemUuid);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            item.updateEquippedEffects(this);
            item.defaultAttackAbilities();
            // TODO account for 2-handed items...
        }
    }

    /**
     * Un-equips an RPGLItem from an inventory slot, unless an InfoSubevent created to announce this is canceled.
     *
     * @param equipmentSlot an equipment slot String
     * @param force         if true, the RPGLItem will be removed regardless of whether the associated InfoSubevent is
     *                      canceled
     * @param context       the context in which the RPGLItem is being un-equipped
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
        if (force || this.invokeInfoSubevent(infoSubeventTags, context).isNotCanceled()) {
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
        getObjectTags.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_object_tags");
        }});
        getObjectTags.setSource(this);
        getObjectTags.prepare(context);
        getObjectTags.setTarget(this);
        getObjectTags.invoke(context);

        tags = getObjectTags.getObjectTags();
        for (int i = 0; i < tags.size(); i++) {
            tagsList.add(tags.getString(i));
        }

        return tagsList;
    }

}
