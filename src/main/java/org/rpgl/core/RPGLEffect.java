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

import java.util.Map;

/**
 * This class represents anything which impacts how Subevents resolve. Conventional examples of this include status
 * conditions such as being poisoned, burning, or being unconscious, but this class is also used to facilitate many
 * background mechanics such as granting special RPGLEvents to an RPGLObject or restoring exhausted RPGLResources.
 *
 * @author Calvin Withun
 */
public class RPGLEffect extends UUIDTableElement {

    /**
     * Returns the RPGLEffect Subevent filters.
     *
     * @return a JsonObject representing the RPGLEffect object's Subevent filters
     */
    public JsonObject getSubeventFilters() {
        return this.getJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS);
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
     * @param sourceUuid the UUID of a RPGLObject
     */
    public void setSource(String sourceUuid) {
        this.putString(RPGLEffectTO.SOURCE_ALIAS, sourceUuid);
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
     * @param targetUuid the UUID of a RPGLObject
     */
    public void setTarget(String targetUuid) {
        this.putString(RPGLEffectTO.TARGET_ALIAS, targetUuid);
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
        RPGLObject source = subevent.getSource();
        RPGLObject target = subevent.getTarget();

        JsonObject subeventFilters = this.getSubeventFilters();
        for (Map.Entry<String, ?> subeventFilterEntry : subeventFilters.asMap().entrySet()) {
            if (subevent.getSubeventId().equals(subeventFilterEntry.getKey())) {
                JsonObject matchedFilter = subeventFilters.getJsonObject(subeventFilterEntry.getKey());
                JsonArray conditions = matchedFilter.getJsonArray("conditions");
                if (!subevent.hasModifyingEffect(this) && this.evaluateConditions(subevent, conditions, context)) {
                    JsonArray functionJsonArray = matchedFilter.getJsonArray("functions");
                    executeFunctions(source, target, subevent, functionJsonArray, context);
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
     * @param source    the RPGLObject which invoked a Subevent
     * @param target    the RPGLObject the Subevent is being directed at
     * @param subevent  the Subevent being invoked
     * @param functions a collection of JSON data defining Functions
     *
     * @throws FunctionMismatchException if a Function is passed the wrong Function ID.
     */
    static void executeFunctions(RPGLObject source, RPGLObject target, Subevent subevent, JsonArray functions, RPGLContext context) throws FunctionMismatchException {
        for (int i = 0; i < functions.size(); i++) {
            JsonObject functionJson = functions.getJsonObject(i);
            Function.FUNCTIONS
                    .get(functionJson.getString("function"))
                    .execute(source, target, subevent, functionJson, context);
        }
    }

}
