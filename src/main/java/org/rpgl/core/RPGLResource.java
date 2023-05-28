package org.rpgl.core;

import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.Subevent;

import java.util.Objects;

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

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

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

    boolean checkCriterion(Subevent subevent, JsonObject criterion, RPGLObject owner) {
        String actorAlias = criterion.getString("actor");
        RPGLObject actor = null;
        if ("source".equals(actorAlias)) {
            actor = subevent.getSource();
        } else if ("target".equals(actorAlias)) {
            actor = subevent.getTarget();
        }
        if (Objects.equals(subevent.getSubeventId(), criterion.getString("subevent"))
                && subevent.getTags().asList().containsAll(criterion.getJsonArray("tags").asList())
                && Math.random() * 100 <= criterion.getInteger("chance")
                && Objects.equals(owner, actor)) {
            int completed = criterion.getInteger("completed") + 1;
            if (completed >= criterion.getInteger("required")) {
                this.refresh();
                return true;
            }
            criterion.putInteger("completed", completed);
        }
        return false;
    }

    public void exhaust() {
        this.setExhausted(true);
        JsonArray refreshCriterion = this.getRefreshCriterion();
        for (int i = 0; i < refreshCriterion.size(); i++) {
            JsonObject criterion = refreshCriterion.getJsonObject(i);
            criterion.putInteger("completed", 0);
            criterion.putInteger("required", generateRequired(criterion.getJsonObject("required_generator")));
        }
    }

    public void refresh() {
        this.setExhausted(false);
        JsonArray refreshCriterion = this.getRefreshCriterion();
        for (int i = 0; i < refreshCriterion.size(); i++) {
            JsonObject criterion = refreshCriterion.getJsonObject(i);
            criterion.putInteger("completed", 0);
            criterion.putInteger("required", 0);
        }
    }

    static int generateRequired(JsonObject requiredGenerator) {
        int required = Objects.requireNonNullElse(requiredGenerator.getInteger("bonus"), 0);
        JsonArray dice = requiredGenerator.getJsonArray("dice").deepClone();
        for (int i = 0; i < dice.size(); i++) {
            required += Die.roll(dice.getJsonObject(i));
        }
        return required;
    }

}
