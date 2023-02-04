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

public class RPGLEffect extends UUIDTableElement {

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
     *  @param subevent a Subevent
     *
     *  @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     *  @throws FunctionMismatchException  if a Function is passed the wrong Function ID.
     */
    public boolean processSubevent(Subevent subevent) throws ConditionMismatchException, FunctionMismatchException {
        RPGLObject source = subevent.getSource();
        RPGLObject target = subevent.getTarget();

        JsonObject subeventFilters = this.getJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS);
        for (Map.Entry<String, Object> subeventFilterEntry : subeventFilters.asMap().entrySet()) {
            if (subevent.getSubeventId().equals(subeventFilterEntry.getKey())) {
                JsonObject matchedFilter = subeventFilters.getJsonObject(subeventFilterEntry.getKey());
                JsonArray conditions = matchedFilter.getJsonArray("conditions");
                if (!subevent.hasModifyingEffect(this) && evaluateConditions(source, target, conditions)) {
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
     * 	<p><b><i>setSource</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setSource(String sourceUuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method sets the source of the RPGLEffect.
     * 	</p>
     *
     *  @param sourceUuid the UUID of a RPGLObject
     */
    public void setSource(String sourceUuid) {
        this.putString(RPGLEffectTO.SOURCE_ALIAS, sourceUuid);
    }

    /**
     * 	<p><b><i>setTarget</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setTarget(String targetUuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method sets the target of the RPGLEffect.
     * 	</p>
     *
     *  @param targetUuid the UUID of a RPGLObject
     */
    public void setTarget(String targetUuid) {
        this.putString(RPGLEffectTO.TARGET_ALIAS, targetUuid);
    }

    /**
     * 	<p><b><i>getSource</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void getSource()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the source of the RPGLEffect.
     * 	</p>
     *
     *  @return a RPGLObject UUID
     */
    public String getSource() {
        return this.getString(RPGLEffectTO.SOURCE_ALIAS);
    }

    /**
     * 	<p><b><i>getTarget</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void getTarget()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the target of the RPGLEffect.
     * 	</p>
     *
     *  @return a RPGLObject UUID
     */
    public String getTarget() {
        return this.getString(RPGLEffectTO.TARGET_ALIAS);
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
     *  @param source     a RPGLObject invoking a Subevent
     *  @param target     a RPGLObject targeted by a Subevent
     *  @param conditions a collection of JSON data defining Conditions
     *
     *  @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     */
    static boolean evaluateConditions(RPGLObject source, RPGLObject target, JsonArray conditions) throws ConditionMismatchException {
        boolean conditionsMet = true;
        for (int i = 0; i < conditions.size(); i++) {
            JsonObject conditionJson = conditions.getJsonObject(i);
            conditionsMet &= Condition.CONDITIONS
                    .get(conditionJson.getString("condition"))
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
     *  @param source    a RPGLObject invoking a Subevent
     *  @param target    a RPGLObject targeted by a Subevent
     *  @param subevent  a Subevent
     *  @param functions a collection of JSON data defining Functions
     *
     *  @throws FunctionMismatchException if a Function is passed the wrong Function ID.
     */
    static void executeFunctions(RPGLObject source, RPGLObject target, Subevent subevent, JsonArray functions)
            throws FunctionMismatchException {
        for (int i = 0; i < functions.size(); i++) {
            JsonObject functionJson = functions.getJsonObject(i);
            Function.FUNCTIONS
                    .get(functionJson.getString("function"))
                    .execute(source, target, subevent, functionJson);
        }
    }

}
