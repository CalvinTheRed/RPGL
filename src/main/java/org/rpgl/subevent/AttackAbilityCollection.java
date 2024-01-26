package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
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
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putJsonArray("abilities", new JsonArray());
        if (UUIDTable.getItem(super.getOriginItem()).hasTag("finesse")) {
            this.addAbility("dex");
        }
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
