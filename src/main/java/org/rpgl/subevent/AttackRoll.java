package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.function.AddBonus;
import org.rpgl.function.AddDamage;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This Subevent is dedicated to making an attack roll and resolving all fallout from making the attack. This is a
 * high-level Subevent which can be referenced in an RPGLEvent template.
 * <br>
 * <br>
 * Source: an RPGLObject making an attack
 * <br>
 * Target: an RPGLObject being attacked
 *
 * @author Calvin Withun
 */
public class AttackRoll extends Roll {

    public AttackRoll() {
        super("attack_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AttackRoll();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AttackRoll();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public AttackRoll invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (AttackRoll) super.invoke(context, originPoint);
    }

    @Override
    public AttackRoll joinSubeventData(JsonObject other) {
        return (AttackRoll) super.joinSubeventData(other);
    }

    @Override
    public AttackRoll prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("withhold_damage_modifier", false);
        this.json.asMap().putIfAbsent("use_origin_attack_ability", false);
        this.json.asMap().putIfAbsent("target_armor_class", Integer.MIN_VALUE);
        this.json.asMap().putIfAbsent("critical_hit_threshold", 20);

        // Add tag so nested subevents such as DamageCollection can know they
        // hail from an attack roll made using a particular attack ability.
        this.addTag(this.getAbility(context));

        // Add tag so nested subevents such as DamageCollection can know they
        // hail from an attack roll of a particular attack type.
        this.addTag(this.json.getString("attack_type"));

        // Add weapon attack bonus, if applicable
        if (super.getOriginItem() != null) {
            new AddBonus().execute(null, this, new JsonObject() {{
                /*{
                    "function": "add_bonus",
                    "bonus": [
                        {
                            "formula": "range",
                            "dice": [ ],
                            "bonus": <origin item attack bonus>
                        }
                    ]
                }*/
                this.putString("function", "add_bonus");
                this.putJsonArray("bonus", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "range");
                        this.putJsonArray("dice", new JsonArray());
                        this.putInteger("bonus", UUIDTable.getItem(getOriginItem()).getAttackBonus());
                    }});
                }});
            }}, context, originPoint);
        }
        return this;
    }

    @Override
    public AttackRoll run(RPGLContext context, JsonArray originPoint) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            this.json.asMap().putIfAbsent("damage", new ArrayList<>());
            new AddBonus().execute(null, this, new JsonObject() {{
                /*{
                    "function": "add_bonus",
                    "bonus": [
                        {
                            "formula": "modifier",
                            "ability": <getAbility>
                            "object": {
                                "from": "subevent",
                                "object": "source",
                                "as_origin": <use_origin_attack_ability>
                            }
                        }
                    ]
                }*/
                this.putString("function", "add_bonus");
                this.putJsonArray("bonus", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "modifier");
                        this.putString("ability", getAbility(context));
                        this.putJsonObject("object", new JsonObject() {{
                            this.putString("from", "subevent");
                            this.putString("object", "source");
                            this.putBoolean("as_origin", json.getBoolean("use_origin_attack_ability"));
                        }});
                    }});
                }});
            }}, context, originPoint);

            this.calculateTargetArmorClass(context);
            this.calculateCriticalHitThreshold(context);
            if (this.getBase() >= this.getCriticalHitThreshold()) {
                this.getBaseDamage(context, originPoint);
                this.getTargetDamage(context, originPoint);
                if (this.confirmCriticalDamage(context)) {
                    this.getCriticalHitDamage(context, originPoint);
                }
                this.resolveDamage(context, originPoint);
                this.resolveNestedSubevents("hit", context, originPoint);
            } else if (this.isCriticalMiss() || super.get() < this.getTargetArmorClass()) {
                this.resolveNestedSubevents("miss", context, originPoint);
            } else {
                this.getBaseDamage(context, originPoint);
                this.getTargetDamage(context, originPoint);
                this.resolveDamage(context, originPoint);
                this.resolveNestedSubevents("hit", context, originPoint);
            }
        }
        return this;
    }

    @Override
    public AttackRoll setOriginItem(String originItem) {
        return (AttackRoll) super.setOriginItem(originItem);
    }

    @Override
    public AttackRoll setSource(RPGLObject source) {
        return (AttackRoll) super.setSource(source);
    }

    @Override
    public AttackRoll setTarget(RPGLObject target) {
        return (AttackRoll) super.setTarget(target);
    }

    @Override
    public AttackRoll grantAdvantage() {
        return (AttackRoll) super.grantAdvantage();
    }

    @Override
    public AttackRoll grantDisadvantage() {
        return (AttackRoll) super.grantDisadvantage();
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("attack_ability");
    }

    /**
     * This helper method collects and stores the base damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the base damage is being determined
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void getBaseDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        // Collect base typed damage dice and bonuses
        DamageCollection baseDamageCollection = new DamageCollection()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", json.getJsonArray("damage").deepClone());
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("base_damage_collection");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getSource())
                .invoke(context, originPoint);

        String damageType = this.json.getJsonArray("damage").getJsonObject(0).getString("damage_type");

        // Add damage modifier from attack ability, if applicable
        if (!this.json.getBoolean("withhold_damage_modifier")) { // TODO make a function ond condition for this stuff...
            new AddDamage().execute(null, baseDamageCollection, new JsonObject() {{
                /*{
                    "function": "add_damage",
                    "damage": [
                        {
                            "formula": "modifier",
                            "damage_type": damageType
                            "ability": getAbility(context, originPoint),
                            "object": {
                                "from": "subevent",
                                "object": "source",
                                "as_origin": if origin ability is used for the attack
                            }
                        }
                    ]
                }*/
                this.putString("function", "add_damage");
                this.putJsonArray("damage", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "modifier");
                        this.putString("damage_type", damageType);
                        this.putString("ability", getAbility(context));
                        this.putJsonObject("object", new JsonObject() {{
                            this.putString("from", "subevent");
                            this.putString("object", "source");
                            this.putBoolean("as_origin", json.getBoolean("use_origin_attack_ability"));
                        }});
                    }});
                }});
            }}, context, originPoint);
        }

        // Add origin item damage bonus, if applicable
        if (super.getOriginItem() != null) {
            new AddDamage().execute(null, baseDamageCollection, new JsonObject() {{
                /*{
                    "function": "add_damage",
                    "damage": [
                        {
                            "formula": "range",
                            "damage_type": damageType
                            "dice": [ ],
                            "bonus": origin item damage bonus
                        }
                    ]
                }*/
                this.putString("function", "add_damage");
                this.putJsonArray("damage", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "range");
                        this.putString("damage_type", damageType);
                        this.putJsonArray("dice", new JsonArray());
                        this.putInteger("bonus", UUIDTable.getItem(getOriginItem()).getDamageBonus());
                    }});
                }});
            }}, context, originPoint);
        }

        // Replace damage key with base damage collection
        this.json.putJsonArray("damage", baseDamageCollection.getDamageCollection());
    }

    /**
     * This helper method collects and stores the target damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the target damage is being determined
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void getTargetDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        // Collect target typed damage dice and bonuses
        DamageCollection targetDamageCollection = new DamageCollection()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("target_damage_collection");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getTarget())
                .invoke(context, originPoint);

        // add target damage collection to base damage collection
        this.json.getJsonArray("damage").asList().addAll(targetDamageCollection.getDamageCollection().asList());
    }

    /**
     * Getter method for the target's final armor class value. Note that calling this method before the target's armor
     * class is calculated will return <code>Integer.MIN_VALUE</code>.
     *
     * @return the target's final armor class, or <code>Integer.MIN_VALUE</code>
     */
    public int getTargetArmorClass() {
        return this.json.getInteger("target_armor_class");
    }

    /**
     * This helper method evaluates the effective armor class of the target to determine if the attack hits or misses.
     * This value can be influenced by the target after the attack roll is made to attempt to avoid the attack, and may
     * be different from the target's base armor class.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void calculateTargetArmorClass(RPGLContext context) throws Exception {
        CalculateEffectiveArmorClass calculateEffectiveArmorClass = new CalculateEffectiveArmorClass()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonObject("base", new JsonObject() {{
                        this.putString("formula", "number");
                        this.putInteger("number", getTarget().getBaseArmorClass(context));
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, getTarget().getPosition())
                .setTarget(super.getTarget())
                .invoke(context, getTarget().getPosition());

        this.json.putInteger("target_armor_class", calculateEffectiveArmorClass.get());
    }

    /**
     * Getter method for the attack's critical hit threshold. Note that calling this method before the target's critical
     * hit threshold is calculated will return <code>20</code>.
     *
     * @return the attack's critical hit threshold, or <code>20</code>
     */
    public int getCriticalHitThreshold() {
        return this.json.getInteger("critical_hit_threshold");
    }

    /**
     * This helper method calculates the critical hit threshold for the attack.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void calculateCriticalHitThreshold(RPGLContext context) throws Exception {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, this.getSource().getPosition())
                .setTarget(super.getTarget())
                .invoke(context, this.getSource().getPosition());

        this.json.putInteger("critical_hit_threshold", calculateCriticalHitThreshold.get());
    }

    /**
     * This helper method confirms that a critical hit deals critical damage.
     *
     * @param context the context in which a critical hit is scored
     * @return true if the attack should deal critical damage, false otherwise
     *
     * @throws Exception if an exception occurs
     */
    boolean confirmCriticalDamage(RPGLContext context) throws Exception {
        return new CriticalDamageConfirmation()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, this.getSource().getPosition())
                .setTarget(super.getTarget())
                .invoke(context, this.getSource().getPosition())
                .isNotCanceled();
    }

    /**
     * This method returns whether the attack roll is a critical miss.
     *
     * @return true if the attack is a critical miss
     */
    public boolean isCriticalMiss() {
        return this.getBase() == 1;
    }

    /**
     * This helper method collects and stores the critical hit damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the critical hit damage is being determined
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void getCriticalHitDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        // Get a copy of the attack damage with twice the number of dice
        JsonArray damageArray = this.json.getJsonArray("damage").deepClone();
        for (int i = 0; i < damageArray.size(); i++) {
            JsonObject damageJson = damageArray.getJsonObject(i);
            JsonArray dice = damageJson.getJsonArray("dice");
            if (dice != null) {
                dice.asList().addAll(dice.deepClone().asList());
            }
        }

        // Collect any extra damage bonuses which aren't doubled
        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", damageArray);
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getTarget())
                .invoke(context, originPoint);

        // Set the attack damage to the critical hit damage collection
        this.json.putJsonArray("damage", criticalHitDamageCollection.getDamageCollection());
    }

    /**
     * This helper method rolls all damage dice for this attack and precipitates the delivery of that damage to the
     * target.
     *
     * @param context the context in which the damage is being rolled.
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void resolveDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        DamageRoll damageRoll = new DamageRoll()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", json.getJsonArray("damage"));
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("attack_damage_roll");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getTarget())
                .invoke(context, originPoint);

        // Store final damage by type to damage key
        this.json.putJsonArray("damage", damageRoll.getDamage());

        this.deliverDamage(context, originPoint);
    }

    /**
     * This helper method resolves any additional subevents specified in the AttackRoll json according to whether the
     * attack hit or missed.
     *
     * @param resolution a String indicating the resolution of the Subevent (<code>"hit"</code> or <code>"miss"</code>)
     * @param context the context this Subevent takes place in
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String resolution, RPGLContext context, JsonArray originPoint) throws Exception {
        JsonArray subeventJsonArray = Objects.requireNonNullElse(this.json.getJsonArray(resolution), new JsonArray());
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject nestedSubeventJson = subeventJsonArray.getJsonObject(i);
            Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson)
                    .setOriginItem(super.getOriginItem())
                    .setSource(super.getSource())
                    .prepare(context, originPoint)
                    .setTarget(super.getTarget())
                    .invoke(context, originPoint);
        }
    }

    /**
     * This helper method delivers the finalized damage of the attack to the target.
     *
     * @param context the context in which the damage is being delivered
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void deliverDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", json.getJsonArray("damage"));
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getTarget())
                .invoke(context, originPoint);

        JsonObject damageByType = damageDelivery.getDamage();
        if (this.json.asMap().containsKey("vampirism")) {
            VampiricSubevent.handleVampirism(this, damageByType, context, originPoint);
        }
    }

}
