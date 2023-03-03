package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.CalculateAbilityScoreSubevent;
import org.rpgl.subevent.CalculateBaseArmorClass;
import org.rpgl.subevent.CalculateProficiencyBonus;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.GetObjectTags;
import org.rpgl.subevent.GetSavingThrowProficiency;
import org.rpgl.subevent.GetWeaponProficiency;
import org.rpgl.subevent.HealingDelivery;
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
     * Returns the RPGLObject's health data. This includes hit dice, base health, max health, current health, and
     * temporary health.
     *
     * @return a JsonObject containing health data for the RPGLObject
     */
    public JsonObject getHealthData() {
        return this.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS);
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
     * Returns the UUIDs of all items held by the RPGLObject.
     *
     * @return a JsonArray of RPGLItem UUIDs
     */
    public JsonArray getInventory() {
        return this.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
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
     * Returns the UUIDs of all RPGLEffects applied to the RPGLObject, not including any provided through equipped items.
     *
     * @return a JsonArray of RPGLItem UUIDs
     */
    public JsonArray getEffects() {
        return this.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS);
    }

    /**
     * Returns the base proficiency bonus of the RPGLObject, not modified by any effects.
     *
     * @return the RPGLObject's base proficiency bonus
     */
    public Integer getProficiencyBonus() {
        return this.getInteger(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * Returns a List of all RPGLEvent objects associated with the RPGLObject. This includes RPGLEvents granted by
     * effects.
     *
     * @return a List of RPGLEvent objects
     */
    public List<RPGLEvent> getEventObjects() {
        // TODO make a Subevent for collecting additional RPGLEvent ID's
        JsonArray eventsArray = this.getEvents();
        List<RPGLEvent> events = new ArrayList<>();
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
     * @return true if one of the RPGLObject's RPGLEffects modified the passed Subevent
     *
     * @throws ConditionMismatchException if one of the Conditions in an RPGLEffect belonging to the RPGLObject is
     *         presented with the wrong Condition ID.
     * @throws FunctionMismatchException if one of the Functions in an RPGLEffect belonging to the RPGLObject is
     *         presented with the wrong Function ID.
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

    public int getAbilityScoreFromAbilityName(RPGLContext context, String ability) throws Exception {
        CalculateAbilityScoreSubevent calculateAbilityScore = new CalculateAbilityScoreSubevent();
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
     * @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     * @param ability the ability score whose modifier will be determined
     * @return the modifier of the target ability
     *
     * @throws Exception if an exception occurs.
     */
    public int getAbilityModifierFromAbilityName(RPGLContext context, String ability) throws Exception {
        return getAbilityModifierFromAbilityScore(this.getAbilityScoreFromAbilityName(context, ability));
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
     * This method determines whether the RPGLObject is proficient in saving throws for a specified ability.
     *
     * @param context     the RPGLContext in which the RPGLObject's save proficiency is determined
     * @param saveAbility the ability score used by the saving throw
     * @return true if the RPGLObject is proficient in saving throws with the specified ability
     *
     * @throws Exception if an exception occurs.
     */
    public boolean isProficientInSavingThrow(RPGLContext context, String saveAbility) throws Exception {
        GetSavingThrowProficiency getSavingThrowProficiency = new GetSavingThrowProficiency();
        getSavingThrowProficiency.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_saving_throw_proficiency");
            this.putString("save_ability", saveAbility);
        }});
        getSavingThrowProficiency.setSource(this);
        getSavingThrowProficiency.prepare(context);
        getSavingThrowProficiency.setTarget(this);
        getSavingThrowProficiency.invoke(context);
        return getSavingThrowProficiency.isProficient();
    }

    /**
     * This method determines whether the RPGLObject is proficient in attacks made using a specified weapon.
     *
     * @param context  the RPGLContext in which the RPGLObject's weapon proficiency is determined
     * @param item     an RPGLItem
     * @return true if the RPGLObject is proficient with the item corresponding to the passed UUID
     *
     * @throws Exception if an exception occurs.
     */
    public boolean isProficientWithWeapon(RPGLContext context, RPGLItem item) throws Exception {
        GetWeaponProficiency getWeaponProficiency = new GetWeaponProficiency();
        getWeaponProficiency.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_weapon_proficiency");
            this.putJsonArray("tags", item.getProficiencyTags().deepClone());
        }});
        getWeaponProficiency.setSource(this);
        getWeaponProficiency.prepare(context);
        getWeaponProficiency.setTarget(this);
        getWeaponProficiency.invoke(context);
        return getWeaponProficiency.isProficient();
    }

    /**
     * This method is how a RPGLObject is intended to take damage.
     *
     * @param context        the RPGLContext in which the RPGLObject takes damage
     * @param damageDelivery a DamageDelivery object containing damage data
     *
     * @throws Exception if an exception occurs.
     */
    public void receiveDamage(RPGLContext context, DamageDelivery damageDelivery) throws Exception {
        JsonObject damageJson = damageDelivery.getDamage();
        Integer damage = 0;
        for (Map.Entry<String, ?> damageJsonEntry : damageJson.asMap().entrySet()) {
            String damageType = damageJsonEntry.getKey();
            Integer typedDamage = damageJson.getInteger(damageJsonEntry.getKey());

            DamageAffinity damageAffinity = new DamageAffinity();
            damageAffinity.joinSubeventData(new JsonObject() {{
                this.putString("subevent", "damage_affinity");
                this.putString("type", damageType);
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
                damage += typedDamage;
            }
        }
        if (damage > 0) {
            this.reduceHitPoints(damage);
        }
    }

    public void receiveHealing(HealingDelivery healingDelivery) {
        JsonObject healthData = this.getHealthData();
        healthData.putInteger("current", healthData.getInteger("current") + healingDelivery.getHealing());
        if (healthData.getInteger("current") > healthData.getInteger("maximum")) {
            // TODO subevent for determining maximum health goes above...
            healthData.putInteger("current", healthData.getInteger("maximum"));
        }
    }

    /**
     * This helper method directly reduces the hit points of the RPGLObject. This is not intended to be called directly.
     *
     * @param amount a quantity of damage
     */
    void reduceHitPoints(int amount) {
        Map<String, Object> healthData = this.getHealthData().asMap();
        Integer temporaryHitPoints = (Integer) healthData.get("temporary");
        Integer currentHitPoints = (Integer) healthData.get("current");
        if (amount > temporaryHitPoints) {
            amount -= temporaryHitPoints;
            temporaryHitPoints = 0;
            currentHitPoints -= amount;
        } else {
            temporaryHitPoints -= amount;
        }
        healthData.put("temporary", temporaryHitPoints);
        healthData.put("current", currentHitPoints);
        // TODO deal with 0 or negative hit points after this...
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
        // TODO make a subevent for equipping an item
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

    public ArrayList<String> getAllTags(RPGLContext context) throws Exception {
        JsonArray templateTags = this.getTags();
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < templateTags.size(); i++) {
            tags.add(templateTags.getString(i));
        }

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_object_tags");
        }});
        getObjectTags.setSource(this);
        getObjectTags.prepare(context);
        getObjectTags.setTarget(this);
        getObjectTags.invoke(context);
        JsonArray extraTags = getObjectTags.getTags();
        for (int i = 0; i < extraTags.size(); i++) {
            tags.add(extraTags.getString(i));
        }

        return tags;
    }
}
