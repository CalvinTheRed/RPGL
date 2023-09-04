package org.rpgl.core;

import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.Subevent;

import java.util.Objects;

/**
 * This class represents the currency which an RPGLObject exchanges in order to use RPGLEvents. This may represent an
 * action, a pell slot, a Ki point, a sorcery point, or other conventional resources, or it may represent a more
 * abstract resource, such as the ability to make a single claw attack as a part of a multiattack. Regardless, every
 * resource has a potency to indicate how effective or powerful it is compared to others of its type (mostly used for
 * spell slots), and a criterion by which the resource becomes refreshed to be used again. This is typically the start
 * of a turn, the end of a turn, a long rest, or a short rest.
 *
 * @author Calvin Withun
 */
public class RPGLResource extends RPGLTaggable {

    /**
     * Returns the RPGLResource's potency
     *
     * @return a JsonObject containing the RPGLResource's potency
     */
    public Integer getPotency() {
        return this.getInteger(RPGLResourceTO.POTENCY_ALIAS);
    }

    /**
     * Setter for potency.
     *
     * @param potency a new potency
     */
    public void setPotency(int potency) {
        this.putInteger(RPGLResourceTO.POTENCY_ALIAS, potency);
    }

    /**
     * Returns the RPGLResource's exhaustion status
     *
     * @return a boolean representing the RPGLResource's exhaustion status
     */
    public Boolean getExhausted() {
        return this.getBoolean(RPGLResourceTO.EXHAUSTED_ALIAS);
    }

    /**
     * Setter for exhausted.
     * <br>
     * <br>
     * NOTE: this method should not be used to refresh or exhaust a RPGLResource - use the <code>refresh()</code> or
     * <code>exhaust()</code> methods instead, respectively.
     *
     * @param exhausted a new exhausted
     */
    public void setExhausted(boolean exhausted) {
        this.putBoolean(RPGLResourceTO.EXHAUSTED_ALIAS, exhausted);
    }

    /**
     * Returns the RPGLResource's refresh criterion
     *
     * @return a JsonArray containing the RPGLResource's refresh criterion
     */
    public JsonArray getRefreshCriterion() {
        return this.getJsonArray(RPGLResourceTO.REFRESH_CRITERION_ALIAS);
    }

    /**
     * Setter for potency.
     *
     * @param refreshCriterion a new refresh criterion
     */
    public void setRefreshCriterion(JsonArray refreshCriterion) {
        this.putJsonArray(RPGLResourceTO.REFRESH_CRITERION_ALIAS, refreshCriterion);
    }

    /**
     * Returns the origin item UUID for the RPGLResource if it has one.
     *
     * @return an RPGLItem UUID, or null if the resource was not provided by an item.
     */
    public String getOriginItem() {
        return this.getString(RPGLEffectTO.ORIGIN_ITEM_ALIAS);
    }

    /**
     * Sets the origin item UUID of the RPGLResource.
     *
     * @param originItem a RPGLItem UUID
     */
    public void setOriginItem(String originItem) {
        this.putString(RPGLEffectTO.ORIGIN_ITEM_ALIAS, originItem);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * This method investigates a Subevent to check if it satisfies the refresh criterion for the resource.
     *
     * @param subevent a Subevent
     * @param owner the RPGLObject to which this resource is assigned
     */
    public void processSubevent(Subevent subevent, RPGLObject owner) {
        if (this.getExhausted()) {
            JsonArray refreshCriterion = this.getRefreshCriterion();
            for (int i = 0; i < refreshCriterion.size(); i++) {
                if (this.checkCriterion(subevent, refreshCriterion.getJsonObject(i), owner)) {
                    break;
                }
            }
        }
    }

    /**
     * This helper method evaluates whether a Subevent matches a particular refresh criterion of this resource, and
     * refreshes it if the criterion is fully satisfied.
     *
     * @param subevent a Subevent
     * @param criterion a particular refresh criterion for this resource
     * @param owner the RPGLObject to which this resource is assigned
     * @return true if this causes the resource to be refreshed, false otherwise. Note that this may return false even
     *         if the criterion ismet, in the case where it is chance-based or must be satisfied multiple times before
     *         refreshing the resource.
     */
    boolean checkCriterion(Subevent subevent, JsonObject criterion, RPGLObject owner) {
        String actorAlias = criterion.getString("actor"); // TODO could this have a better field name?
        RPGLObject actor = null;
        boolean anyActor = false;
        if ("source".equals(actorAlias)) {
            actor = subevent.getSource();
        } else if ("target".equals(actorAlias)) {
            actor = subevent.getTarget();
        } else if ("any".equals(actorAlias)) {
            anyActor = true;
        }
        if (Objects.equals(subevent.getSubeventId(), criterion.getString("subevent"))
                && subevent.getTags().asList().containsAll(criterion.getJsonArray("tags").asList())
                && Math.random() * 100 <= criterion.getInteger("chance")
                && (anyActor || Objects.equals(owner, actor))) {
            int completed = criterion.getInteger("completed") + 1;
            if (completed >= criterion.getInteger("required")) {
                this.refresh();
                return true;
            }
            criterion.putInteger("completed", completed);
        }
        return false;
    }

    /**
     * This method exhausts the resource to make it unable to fuel future RPGLEvents. This method also triggers the
     * generation of the next required count.
     */
    public void exhaust() {
        this.setExhausted(true);
        JsonArray refreshCriterion = this.getRefreshCriterion();
        for (int i = 0; i < refreshCriterion.size(); i++) {
            JsonObject criterion = refreshCriterion.getJsonObject(i);
            criterion.putInteger("completed", 0);
            criterion.putInteger("required", generateRequired(criterion.getJsonObject("required_generator")));
        }
    }

    /**
     * This method refreshes the resource to make it available to fuel future RPGLEvents. This method also resets all
     * the resource's refresh criteria to have 0 completions and 0 required.
     */
    public void refresh() {
        this.setExhausted(false);
        JsonArray refreshCriterion = this.getRefreshCriterion();
        for (int i = 0; i < refreshCriterion.size(); i++) {
            JsonObject criterion = refreshCriterion.getJsonObject(i);
            criterion.putInteger("completed", 0);
            criterion.putInteger("required", 0);
        }
    }

    /**
     * This helper method generates a required count based on the required generator provided. This makes it possible to
     * have a different required count each time a resource is exhausted, in case it takes a variable amount of time to
     * refresh it each time it is exhausted.
     *
     * @param requiredGenerator a required generator for one refresh criterion of a resource
     * @return a required count
     */
    static int generateRequired(JsonObject requiredGenerator) {
        int required = Objects.requireNonNullElse(requiredGenerator.getInteger("bonus"), 0);
        JsonArray dice = requiredGenerator.getJsonArray("dice").deepClone();
        for (int i = 0; i < dice.size(); i++) {
            required += Die.roll(dice.getJsonObject(i));
        }
        return required;
    }

}
