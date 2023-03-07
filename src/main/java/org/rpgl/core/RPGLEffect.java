package org.rpgl.core;

import org.rpgl.condition.Condition;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.function.Function;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;
import org.rpgl.uuidtable.UUIDTableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * This method checks the passed Subevent against the RPGLEffect's Conditions, and if they evaluate true, the
     * RPGLEffect executes its functions.
     *
     * @param subevent a Subevent
     * @return true if the Subevent was present in this object's subevent filter and if the Conditions in that filter
     *         were satisfied
     *
     * @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     * @throws FunctionMismatchException  if a Function is passed the wrong Function ID.
     */
    public boolean processSubevent(Subevent subevent, RPGLContext context) throws Exception {
        JsonObject subeventFilters = this.getSubeventFilters();
        for (Map.Entry<String, ?> subeventFilterEntry : subeventFilters.asMap().entrySet()) {
            if (subevent.getSubeventId().equals(subeventFilterEntry.getKey())) {
                JsonObject matchedFilter = subeventFilters.getJsonObject(subeventFilterEntry.getKey());
                JsonArray conditions = matchedFilter.getJsonArray("conditions");
                if (!subevent.hasModifyingEffect(this) && this.evaluateConditions(subevent, conditions, context)) {
                    JsonArray functionJsonArray = matchedFilter.getJsonArray("functions");
                    executeFunctions(subevent, functionJsonArray, context);
                    subevent.addModifyingEffect(this);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * This method evaluates a given collection of Conditions on a given RPGLObject source and target.
     *
     * @param subevent   the Subevent being invoked
     * @param conditions a collection of JSON data defining Conditions
     * @return true if any Conditions evaluated to true
     *
     * @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     */
    boolean evaluateConditions(Subevent subevent, JsonArray conditions, RPGLContext context) throws Exception {
        boolean conditionsMet = true;
        for (int i = 0; i < conditions.size(); i++) {
            JsonObject conditionJson = conditions.getJsonObject(i);
            conditionsMet &= Condition.CONDITIONS
                    .get(conditionJson.getString("condition"))
                    .evaluate(this.getSource(), this.getTarget(), subevent, conditionJson, context);
        }
        return conditionsMet;
    }

    /**
     * This method executes a given collection of Functions on given RPGLObjects and Subevents.
     *
     * @param subevent  the Subevent being invoked
     * @param functions a collection of JSON data defining Functions
     *
     * @throws FunctionMismatchException if a Function is passed the wrong Function ID.
     */
    void executeFunctions(Subevent subevent, JsonArray functions, RPGLContext context) throws Exception {
        for (int i = 0; i < functions.size(); i++) {
            JsonObject functionJson = functions.getJsonObject(i);
            Function.FUNCTIONS
                    .get(functionJson.getString("function"))
                    .execute(this.getSource(), this.getTarget(), subevent, functionJson, context);
        }
    }

    public static RPGLObject getObject(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject instructions) throws Exception {
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
                return effectSource;
            } else if ("target".equals(object)) {
                return effectTarget;
            }
        }
        Exception e = new Exception("could not isolate an RPGLObject: " + instructions);
        LOGGER.error(e.getMessage());
        throw e;
    }

}
