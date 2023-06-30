package org.rpgl.core;

import org.rpgl.condition.Condition;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.function.Function;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;
import org.rpgl.uuidtable.UUIDTableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * This class represents anything which impacts how Subevents resolve. Conventional examples of this include status
 * conditions such as being poisoned, burning, or being unconscious, but this class is also used to facilitate many
 * background mechanics such as granting special RPGLEvents to an RPGLObject or restoring exhausted RPGLResources.
 *
 * @author Calvin Withun
 */
public class RPGLEffect extends UUIDTableElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPGLEffect.class);

    /**
     * Returns the RPGLEffect Subevent filters.
     *
     * @return a JsonObject representing the RPGLEffect object's Subevent filters
     */
    public JsonObject getSubeventFilters() {
        return this.getJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS);
    }

    /**
     * Setter for subevent filters.
     *
     * @param subeventFilters a new subevent filters JsonObject
     */
    public void setSubeventFilters(JsonObject subeventFilters) {
        this.putJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS, subeventFilters);
    }

    /**
     * Returns the source of the RPGLEffect.
     *
     * @return a RPGLObject
     */
    public RPGLObject getSource() {
        return UUIDTable.getObject(this.getString(RPGLEffectTO.SOURCE_ALIAS));
    }

    /**
     * Sets the source of the RPGLEffect.
     *
     * @param source a RPGLObject
     */
    public void setSource(RPGLObject source) {
        this.putString(RPGLEffectTO.SOURCE_ALIAS, source.getUuid());
    }

    /**
     * Returns the target of the RPGLEffect.
     *
     * @return a RPGLObject
     */
    public RPGLObject getTarget() {
        return UUIDTable.getObject(this.getString(RPGLEffectTO.TARGET_ALIAS));
    }

    /**
     * Sets the target of the RPGLEffect.
     *
     * @param target a RPGLObject
     */
    public void setTarget(RPGLObject target) {
        this.putString(RPGLEffectTO.TARGET_ALIAS, target.getUuid());
    }

    /**
     * Returns the origin item UUID for the RPGLEffect if it has one.
     *
     * @return an RPGLItem UUID, or null if the effect was not produced by an item.
     */
    public String getOriginItem() {
        return this.getString(RPGLEffectTO.ORIGIN_ITEM_ALIAS);
    }

    /**
     * Sets the origin item UUID of the RPGLEffect.
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
     * This method checks the passed Subevent against the RPGLEffect's Conditions, and if they evaluate true, the
     * RPGLEffect executes its functions.
     *
     * @param subevent a Subevent
     * @param context  the context in which the subevent is being processed
     * @return true if the Subevent was present in this object's subevent filter and if the Conditions in that filter
     *         were satisfied
     *
     * @throws Exception if an exception occurs
     */
    public boolean processSubevent(Subevent subevent, RPGLContext context) throws Exception {
        JsonObject subeventFilters = this.getSubeventFilters();
        for (Map.Entry<String, ?> subeventFilterEntry : subeventFilters.asMap().entrySet()) {
            if (Objects.equals(subevent.getSubeventId(), subeventFilterEntry.getKey())) {
                JsonArray matchedFilterBehaviors = subeventFilters.getJsonArray(subeventFilterEntry.getKey());
                for (int i = 0; i < matchedFilterBehaviors.size(); i++) {
                    JsonObject matchedFilterBehavior = matchedFilterBehaviors.getJsonObject(i);
                    JsonArray conditions = matchedFilterBehavior.getJsonArray("conditions");
                    if (!subevent.hasModifyingEffect(this) && this.evaluateConditions(subevent, conditions, context)) {
                        JsonArray functionJsonArray = matchedFilterBehavior.getJsonArray("functions");
                        executeFunctions(subevent, functionJsonArray, context);
                        subevent.addModifyingEffect(this);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This helper method evaluates a given collection of Conditions on a given RPGLObject source and target.
     *
     * @param subevent   the Subevent being invoked
     * @param conditions a collection of JSON data defining Conditions
     * @param context    the context in which the Conditions are being evaluated
     * @return true if any Conditions evaluated to true
     *
     * @throws Exception if an exception occurs
     */
    boolean evaluateConditions(Subevent subevent, JsonArray conditions, RPGLContext context) throws Exception {
        boolean conditionsMet = true;
        for (int i = 0; i < conditions.size(); i++) {
            JsonObject conditionJson = conditions.getJsonObject(i);
            System.out.println(conditionJson);
            conditionsMet &= Condition.CONDITIONS
                    .get(conditionJson.getString("condition"))
                    .evaluate(this, subevent, conditionJson, context);
        }
        return conditionsMet;
    }

    /**
     * This helper method executes a given collection of Functions on given RPGLObjects and Subevents.
     *
     * @param subevent  the Subevent being invoked
     * @param functions a collection of JSON data defining Functions
     * @param context   the context in which the Functions are being executed
     *
     * @throws Exception if an exception occurs
     */
    void executeFunctions(Subevent subevent, JsonArray functions, RPGLContext context) throws Exception {
        for (int i = 0; i < functions.size(); i++) {
            JsonObject functionJson = functions.getJsonObject(i);
            Function.FUNCTIONS
                    .get(functionJson.getString("function"))
                    .execute(this, subevent, functionJson, context);
        }
    }

    /**
     * This helper method retrieves the source or the target RPGLObject of either an RPGLEffect or a Subevent being
     * processed.
     *
     * @param effect       the RPGLEffect processing subevent
     * @param subevent     the Subevent being processed
     * @param instructions the JSON data instructing which RPGLObject should be returned
     * @return a RPGLObject
     *
     * @throws Exception if an exception occurs
     */
    public static RPGLObject getObject(RPGLEffect effect, Subevent subevent, JsonObject instructions) throws Exception {
        String from = instructions.getString("from");
        String object = instructions.getString("object");
        if ("subevent".equals(from)) {
            if ("source".equals(object)) {
                return subevent.getSource();
            } else if ("target".equals(object)) {
                return subevent.getTarget();
            }
        } else if ("effect".equals(from)) {
            if ("source".equals(object)) {
                return effect.getSource();
            } else if ("target".equals(object)) {
                return effect.getTarget();
            }
        }
        Exception e = new Exception("could not isolate an RPGLObject: " + instructions);
        LOGGER.error(e.getMessage());
        throw e;
    }

}
