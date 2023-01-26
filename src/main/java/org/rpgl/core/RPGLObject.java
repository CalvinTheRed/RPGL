package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.subevent.*;
import org.rpgl.uuidtable.UUIDTable;
import org.rpgl.uuidtable.UUIDTableElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * RPGLObjects are objects which represent anything which might be placed on a game board (not including the game board
 * or terrain itself).
 *
 * @author Calvin Withun
 */
public class RPGLObject extends UUIDTableElement {

    /**
     * A copy-constructor for the RPGLObject class.
     *
     *  @param data the data to be copied to this object
     */
    RPGLObject(JsonObject data) {
        this.join(data);
    }

    /**
     * 	<p>
     * 	<b><i>invokeEvent</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void invokeEvent(RPGLObject[] targets, RPGLEvent event, RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method precipitates the process of invoking an RPGLEvent.
     * 	</p>
     *
     * 	@param targets an array of RPGLObjects targeted by the RPGLEvent being invoked
     *  @param event   the RPGLEvent being invoked
     *  @param context the RPGLContext in which the RPGLEvent is invoked
     *
     * 	@throws Exception if an exception occurs.
     */
    public void invokeEvent(RPGLObject[] targets, RPGLEvent event, RPGLContext context) throws Exception {
        // assume that any necessary resources have already been spent
        JsonArray subeventJsonArray = (JsonArray) event.get("subevents");
        for (Object subeventJsonElement : subeventJsonArray) {
            JsonObject subeventJson = (JsonObject) subeventJsonElement;
            String subeventId = (String) subeventJson.get("subevent");
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
     * 	<p>
     * 	<b><i>processSubevent</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean processSubevent(Subevent subevent)
     * 	throws ConditionMismatchException, FunctionMismatchException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method presents a Subevent to the RPGLObject's RPGLEffects in order to influence the result of the Subevent.
     * 	</p>
     *
     * 	@param subevent a Subevent being invoked
     *  @return true if one of the RPGLObject's RPGLEffects modified the passed Subevent
     *
     * 	@throws ConditionMismatchException if one of the Conditions in an RPGLEffect belonging to the RPGLObject is
     * 	presented with the wrong Condition ID.
     *  @throws FunctionMismatchException if one of the Functions in an RPGLEffect belonging to the RPGLObject is
     *  presented with the wrong Function ID.
     */
    public boolean processSubevent(Subevent subevent) throws ConditionMismatchException, FunctionMismatchException {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffects()) {
            wasSubeventProcessed |= effect.processSubevent(subevent);
        }
        return wasSubeventProcessed;
    }

    /**
     * 	<p>
     * 	<b><i>addEffect</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean addEffect(RPGLEffect effect)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds an RPGLEffect to the RPGLObject.
     * 	</p>
     *
     * 	TODO should effects be restricted if they are the same? double-dipping
     *
     * 	@param effect a RPGLEffect to be assigned to the RPGLObject
     *  @return true if the RPOGLObject's RPGLEffect collection changed as a result of the call
     */
    public boolean addEffect(RPGLEffect effect) {
        JsonArray effects = (JsonArray) this.get("effects");
        return effects.add(effect.getUuid());
    }

    /**
     * 	<p>
     * 	<b><i>removeEffect</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean removeEffect(RPGLEffect effect)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds an RPGLEffect to the RPGLObject.
     * 	</p>
     *
     * 	@param effect a RPGLEffect to be assigned to the RPGLObject
     *  @return true if the RPGLObject's RPGLEffect collection contained the specified element
     */
    public boolean removeEffect(RPGLEffect effect) {
        JsonArray effects = (JsonArray) this.get("effects");
        return effects.remove(effect.getUuid());
    }

    /**
     * 	<p>
     * 	<b><i>getEffects</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEffect[] getEffects()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns an array of all RPGLEffect objects assigned to the RPGLObject (including any granted from
     * 	equipped items).
     * 	</p>
     *
     *  @return an array of RPGLEffect objects
     */
    public RPGLEffect[] getEffects() {
        ArrayList<RPGLEffect> effectsList = new ArrayList<>();

        // Add RPGLEffects from object
        JsonArray objectEffectUuids = (JsonArray) this.get("effects");
        for (Object effectUuid : objectEffectUuids) {
            effectsList.add(UUIDTable.getEffect((String) effectUuid));
        }

        // Add RPGLEffects from equipped items
        JsonObject items = (JsonObject) this.get("items");
        for (Map.Entry<String, Object> itemsEntrySet : items.entrySet()) {
            String key = itemsEntrySet.getKey();
            if (!"inventory".equals(key)) {
                String itemUuid = (String) itemsEntrySet.getValue();
                Collections.addAll(effectsList, UUIDTable.getItem(itemUuid).getEquippedEffects());
            }
        }

        // Return list as array
        return effectsList.toArray(new RPGLEffect[0]);
    }

    /**
     * 	<p>
     * 	<b><i>getEvents</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String[] getEvents()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns an array of RPGLEvent ID's which this RPGLObject has been granted access to.
     * 	</p>
     *
     *  @return an array of RPGLEvent ID Strings
     */
    public String[] getEvents() {
        // TODO make a Subevent for collecting additional RPGLEvent ID's
        JsonArray eventsArray = (JsonArray) this.get("events");
        return eventsArray.toArray(new String[0]); // Ignore this warning, only Strings should be stored in eventsArray.
    }

    /**
     * 	<p>
     * 	<b><i>getProficiencyBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Long getProficiencyBonus(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the proficiency bonus of the RPGLObject.
     * 	</p>
     *
     *  @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     *
     * 	@throws Exception if an exception occurs.
     */
    public Long getProficiencyBonus(RPGLContext context) throws Exception {
        CalculateProficiencyModifier calculateProficiencyModifier = new CalculateProficiencyModifier();
        String calculateProficiencyModifierJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject calculateProficiencyModifierJson = JsonParser.parseObjectString(calculateProficiencyModifierJsonString);
        calculateProficiencyModifier.joinSubeventJson(calculateProficiencyModifierJson);
        calculateProficiencyModifier.setSource(this);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.setTarget(this);
        calculateProficiencyModifier.invoke(context);
        return calculateProficiencyModifier.get();
    }

    /**
     * 	<p>
     * 	<b><i>getAbilityModifierFromAbilityScore</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public long getAbilityModifierFromAbilityScore(RPGLContext context, String ability)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the RPGLObject's ability score modifier for a specified ability score.
     * 	</p>
     *
     *  @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     *  @param ability the ability score whose modifier will be determined
     *
     * 	@throws Exception if an exception occurs.
     */
    public long getAbilityModifierFromAbilityScore(RPGLContext context, String ability) throws Exception {
        CalculateAbilityScore calculateAbilityScore = new CalculateAbilityScore();
        String calculateAbilityScoreJsonString = String.format("""
                        {
                            "subevent": "calculate_ability_score",
                            "ability": "%s"
                        }
                        """,
                ability
        );
        JsonObject calculateAbilityScoreJson = JsonParser.parseObjectString(calculateAbilityScoreJsonString);
        calculateAbilityScore.joinSubeventJson(calculateAbilityScoreJson);
        calculateAbilityScore.setSource(this);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.setTarget(this);
        calculateAbilityScore.invoke(context);
        return getAbilityModifierFromAbilityScore(calculateAbilityScore.get());
    }

    /**
     * 	<p>
     * 	<b><i>getAbilityModifierFromAbilityScore</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public long getAbilityModifierFromAbilityScore(long abilityScore)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the modifier for an ability score.
     * 	</p>
     *
     *  @param abilityScore an ability score number
     */
    static long getAbilityModifierFromAbilityScore(long abilityScore) {
        if (abilityScore < 10L) {
            // integer division rounds toward zero, so abilityScore must be
            // adjusted to calculate the correct values for negative modifiers
            abilityScore --;
        }
        return (abilityScore - 10L) / 2L;
    }

    /**
     * 	<p>
     * 	<b><i>isProficientInSavingThrow</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isProficientInSavingThrow(RPGLContext context, String saveAbility)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines whether the RPGLObject is proficient in saving throws for a specified ability.
     * 	</p>
     *
     *  @param context     the RPGLContext in which the RPGLObject's save proficiency is determined
     *  @param saveAbility the ability score used by the saving throw
     *  @return true if the RPGLObject is proficient in saving throws with the specified ability
     *
     * 	@throws Exception if an exception occurs.
     */
    public boolean isProficientInSavingThrow(RPGLContext context, String saveAbility) throws Exception {
        GetSavingThrowProficiency getSavingThrowProficiency = new GetSavingThrowProficiency();
        String getSaveProficiencyJsonString = String.format("""
                        {
                            "subevent": "get_saving_throw_proficiency",
                            "save_ability": "%s"
                        }
                        """,
                saveAbility
        );
        JsonObject getSaveProficiencyJson = JsonParser.parseObjectString(getSaveProficiencyJsonString);
        getSavingThrowProficiency.joinSubeventJson(getSaveProficiencyJson);
        getSavingThrowProficiency.setSource(this);
        getSavingThrowProficiency.prepare(context);
        getSavingThrowProficiency.setTarget(this);
        getSavingThrowProficiency.invoke(context);
        return getSavingThrowProficiency.getIsProficient();
    }

    /**
     * 	<p>
     * 	<b><i>isProficientWithWeapon</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isProficientWithWeapon(RPGLContext context, String itemUuid)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines whether the RPGLObject is proficient in attacks made using a specified weapon.
     * 	</p>
     *
     *  @param context  the RPGLContext in which the RPGLObject's weapon proficiency is determined
     *  @param itemUuid the UUID of a RPGLItem object
     *
     * 	@throws Exception if an exception occurs.
     */
    public boolean isProficientWithWeapon(RPGLContext context, String itemUuid) throws Exception {
        GetWeaponProficiency getWeaponProficiency = new GetWeaponProficiency();
        String getWeaponProficiencyJsonString = String.format("""
                        {
                            "subevent": "get_weapon_proficiency",
                            "item": "%s"
                        }
                        """,
                itemUuid
        );
        JsonObject getSaveProficiencyJson = JsonParser.parseObjectString(getWeaponProficiencyJsonString);
        getWeaponProficiency.joinSubeventJson(getSaveProficiencyJson);
        getWeaponProficiency.setSource(this);
        getWeaponProficiency.prepare(context);
        getWeaponProficiency.setTarget(this);
        getWeaponProficiency.invoke(context);
        return getWeaponProficiency.getIsProficient();
    }

    /**
     * 	<p>
     * 	<b><i>receiveDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void receiveDamage(RPGLContext context, DamageDelivery damageDelivery)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method is how a RPGLObject is intended to take damage.
     * 	</p>
     *
     *  @param context        the RPGLContext in which the RPGLObject takes damage
     *  @param damageDelivery a DamageDelivery object containing damage data
     *
     * 	@throws Exception if an exception occurs.
     */
    public void receiveDamage(RPGLContext context, DamageDelivery damageDelivery) throws Exception {
        JsonObject damageObject = damageDelivery.getDamage();
        for (Map.Entry<String, Object> damageObjectEntry : damageObject.entrySet()) {
            String damageType = damageObjectEntry.getKey();
            Long damage = (Long) damageObjectEntry.getValue();

            DamageAffinity damageAffinity = new DamageAffinity();
            String damageAffinityJsonString = String.format("""
                            {
                                "subevent": "damage_affinity",
                                "type": "%s"
                            }
                            """,
                    damageType
            );
            JsonObject damageAffinityJson = JsonParser.parseObjectString(damageAffinityJsonString);
            damageAffinity.joinSubeventJson(damageAffinityJson);
            damageAffinity.setSource(this);
            damageAffinity.prepare(context);
            damageAffinity.setTarget(this);
            damageAffinity.invoke(context);
            String affinity = damageAffinity.getAffinity();

            if ("normal".equals(affinity)) {
                this.reduceHitPoints(damage);
            } else if ("resistance".equals(affinity)) {
                this.reduceHitPoints(damage / 2L);
            } else if ("vulnerability".equals(affinity)) {
                this.reduceHitPoints(damage * 2L);
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>receiveDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void reduceHitPoints(long amount)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method directly reduces the hit points of the RPGLObject. This is not intended to be called directly.
     * 	</p>
     *
     *  @param amount a quantity of damage
     */
    void reduceHitPoints(long amount) {
        JsonObject healthData = (JsonObject) this.get("health_data");
        Long temporaryHitPoints = (Long) healthData.get("temporary");
        Long currentHitPoints = (Long) healthData.get("current");
        if (amount > temporaryHitPoints) {
            amount -= temporaryHitPoints;
            temporaryHitPoints = 0L;
            currentHitPoints -= amount;
        } else {
            temporaryHitPoints -= amount;
        }
        healthData.put("temporary", temporaryHitPoints);
        healthData.put("current", currentHitPoints);
        // TODO deal with 0 or negative hit points after this...
    }

    /**
     * 	<p>
     * 	<b><i>getBaseArmorClass</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public long getBaseArmorClass(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the base armor class of the RPGLObject.
     * 	</p>
     *
     *  @param context the RPGLContext in which the RPGLObject's base armor class is determined
     *
     * 	@throws Exception if an exception occurs.
     */
    public long getBaseArmorClass(RPGLContext context) throws Exception {
        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();
        String calculateBaseArmorClassJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject calculateBaseArmorClassJson = JsonParser.parseObjectString(calculateBaseArmorClassJsonString);
        calculateBaseArmorClass.joinSubeventJson(calculateBaseArmorClassJson);
        calculateBaseArmorClass.setSource(this);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.setTarget(this);
        calculateBaseArmorClass.invoke(context);
        return calculateBaseArmorClass.get();
    }

    /**
     * 	<p>
     * 	<b><i>giveItem</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void giveItem(String itemUuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds a RPGLItem to the RPGLObject's inventory.
     * 	</p>
     *
     *  @param itemUuid a RPGLItem's UUID String
     */
    public void giveItem(String itemUuid) {
        JsonObject items = (JsonObject) this.get("items");
        JsonArray inventory = (JsonArray) items.get("inventory");
        inventory.add(itemUuid);
    }

    /**
     * 	<p>
     * 	<b><i>equipItem</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void equipItem(String itemUuid, String equipmentSlot)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method causes the RPGLObject to equip a RPGLItem in a specified equipment slot. The RPGLItem will not be
     * 	equipped if it is not already in the RPGLObject's inventory.
     * 	</p>
     *
     *  @param itemUuid      a RPGLItem's UUID String
     *  @param equipmentSlot an equipment slot name (can be anything other than <code>"inventory"</code>)
     */
    public void equipItem(String itemUuid, String equipmentSlot) {
        // TODO make a subevent for equipping an item
        JsonObject items = (JsonObject) this.get("items");
        JsonArray inventory = (JsonArray) items.get("inventory");
        if (inventory.contains(itemUuid)) {
            items.put(equipmentSlot, itemUuid);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            item.updateEquippedEffects(this);
            item.defaultAttackAbilities();
            // TODO account for 2-handed items...
        }
    }

}
