package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.function.AddBonus;
import org.rpgl.function.AddDamage;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.List;
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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AttackRoll();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.asMap().putIfAbsent("withhold_damage_modifier", false);
        this.json.asMap().putIfAbsent("use_origin_attack_ability", false);
        this.json.asMap().putIfAbsent("target_armor_class", Integer.MIN_VALUE);
        this.json.asMap().putIfAbsent("critical_hit_threshold", 20);

        // Add tag so nested subevents such as DamageCollection can know they
        // hail from an attack roll made using a particular attack ability.
        this.addTag(this.getAbility(context));

        // Add weapon attack bonus, if applicable
        if (this.getOriginItem() != null) {
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
            }}, context, resources);
        }
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
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
            }}, context, resources);

            this.calculateTargetArmorClass(context, resources);
            this.calculateCriticalHitThreshold(context, resources);
            if (this.getBase() >= this.getCriticalHitThreshold()) {
                this.getBaseDamage(context, resources);
                this.getTargetDamage(context, resources);
                if (this.confirmCriticalDamage(context, resources)) {
                    this.getCriticalHitDamage(context, resources);
                }
                this.resolveDamage(context, resources);
                this.resolveNestedSubevents("hit", context, resources);
            } else if (this.isCriticalMiss() || super.get() < this.getTargetArmorClass()) {
                this.resolveNestedSubevents("miss", context, resources);
            } else {
                this.getBaseDamage(context, resources);
                this.getTargetDamage(context, resources);
                this.resolveDamage(context, resources);
                this.resolveNestedSubevents("hit", context, resources);
            }
        }
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("attack_ability");
    }

    /**
     * This helper method collects and stores the base damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the base damage is being determined
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void getBaseDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
        // Collect base typed damage dice and bonuses
        DamageCollection baseDamageCollection = new DamageCollection();
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage").deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_damage_collection");
            }});
        }});
        baseDamageCollection.setOriginItem(this.getOriginItem());
        baseDamageCollection.setSource(super.getSource());
        baseDamageCollection.prepare(context, resources);
        baseDamageCollection.setTarget(super.getSource());
        baseDamageCollection.invoke(context, resources);

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
                            "ability": getAbility(context),
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
            }}, context, List.of());
        }

        // Add origin item damage bonus, if applicable
        if (this.getOriginItem() != null) {
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
            }}, context, List.of());
        }

        // Replace damage key with base damage collection
        this.json.putJsonArray("damage", baseDamageCollection.getDamageCollection());
    }

    /**
     * This helper method collects and stores the target damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the target damage is being determined
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void getTargetDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
        // Collect target typed damage dice and bonuses
        DamageCollection targetDamageCollection = new DamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_collection");
            }});
        }});
        targetDamageCollection.setOriginItem(this.getOriginItem());
        targetDamageCollection.setSource(super.getSource());
        targetDamageCollection.prepare(context, resources);
        targetDamageCollection.setTarget(super.getTarget());
        targetDamageCollection.invoke(context, resources);

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
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void calculateTargetArmorClass(RPGLContext context, List<RPGLResource> resources) throws Exception {
        CalculateEffectiveArmorClass calculateEffectiveArmorClass = new CalculateEffectiveArmorClass();
        calculateEffectiveArmorClass.joinSubeventData(new JsonObject() {{
            this.putJsonObject("base", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", getTarget().getBaseArmorClass(context));
            }});
        }});
        calculateEffectiveArmorClass.setOriginItem(this.getOriginItem());
        calculateEffectiveArmorClass.setSource(super.getSource());
        calculateEffectiveArmorClass.prepare(context, resources);
        calculateEffectiveArmorClass.setTarget(super.getTarget());
        calculateEffectiveArmorClass.invoke(context, resources);
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
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void calculateCriticalHitThreshold(RPGLContext context, List<RPGLResource> resources) throws Exception {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold();
        calculateCriticalHitThreshold.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        calculateCriticalHitThreshold.setOriginItem(this.getOriginItem());
        calculateCriticalHitThreshold.setSource(super.getSource());
        calculateCriticalHitThreshold.prepare(context, resources);
        calculateCriticalHitThreshold.setTarget(super.getTarget());
        calculateCriticalHitThreshold.invoke(context, resources);

        this.json.putInteger("critical_hit_threshold", calculateCriticalHitThreshold.get());
    }

    /**
     * This helper method confirms that a critical hit deals critical damage.
     *
     * @param context the context in which a critical hit is scored
     * @param resources a list of resources used to produce this subevent
     * @return true if the attack should deal critical damage, false otherwise
     *
     * @throws Exception if an exception occurs
     */
    boolean confirmCriticalDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
        CriticalDamageConfirmation criticalDamageConfirmation = new CriticalDamageConfirmation();
        criticalDamageConfirmation.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        criticalDamageConfirmation.setOriginItem(this.getOriginItem());
        criticalDamageConfirmation.setSource(super.getSource());
        criticalDamageConfirmation.prepare(context, resources);
        criticalDamageConfirmation.setTarget(super.getTarget());
        criticalDamageConfirmation.invoke(context, resources);
        return criticalDamageConfirmation.isNotCanceled();
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
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void getCriticalHitDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
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
        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", damageArray);
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        criticalHitDamageCollection.setOriginItem(this.getOriginItem());
        criticalHitDamageCollection.setSource(super.getSource());
        criticalHitDamageCollection.prepare(context, resources);
        criticalHitDamageCollection.setTarget(super.getTarget());
        criticalHitDamageCollection.invoke(context, resources);

        // Set the attack damage to the critical hit damage collection
        this.json.putJsonArray("damage", criticalHitDamageCollection.getDamageCollection());
    }

    /**
     * This helper method rolls all damage dice for this attack and precipitates the delivery of that damage to the
     * target.
     *
     * @param context the context in which the damage is being rolled.
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void resolveDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
        DamageRoll damageRoll = new DamageRoll();
        damageRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("attack_damage_roll");
            }});
        }});
        damageRoll.setOriginItem(this.getOriginItem());
        damageRoll.setSource(super.getSource());
        damageRoll.prepare(context, resources);
        damageRoll.setTarget(super.getTarget());
        damageRoll.invoke(context, resources);

        // Store final damage by type to damage key
        this.json.putJsonArray("damage", damageRoll.getDamage());

        this.deliverDamage(context, resources);
    }

    /**
     * This helper method resolves any additional subevents specified in the AttackRoll json according to whether the
     * attack hit or missed.
     *
     * @param resolution a String indicating the resolution of the Subevent (<code>"hit"</code> or <code>"miss"</code>)
     * @param context the context this Subevent takes place in
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String resolution, RPGLContext context, List<RPGLResource> resources) throws Exception {
        JsonArray subeventJsonArray = Objects.requireNonNullElse(this.json.getJsonArray(resolution), new JsonArray());
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject nestedSubeventJson = subeventJsonArray.getJsonObject(i);
            Subevent subevent = Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson);
            subevent.setOriginItem(this.getOriginItem());
            subevent.setSource(super.getSource());
            subevent.prepare(context, resources);
            subevent.setTarget(super.getTarget());
            subevent.invoke(context, resources);
        }
    }

    /**
     * This helper method delivers the finalized damage of the attack to the target.
     *
     * @param context the context in which the damage is being delivered
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void deliverDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        damageDelivery.setOriginItem(this.getOriginItem());
        damageDelivery.setSource(super.getSource());
        damageDelivery.prepare(context, resources);
        damageDelivery.setTarget(super.getTarget());
        damageDelivery.invoke(context, resources);

        JsonObject damageByType = damageDelivery.getDamage();
        if (this.json.asMap().containsKey("vampirism")) {
            VampiricSubevent.handleVampirism(this, damageByType, context, resources);
        }
    }

}
