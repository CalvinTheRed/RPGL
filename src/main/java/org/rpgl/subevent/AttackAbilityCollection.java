package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This Subevent is dedicated to collecting non-standard attack ability scores for attacks made with particular items.
 * <br>
 * <br>
 * Source: an RPGLObject collecting valid events for weapon attacks
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class AttackAbilityCollection extends Subevent {

    public AttackAbilityCollection() {
        super("attack_ability_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AttackAbilityCollection();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AttackAbilityCollection();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public AttackAbilityCollection invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (AttackAbilityCollection) super.invoke(context, originPoint);
    }

    @Override
    public AttackAbilityCollection joinSubeventData(JsonObject other) {
        return (AttackAbilityCollection) super.joinSubeventData(other);
    }

    @Override
    public AttackAbilityCollection prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putJsonArray("abilities", new JsonArray());
        if (UUIDTable.getItem(super.getOriginItem()).hasTag("finesse")) {
            this.addAbility("dex");
        }
        return this;
    }

    @Override
    public AttackAbilityCollection run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public AttackAbilityCollection setOriginItem(String originItem) {
        return (AttackAbilityCollection) super.setOriginItem(originItem);
    }

    @Override
    public AttackAbilityCollection setSource(RPGLObject source) {
        return (AttackAbilityCollection) super.setSource(source);
    }

    @Override
    public AttackAbilityCollection setTarget(RPGLObject target) {
        return (AttackAbilityCollection) super.setTarget(target);
    }

    /**
     * Adds an ability score name to the subevent.
     *
     * @param ability an ability score name
     */
    public void addAbility(String ability) {
        this.getAbilities().addString(ability);
    }

    /**
     * Returns the list of ability score names gathered for a weapon attack.
     *
     * @return a JsonArray of ability score names
     */
    public JsonArray getAbilities() {
        return this.json.getJsonArray("abilities");
    }

}
