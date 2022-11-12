package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by an RPGLEffect in order to determine whether it should execute its Functions when an RPGLObject
 * invokes a Subevent.
 *
 * @author Calvin Withun
 */
public abstract class Condition {

    /**
     * A map of all Conditions which can be used in the JSON of an RPGLEffect.
     */
    public static final Map<String, Condition> CONDITIONS;

    static {
        CONDITIONS = new HashMap<>();
        Condition.CONDITIONS.put("all", new All());
        Condition.CONDITIONS.put("any", new Any());
        Condition.CONDITIONS.put("false", new False());
        Condition.CONDITIONS.put("invert", new Invert());
        Condition.CONDITIONS.put("true", new True());
    }

    /**
     * 	<p><b><i>verifyCondition</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void verifyCondition(String expected, JsonObject conditionJson)
     * 	throws ConditionMismatchException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Verifies that the additional information provided to <code>evaluate(...)</code> is intended for the Condition
     * 	type being evaluated.
     * 	</p>
     *
     * 	@param expected      the expected conditionId
     *  @param conditionJson a JsonObject containing additional information necessary for the condition to be evaluated
     * 	@throws ConditionMismatchException if conditionJson is for a different condition than the one being evaluated
     */
    void verifyCondition(String expected, JsonObject conditionJson) throws ConditionMismatchException {
        if (!expected.equals(conditionJson.get("condition"))) {
            throw new ConditionMismatchException(expected, (String) conditionJson.get("condition"));
        }
    }

    /**
     * 	<p><b><i>evaluate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public abstract boolean evaluate(RPGLObject source, RPGLObject target, JsonObject conditionJson)
     * 	throws ConditionMismatchException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Evaluates a Subevent or RPGLObject to determine if a defined condition is satisfied.
     * 	</p>
     *
     * 	@param source        the RPGLObject which invoked a Subevent
     *  @param target        the RPGLObject the Subevent is being directed at
     *  @param conditionJson a JsonObject containing additional information necessary for the condition to be evaluated
     * 	@return the result of the evaluation
     * 	@throws ConditionMismatchException if conditionJson is for a different condition than the one being evaluated
     */
    public abstract boolean evaluate(RPGLObject source, RPGLObject target, JsonObject conditionJson) throws ConditionMismatchException;

}
