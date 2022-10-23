package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

/**
 * RPGLObjects are objects which represent anything which might be placed on a game board (not including the game board
 * or terrain itself).
 *
 * @author Calvin Withun
 */
public class RPGLObject extends JsonObject {

    /**
     * A copy-constructor for the RPGLObject class.
     *
     * @param data the data to be copied to this object
     */
    RPGLObject(JsonObject data) {
        this.join(data);
    }

    public void addEffect(RPGLEffect effect) {
        JsonArray effects = (JsonArray) this.get("effects");
        if (effects == null) {
            effects = new JsonArray();
            this.put("effects", effects);
        }
        effects.add(effect.get("uuid"));
    }

    private RPGLEffect[] getEffects() {
        JsonArray effectUuids = (JsonArray) this.get("effects");
        RPGLEffect[] effects = new RPGLEffect[effectUuids.size()];
        int i = 0;
        for (Object effectUuidElement : effectUuids) {
            if (effectUuidElement instanceof Long effectUuid) {
                effects[i] = UUIDTable.getEffect(effectUuid);
            }
            i++;
        }
        return effects;
    }

    public void invokeEvent(RPGLObject[] targets, RPGLEvent event) throws SubeventMismatchException, JsonFormatException {
        // assume that any necessary resources have already been spent
        JsonArray subevents = (JsonArray) event.get("subevents");
        for (Object subeventElement : subevents) {
            if (subeventElement instanceof JsonObject subeventJson) {
                String subeventId = (String) subeventJson.get("subevent");
                Subevent subevent = Subevent.SUBEVENTS.get(subeventId).clone(subeventJson);
                subevent.prepare(this);
                for (RPGLObject target : targets) {
                    Subevent subeventClone = subevent.clone();
                    subeventClone.invoke(this, target);
                }
            }
        }
    }

    public boolean processSubevent(RPGLObject source, RPGLObject target, Subevent subevent) {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffects()) {
            wasSubeventProcessed |= effect.processSubevent(source, target, subevent);
        }
        return wasSubeventProcessed;
    }

    public int getProficiencyModifier() {
        int level;
        try {
            JsonArray hitDice = (JsonArray) this.seek("health_data.hit_dice");
            level = hitDice.size();
        } catch (JsonFormatException e) {
            level = 1;
        }
        return this.getProficiencyModifier(level);
    }

    public int getProficiencyModifier(int level) {
        return ((level - 1) / 4) + 2;
    }

    public int getAbilityModifier(String ability) throws JsonFormatException {
        // TODO this should eventually operate using a Subevent to get the ability score
        return this.getAbilityModifier((Long) this.seek("ability_scores." + ability));
    }

    public int getAbilityModifier(long abilityScore) {
        if (abilityScore < 10) {
            // integer division rounds toward zero, so abilityScore must be
            // adjusted to calculate the correct values for negative modifiers
            abilityScore --;
        }
        return (int) ((abilityScore - 10) / 2);
    }

}
