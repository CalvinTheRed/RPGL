package org.rpgl.core;

import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.json.JsonArray;

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

}
