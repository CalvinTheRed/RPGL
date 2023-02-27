package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an abstract condition which must be met in order for an RPGLEffect to execute its Functions on
 * a Subevent. Most RPGLEffects will include several Condition references.
 *
 * @author Calvin Withun
 */
public abstract class Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(Condition.class);

    /**
     * A map of all Conditions which can be used in the JSON of an RPGLEffect.
     */
    public static final Map<String, Condition> CONDITIONS = new HashMap<>();

    final String conditionId;

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
        Condition.CONDITIONS.put("objects_match", new ObjectsMatch());
        Condition.CONDITIONS.put("subevent_has_tag", new SubeventHasTag());

        if (includeTestingConditions) {
            Condition.CONDITIONS.put("false", new False());
            Condition.CONDITIONS.put("true", new True());
        }
    }

    public Condition(String conditionId) {
        this.conditionId = conditionId;
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
            ConditionMismatchException e = new ConditionMismatchException(expected, conditionJson.getString("condition"));
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Evaluates a Subevent or RPGLObject to determine if a defined condition is satisfied.
     *
     * @param effectSource  the RPGLObject sourcing the RPGLEffect being considered
     * @param effectTarget  the RPGLObject targeted by the RPGLEffect being considered
     * @param subevent      the Subevent being invoked
     * @param conditionJson a JsonObject containing additional information necessary for the condition to be evaluated
     * @return the result of the evaluation
     *
     * @throws ConditionMismatchException if conditionJson is for a different condition than the one being evaluated
     */
    public abstract boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception;

    // =================================================================================================================
    // Condition helper methods
    // =================================================================================================================

    RPGLObject getObject(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject instructions) throws Exception {
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

    boolean compare(int value, int target, String comparison) throws Exception {
        if ("=".equals(comparison)) {
            return value == target;
        } else if ("<".equals(comparison)) {
            return value < target;
        } else if (">".equals(comparison)) {
            return value > target;
        } else if ("<=".equals(comparison)) {
            return value <= target;
        } else if (">=".equals(comparison)) {
            return value >= target;
        }

        Exception e = new Exception("Illegal comparison value: " + comparison);
        LOGGER.error(e.getMessage());
        throw e;
    }

}
