package org.rpgl.core;

import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.CalculateAbilityScore;
import org.rpgl.subevent.CalculateBaseArmorClass;
import org.rpgl.subevent.CalculateProficiencyBonus;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.GetSavingThrowProficiency;
import org.rpgl.subevent.GetWeaponProficiency;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;
import org.rpgl.uuidtable.UUIDTableElement;

import java.util.Map;

/**
 * This class represents anythig which might appear on a battle map. Examples of this include buildings, Goblins, and
 * discarded items.
 *
 * @author Calvin Withun
 */
public class RPGLObject extends UUIDTableElement {

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
     * public void addEffect(RPGLEffect effect)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method adds an RPGLEffect to the RPGLObject.
     * 	</p>
     *
     * 	TODO should effects be restricted if they are the same? double-dipping
     *
     * 	@param effect a RPGLEffect to be assigned to the RPGLObject
     */
    public void addEffect(RPGLEffect effect) {
        this.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS).addString(effect.getUuid());
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
        return this.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS).asList().remove(effect.getUuid());
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
        JsonArray objectEffects = this.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS);
        JsonObject equippedItems = this.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
        JsonArray equippedItemEffects = new JsonArray();
        for (Map.Entry<String, ?> equippedItemEntry : equippedItems.asMap().entrySet()) {
            String equippedItemUuid = equippedItems.getString(equippedItemEntry.getKey());
            RPGLItem equippedItem = UUIDTable.getItem(equippedItemUuid);
            equippedItemEffects.asList().addAll(equippedItem.getJsonArray(RPGLItemTO.WHILE_EQUIPPED_ALIAS).asList());
        }
        RPGLEffect[] effects = new RPGLEffect[objectEffects.size() + equippedItemEffects.size()];
        for (int i = 0; i < effects.length; i++) {
            if (i < objectEffects.size()) {
                effects[i] = UUIDTable.getEffect(objectEffects.getString(i));
            } else {
                effects[i] = UUIDTable.getEffect(equippedItemEffects.getString(i - objectEffects.size()));
            }
        }
        return effects;
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
     * 	This method returns an array of RPGLEvents which this RPGLObject has access to (including any granted from
     * 	active effects).
     * 	</p>
     *
     *  @return an array of RPGLEvent ID Strings
     */
    public RPGLEvent[] getEvents() {
        // TODO make a Subevent for collecting additional RPGLEvent ID's
        JsonArray eventsArray = this.getJsonArray(RPGLObjectTO.EVENTS_ALIAS);
        RPGLEvent[] events = new RPGLEvent[eventsArray.size()];
        for (int i = 0; i < events.length; i++) {
            events[i] = RPGLFactory.newEvent(eventsArray.getString(i));
        }
        return events;
    }

    /**
     * 	<p>
     * 	<b><i>getProficiencyBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int getProficiencyBonus(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the proficiency bonus of the RPGLObject.
     * 	</p>
     *
     *  @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     *  @return this RPGLObject's proficiency bonus
     *
     * 	@throws Exception if an exception occurs.
     */
    public int getProficiencyBonus(RPGLContext context) throws Exception {
        CalculateProficiencyBonus calculateProficiencyBonus = new CalculateProficiencyBonus();
        calculateProficiencyBonus.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_proficiency_modifier");
        }});
        calculateProficiencyBonus.setSource(this);
        calculateProficiencyBonus.prepare(context);
        calculateProficiencyBonus.setTarget(this);
        calculateProficiencyBonus.invoke(context);
        return calculateProficiencyBonus.get();
    }

    /**
     * 	<p>
     * 	<b><i>getAbilityModifierFromAbilityScore</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int getAbilityModifierFromAbilityScore(RPGLContext context, String ability)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the RPGLObject's ability score modifier for a specified ability score.
     * 	</p>
     *
     *  @param context the RPGLContext in which the RPGLObject's proficiency bonus is determined
     *  @param ability the ability score whose modifier will be determined
     *  @return the modifier of the target ability
     *
     * 	@throws Exception if an exception occurs.
     */
    public int getAbilityModifierFromAbilityScore(RPGLContext context, String ability) throws Exception {
        CalculateAbilityScore calculateAbilityScore = new CalculateAbilityScore();
        calculateAbilityScore.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_ability_score");
            this.putString("ability", ability);
        }});
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
     * public int getAbilityModifierFromAbilityScore(int abilityScore)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the modifier for an ability score.
     * 	</p>
     *
     *  @param abilityScore an ability score number
     *  @return the modifier for the passed score
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
        getSavingThrowProficiency.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_saving_throw_proficiency");
            this.putString("save_ability", saveAbility);
        }});
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
     *  @return true if the RPGLObject is proficient with the item corresponding to the passed UUID
     *
     * 	@throws Exception if an exception occurs.
     */
    public boolean isProficientWithWeapon(RPGLContext context, String itemUuid) throws Exception {
        GetWeaponProficiency getWeaponProficiency = new GetWeaponProficiency();
        getWeaponProficiency.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_weapon_proficiency");
            this.putString("item", itemUuid);
        }});
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
            damageAffinity.setSource(this);
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

    /**
     * 	<p>
     * 	<b><i>receiveDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void reduceHitPoints(int amount)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method directly reduces the hit points of the RPGLObject. This is not intended to be called directly.
     * 	</p>
     *
     *  @param amount a quantity of damage
     */
    void reduceHitPoints(int amount) {
        Map<String, Object> healthData = this.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).asMap();
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
     * 	<p>
     * 	<b><i>getBaseArmorClass</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int getBaseArmorClass(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the base armor class of the RPGLObject.
     * 	</p>
     *
     *  @param context the RPGLContext in which the RPGLObject's base armor class is determined
     *  @return the RPGLObject's base armor class
     *
     * 	@throws Exception if an exception occurs.
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
        JsonArray inventory = this.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
        if (!inventory.asList().contains(itemUuid)) {
            inventory.addString(itemUuid);
        }
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
        JsonArray inventory = this.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
        if (inventory.asList().contains(itemUuid)) {
            JsonObject equippedItems = this.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
            equippedItems.putString(equipmentSlot, itemUuid);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            item.updateEquippedEffects(this);
            item.defaultAttackAbilities();
            // TODO account for 2-handed items...
        }
    }
}
