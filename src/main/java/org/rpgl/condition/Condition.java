package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
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

    // TODO consider adding checks in each Condition if it has a risk of creating circular calls to itself?
    //  ex an effect watches for CalculateAbilityScore, and has a CheckAbility Condition for the same ability

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
        Condition.CONDITIONS.put("check_ability", new CheckAbility());
        Condition.CONDITIONS.put("check_damage_type", new CheckDamageType());
        Condition.CONDITIONS.put("invert", new Invert());
        Condition.CONDITIONS.put("is_objects_turn", new IsObjectsTurn());
        Condition.CONDITIONS.put("object_ability_score_comparison", new ObjectAbilityScoreComparison());
        Condition.CONDITIONS.put("object_has_tag", new ObjectHasTag());
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
     * type being evaluated.
     *
     * @param conditionJson a JsonObject containing additional information necessary for the condition to be evaluated
     *
     * @throws ConditionMismatchException if conditionJson is for a different condition than the one being evaluated
     */
    void verifyCondition(JsonObject conditionJson) throws ConditionMismatchException {
        if (!this.conditionId.equals(conditionJson.getString("condition"))) {
            ConditionMismatchException e = new ConditionMismatchException(this.conditionId, conditionJson.getString("condition"));
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * This method facilitates the evaluation of a Condition. It verifies the Condition and then runs it.
     *
     * @param effect        the RPGLEffect for which the Condition is being evaluated
     * @param subevent      the Subevent for which the Condition is being evaluated
     * @param conditionJson the json definition for the Condition
     * @param context       the context in which the Condition is being invoked
     *
     * @throws Exception if an exception occurs
     */
    public boolean evaluate(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        this.verifyCondition(conditionJson);
        return this.run(effect, subevent, conditionJson, context);
    }

    /**
     * This method contains the logic definitive of the Condition.
     *
     * @param effect        the RPGLEffect for which the Condition is being evaluated
     * @param subevent      the Subevent for which the Condition is being evaluated
     * @param conditionJson the json definition for the Condition
     * @param context       the context in which the Condition is being invoked
     *
     * @throws Exception if an exception occurs
     */
    public abstract boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception;

    // =================================================================================================================
    // Condition helper methods
    // =================================================================================================================

    /**
     * This helper method compares two integer values in accordance with a specified comparison operator.
     *
     * @param value      the int being compared to another value
     * @param target     the int being compared against
     * @param comparison the operator being used for the comparison (<code>"=", "<", "<=", ">", ">="</code>)
     * @return true if the comparison is satisfied
     *
     * @throws Exception if an invalid comparison operator is provided
     */
    boolean compareValues(int value, int target, String comparison) throws Exception {
        switch(comparison) {
            case "=":
                return value == target;
            case ">":
                return value > target;
            case "<":
                return value < target;
            case ">=":
                return value >= target;
            case "<=":
                return value <= target;
            default: {
                Exception e = new Exception("Illegal comparison value: " + comparison);
                LOGGER.error(e.getMessage());
                throw e;
            }
        }
    }

}
