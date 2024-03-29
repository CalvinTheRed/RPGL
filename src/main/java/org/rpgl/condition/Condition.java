package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.DimensionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(Condition.class);
    private static final Stack<JsonObject> ACTIVE_CONDITIONS = new Stack<>();

    private static boolean exitingConditionLoop = false;
    private static JsonObject loopedConditionJson = null;

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
        Condition.CONDITIONS.put("check_ability_score", new CheckAbilityScore());
        Condition.CONDITIONS.put("check_distance", new CheckDistance());
        Condition.CONDITIONS.put("check_level", new CheckLevel());
        Condition.CONDITIONS.put("check_skill", new CheckSkill());
        Condition.CONDITIONS.put("entering_reach", new EnteringReach());
        Condition.CONDITIONS.put("equipped_item_has_tag", new EquippedItemHasTag());
        Condition.CONDITIONS.put("exiting_reach", new ExitingReach());
        Condition.CONDITIONS.put("includes_damage_type", new IncludesDamageType());
        Condition.CONDITIONS.put("invert", new Invert());
        Condition.CONDITIONS.put("is_objects_turn", new IsObjectsTurn());
        Condition.CONDITIONS.put("object_has_tag", new ObjectHasTag());
        Condition.CONDITIONS.put("object_wielding_origin_item", new ObjectWieldingOriginItem());
        Condition.CONDITIONS.put("objects_match", new ObjectsMatch());
        Condition.CONDITIONS.put("origin_item_has_tag", new OriginItemHasTag());
        Condition.CONDITIONS.put("origin_items_match", new OriginItemsMatch());
        Condition.CONDITIONS.put("subevent_has_tag", new SubeventHasTag());
        Condition.CONDITIONS.put("user_ids_match", new UserIdsMatch());

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
        if (!Objects.equals(this.conditionId, conditionJson.getString("condition"))) {
            ConditionMismatchException e = new ConditionMismatchException(this.conditionId, conditionJson.getString("condition"));
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * This method facilitates the evaluation of a Condition. It verifies the Condition and then runs it.
     *
     * @param effect the RPGLEffect containing this Condition
     * @param subevent a Subevent being invoked
     * @param conditionJson a JsonObject containing additional information necessary for the Condition to be evaluated
     * @param context the context in which the Condition is being invoked
     * @param originPoint the point from which the passed subevent emanates
     * @return true if the condition is satisfied. Note that if a subevent-condition loop is formed, this method will
     * return false until that loop is exited.
     *
     * @throws Exception if an exception occurs
     */
    public boolean evaluate(
            RPGLEffect effect,
            Subevent subevent,
            JsonObject conditionJson,
            RPGLContext context,
            JsonArray originPoint
    ) throws Exception {
        this.verifyCondition(conditionJson);
        if (ACTIVE_CONDITIONS.contains(conditionJson)) {
            // begin the back-out if you detect a loop
            exitingConditionLoop = true;
            loopedConditionJson = conditionJson;
            return false;
        } else {
            // else proceed as usual
            ACTIVE_CONDITIONS.push(conditionJson);
            boolean result;
            try {
                result = this.run(effect, subevent, conditionJson, context, originPoint);
            } catch (Exception e) {
                ACTIVE_CONDITIONS.pop();
                throw e;
            }
            ACTIVE_CONDITIONS.pop();

            if (exitingConditionLoop) {
                // back out and fail the condition if you are exiting a loop
                if (Objects.equals(loopedConditionJson, conditionJson)) {
                    // end the back-out if you have reached the start of the loop
                    exitingConditionLoop = false;
                    loopedConditionJson = null;
                }
                return false;
            }
            return result;
        }
    }

    /**
     * This method contains the logic definitive of the Condition.
     *
     * @param effect the RPGLEffect containing this Condition
     * @param subevent a Subevent being invoked
     * @param conditionJson a JsonObject containing additional information necessary for the Condition to be evaluated
     * @param context the context in which the Condition is being invoked
     * @param originPoint the point from which the passed subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    public abstract boolean run(
            RPGLEffect effect,
            Subevent subevent,
            JsonObject conditionJson,
            RPGLContext context,
            JsonArray originPoint
    ) throws Exception;

    // =================================================================================================================
    // Condition helper methods
    // =================================================================================================================

    /**
     * This helper method compares two integer values in accordance with a specified comparison operator.
     *
     * @param value the double being compared to another value
     * @param target the double being compared against
     * @param comparison the operator being used for the comparison (<code>"=", "<", "<=", ">", ">="</code>)
     * @return true if the comparison is satisfied
     *
     * @throws Exception if an invalid comparison operator is provided
     */
    public static boolean compareValues(double value, double target, String comparison) throws Exception {
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

    /**
     * Finds the distance between two N-dimensional points. pos1 and pos2 must be of the same degree.
     *
     * @param pos1 a point in coordinate space
     * @param pos2 a point in coordinate space
     * @param algorithm the algorithm to measure distance (<code>"direct"</code> or <code>"taxicab"</code>)
     * @return the distance between the two passed points
     *
     * @throws DimensionMismatchException if the passed points have different degrees
     */
    public static double getDistance(JsonArray pos1, JsonArray pos2, String algorithm) throws DimensionMismatchException {
        if (pos1.size() == pos2.size()) {
            return switch (algorithm) {
                case "direct" -> getDirectDistance(pos1, pos2);
                case "taxicab" -> getTaxicabDistance(pos1, pos2);
                default -> -1;
            };
        } else {
            throw new DimensionMismatchException(pos1, pos2);
        }
    }

    /**
     * This helper method calculates the distance between two points by the shortest distance.
     *
     * @param pos1 a point in coordinate space
     * @param pos2 a point in coordinate space
     * @return the direct distance between the two passed points.
     */
    private static double getDirectDistance(JsonArray pos1, JsonArray pos2) {
        double sum = 0d;
        for (int i = 0; i < pos1.size(); i++) {
            sum += Math.pow((pos1.getDouble(i) - pos2.getDouble(i)), 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * This helper method calculates the distance between two points via taxicab distance.
     *
     * @param pos1 a point in coordinate space
     * @param pos2 a point in coordinate space
     * @return the taxicab distance between the two passed points.
     */
    private static double getTaxicabDistance(JsonArray pos1, JsonArray pos2) {
        double sum = 0d;
        for (int i = 0; i < pos1.size(); i++) {
            sum += Math.abs(pos1.getDouble(i) - pos2.getDouble(i));
        }
        return sum;
    }

}
