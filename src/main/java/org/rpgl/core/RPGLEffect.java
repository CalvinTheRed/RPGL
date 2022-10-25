package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.condition.Condition;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.function.Function;
import org.rpgl.subevent.Subevent;

/**
 * RPGLEffects are objects assigned to RPGLObjects which influence the final results of Subevents executed by or upon
 * those RPGLObjects.
 *
 * @author Calvin Withun
 */
public class RPGLEffect extends JsonObject {

    /**
     * A copy-constructor for the RPGLEffect class.
     *
     * @param data the data to be copied to this object
     */
    RPGLEffect(JsonObject data) {
        this.join(data);
    }

    public boolean processSubevent(RPGLObject source, RPGLObject target, Subevent subevent)
            throws ConditionMismatchException, FunctionMismatchException {
        JsonArray behaviorArray = (JsonArray) this.get("behavior");
        for (Object behaviorElement : behaviorArray) {
            JsonObject behavior = (JsonObject) behaviorElement;
            JsonArray conditionJsonArray = (JsonArray) behavior.get("conditions");
            if (!subevent.hasModifyingEffect(this) && this.evaluateConditions(source, target, conditionJsonArray)) {
                JsonArray functionJsonArray = (JsonArray) behavior.get("functions");
                this.executeFunctions(source, target, functionJsonArray);
                subevent.addModifyingEffect(this);
                return true;
            }
        }
        return false;
    }

    boolean evaluateConditions(RPGLObject source, RPGLObject target, JsonArray conditionJsonArray)
            throws ConditionMismatchException {
        boolean conditionsMet = true;
        for (Object conditionJsonElement : conditionJsonArray) {
            JsonObject conditionJson = (JsonObject) conditionJsonElement;
            String conditionId = (String) conditionJson.get("condition");
            Condition condition = Condition.CONDITIONS.get(conditionId);
            conditionsMet &= condition.evaluate(source, target, conditionJson);
        }
        return conditionsMet;
    }

    void executeFunctions(RPGLObject source, RPGLObject target, JsonArray functionJsonArray)
            throws FunctionMismatchException {
        for (Object functionJsonElement : functionJsonArray) {
            JsonObject functionJson = (JsonObject) functionJsonElement;
            String functionId = (String) functionJson.get("function");
            Function function = Function.FUNCTIONS.get(functionId);
            function.execute(source, target, functionJson);
        }
    }

}
