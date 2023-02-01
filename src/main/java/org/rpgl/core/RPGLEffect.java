package org.rpgl.core;

import org.rpgl.condition.Condition;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.datapack.UUIDTableElementTO;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.function.Function;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTableElement;

import java.util.List;
import java.util.Map;

public class RPGLEffect extends JsonObject implements UUIDTableElement {

    @Override
    public String getUuid() {
        return this.getString(UUIDTableElementTO.UUID_ALIAS);
    }

    @Override
    public void setUuid(String uuid) {
        this.put(UUIDTableElementTO.UUID_ALIAS, uuid);
    }

    @Override
    public void deleteUuid() {
        this.put(UUIDTableElementTO.UUID_ALIAS, null);
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
     *  @param subevent a Subevent
     *
     *  @throws ConditionMismatchException if a Condition is passed the wrong Condition ID.
     *  @throws FunctionMismatchException  if a Function is passed the wrong Function ID.
     */
    public boolean processSubevent(Subevent subevent) throws ConditionMismatchException, FunctionMismatchException {
        RPGLObject source = subevent.getSource();
        RPGLObject target = subevent.getTarget();

        Map<String, Object> subeventFilters = this.getMap(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS);
        for (Map.Entry<String, Object> subeventFilterEntry : subeventFilters.entrySet()) {
            if (subevent.getSubeventId().equals(subeventFilterEntry.getKey())) {
                Map<String, Object> matchedFilter = (Map<String, Object>) subeventFilterEntry.getValue();
                List<Object> conditions = (List<Object>) matchedFilter.get("conditions");
                if (!subevent.hasModifyingEffect(this) && evaluateConditions(source, target, conditions)) {
                    List<Object> functionJsonArray = (List<Object>) matchedFilter.get("functions");
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
        this.put(RPGLEffectTO.SOURCE_ALIAS, sourceUuid);
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
        this.put(RPGLEffectTO.TARGET_ALIAS, targetUuid);
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
        return (String) this.get(RPGLEffectTO.SOURCE_ALIAS);
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
        return (String) this.get(RPGLEffectTO.TARGET_ALIAS);
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
    static boolean evaluateConditions(RPGLObject source, RPGLObject target, List<Object> conditions) throws ConditionMismatchException {
        boolean conditionsMet = true;
        for (Object conditionJsonElement : conditions) {
            Map<String, Object> conditionJson = (Map<String, Object>) conditionJsonElement;
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
     *  @param source    a RPGLObject invoking a Subevent
     *  @param target    a RPGLObject targeted by a Subevent
     *  @param subevent  a Subevent
     *  @param functions a collection of JSON data defining Functions
     *
     *  @throws FunctionMismatchException if a Function is passed the wrong Function ID.
     */
    static void executeFunctions(RPGLObject source, RPGLObject target, Subevent subevent, List<Object> functions)
            throws FunctionMismatchException {
        for (Object functionJsonElement : functions) {
            JsonObject functionJson = (JsonObject) functionJsonElement;
            Function.FUNCTIONS
                    .get((String) functionJson.get("function"))
                    .execute(source, target, subevent, functionJson);
        }
    }

}
