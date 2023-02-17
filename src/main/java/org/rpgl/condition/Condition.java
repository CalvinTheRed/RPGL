package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an abstract condition which must be met in order for an RPGLEffect to execute its Functions on
 * a Subevent. Most RPGLEffects will include several Condition references.
 *
 * @author Calvin Withun
 */
public abstract class Condition {

    /**
     * A map of all Conditions which can be used in the JSON of an RPGLEffect.
     */
    public static final Map<String, Condition> CONDITIONS = new HashMap<>();

    /**
     * This method populates Condition.CONDITIONS.
     *
     * @param includeTestingConditions whether testing-only Conditions should be loaded into RPGL
     */
    public static void initialize(boolean includeTestingConditions) {
        Condition.CONDITIONS.clear();

        Condition.CONDITIONS.put("all", new All());
        Condition.CONDITIONS.put("any", new Any());
        Condition.CONDITIONS.put("invert", new Invert());

        if (includeTestingConditions) {
            Condition.CONDITIONS.put("false", new False());
            Condition.CONDITIONS.put("true", new True());
        }
    }

    /**
     * Verifies that the additional information provided to <code>evaluate(...)</code> is intended for the Condition
     * 	type being evaluated.
     *
     * @param expected      the expected conditionId
     * @param conditionJson a JsonObject containing additional information necessary for the condition to be evaluated
     *
     * @throws ConditionMismatchException if conditionJson is for a different condition than the one being evaluated
     */
    void verifyCondition(String expected, JsonObject conditionJson) throws ConditionMismatchException {
        if (!expected.equals(conditionJson.getString("condition"))) {
            throw new ConditionMismatchException(expected, conditionJson.getString("condition"));
        }
    }

    /**
     * Evaluates a Subevent or RPGLObject to determine if a defined condition is satisfied.
     *
     * @param source        the RPGLObject which invoked a Subevent
     * @param target        the RPGLObject the Subevent is being directed at
     * @param subevent      the Subevent being invoked
     * @param conditionJson a JsonObject containing additional information necessary for the condition to be evaluated
     * @return the result of the evaluation
     *
     * @throws ConditionMismatchException if conditionJson is for a different condition than the one being evaluated
     */
    public abstract boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException;

}
