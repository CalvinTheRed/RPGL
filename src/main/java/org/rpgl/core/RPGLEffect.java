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
     * 	<p><b><i>RPGLEffect</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * RPGLEffect(JsonObject effectJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	A copy-constructor for the RPGLEffect class.
     * 	</p>
     *
     * @param effectJson the data to be joined to the new RPGLEffect
     */
    RPGLEffect(JsonObject effectJson) {
        this.join(effectJson);
    }

    /**
     * 	<p><b><i>processSubevent</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean processSubevent(Subevent subevent)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method checks the passed Subevent against the RPGLEffect's Conditions, and if they evaluate true, the
     * 	RPGLEffect executes its functions.
     * 	</p>
     *
     * @param subevent a Subevent
     */
    public boolean processSubevent(Subevent subevent)
            throws ConditionMismatchException, FunctionMismatchException {
        // TODO make "behavior" an object with keys for subeventId's to save on a Subevent... harder to test though?
        RPGLObject source = subevent.getSource();
        RPGLObject target = subevent.getTarget();
        JsonArray behaviors = (JsonArray) this.get("behavior");
        for (Object behaviorElement : behaviors) {
            JsonObject behavior = (JsonObject) behaviorElement;
            JsonArray conditions = (JsonArray) behavior.get("conditions");
            if (!subevent.hasModifyingEffect(this) && evaluateConditions(source, target, conditions)) {
                JsonArray functionJsonArray = (JsonArray) behavior.get("functions");
                executeFunctions(source, target, subevent, functionJsonArray);
                subevent.addModifyingEffect(this);
                return true;
            }
        }
        return false;
    }

    /**
     * 	<p><b><i>evaluateConditions</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static boolean evaluateConditions(RPGLObject source, RPGLObject target, JsonArray conditions)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method evaluates a given collection of Conditions on given RPGLObject sources and targets.
     * 	</p>
     *
     * @param source     a RPGLObject invoking a Subevent
     * @param target     a RPGLObject targeted by a Subevent
     * @param conditions a collection of JSON data defining Conditions
     */
    static boolean evaluateConditions(RPGLObject source, RPGLObject target, JsonArray conditions)
            throws ConditionMismatchException {
        boolean conditionsMet = true;
        for (Object conditionJsonElement : conditions) {
            JsonObject conditionJson = (JsonObject) conditionJsonElement;
            conditionsMet &= Condition.CONDITIONS
                    .get((String) conditionJson.get("condition"))
                    .evaluate(source, target, conditionJson);
        }
        return conditionsMet;
    }

    /**
     * 	<p><b><i>executeFunctions</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void executeFunctions(RPGLObject source, RPGLObject target, JsonArray functions)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method executes a given collection of Functions on given RPGLObjects and Subevents.
     * 	</p>
     *
     * @param source    a RPGLObject invoking a Subevent
     * @param target    a RPGLObject targeted by a Subevent
     * @param subevent  a Subevent
     * @param functions a collection of JSON data defining Functions
     */
    static void executeFunctions(RPGLObject source, RPGLObject target, Subevent subevent, JsonArray functions)
            throws FunctionMismatchException {
        for (Object functionJsonElement : functions) {
            JsonObject functionJson = (JsonObject) functionJsonElement;
            Function.FUNCTIONS
                    .get((String) functionJson.get("function"))
                    .execute(source, target, subevent, functionJson);
        }
    }

}
