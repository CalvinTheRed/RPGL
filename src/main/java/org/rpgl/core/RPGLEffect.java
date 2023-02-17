package org.rpgl.core;

import org.rpgl.condition.Condition;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.function.Function;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
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

    public JsonObject getSubeventFilters() {
        return this.getJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS);
    }

    /**
     * This method returns the source of the RPGLEffect.
     *
     * @return a RPGLObject UUID
     */
    public String getSource() {
        return this.getString(RPGLEffectTO.SOURCE_ALIAS);
    }

    /**
     * This method sets the source of the RPGLEffect.
     *
     * @param sourceUuid the UUID of a RPGLObject
     */
    public void setSource(String sourceUuid) {
        this.putString(RPGLEffectTO.SOURCE_ALIAS, sourceUuid);
    }

    /**
     * This method returns the target of the RPGLEffect.
     *
     * @return a RPGLObject UUID
     */
    public String getTarget() {
        return this.getString(RPGLEffectTO.TARGET_ALIAS);
    }

    /**
     * This method sets the target of the RPGLEffect.
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
     *	RPGLEffect executes its functions.
     *
     * @param subevent a Subevent
     * @return true if the Subevent was present in this object's subevent filter and if the Conditions in that filter
     *         were satisfied
     *
     * @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     * @throws FunctionMismatchException  if a Function is passed the wrong Function ID.
     */
    public boolean processSubevent(Subevent subevent) throws ConditionMismatchException, FunctionMismatchException {
        RPGLObject source = subevent.getSource();
        RPGLObject target = subevent.getTarget();

        JsonObject subeventFilters = this.getSubeventFilters();
        for (Map.Entry<String, ?> subeventFilterEntry : subeventFilters.asMap().entrySet()) {
            if (subevent.getSubeventId().equals(subeventFilterEntry.getKey())) {
                JsonObject matchedFilter = subeventFilters.getJsonObject(subeventFilterEntry.getKey());
                JsonArray conditions = matchedFilter.getJsonArray("conditions");
                if (!subevent.hasModifyingEffect(this) && evaluateConditions(source, target, subevent, conditions)) {
                    JsonArray functionJsonArray = matchedFilter.getJsonArray("functions");
                    executeFunctions(source, target, subevent, functionJsonArray);
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
     * @param source     the RPGLObject which invoked a Subevent
     * @param target     the RPGLObject the Subevent is being directed at
     * @param subevent   the Subevent being invoked
     * @param conditions a collection of JSON data defining Conditions
     * @return true if any Conditions evaluated to true
     *
     * @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     */
    static boolean evaluateConditions(RPGLObject source, RPGLObject target, Subevent subevent, JsonArray conditions) throws ConditionMismatchException {
        boolean conditionsMet = true;
        for (int i = 0; i < conditions.size(); i++) {
            JsonObject conditionJson = conditions.getJsonObject(i);
            conditionsMet &= Condition.CONDITIONS
                    .get(conditionJson.getString("condition"))
                    .evaluate(source, target, subevent, conditionJson);
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
    static void executeFunctions(RPGLObject source, RPGLObject target, Subevent subevent, JsonArray functions) throws FunctionMismatchException {
        for (int i = 0; i < functions.size(); i++) {
            JsonObject functionJson = functions.getJsonObject(i);
            Function.FUNCTIONS
                    .get(functionJson.getString("function"))
                    .execute(source, target, subevent, functionJson);
        }
    }

}
