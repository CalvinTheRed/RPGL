package org.rpgl.function;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.subevent.Subevent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by an RPGLEffect in order to change the fallout of a Subevent or to precipitate a new Subevent.
 *
 * @author Calvin Withun
 */
public abstract class Function {

    /**
     * A map of all Functions which can be used in the JSON of an RPGLEffect.
     */
    public static final Map<String, Function> FUNCTIONS;

    static {
        FUNCTIONS = new HashMap<>();
        Function.FUNCTIONS.put("dummy_function", new DummyFunction());
    }

    /**
     * 	<p><b><i>verifyFunction</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void verifyFunction(String expected, JsonObject conditionJson)
     * 	throws FunctionMismatchException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Verifies that the additional information provided to <code>execute(...)</code> is intended for the Function
     * 	type being executed.
     * 	</p>
     *
     * 	@param expected     the expected functionId
     *  @param functionJson a JsonObject containing additional information necessary for the function to be executed
     * 	@throws FunctionMismatchException if functionJson is for a different function than the one being executed
     */
    void verifyFunction(String expected, JsonObject functionJson) throws FunctionMismatchException {
        if (!expected.equals(functionJson.get("function"))) {
            throw new FunctionMismatchException(expected, (String) functionJson.get("function"));
        }
    }

    /**
     * 	<p><b><i>execute</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public abstract void execute(RPGLObject source, RPGLObject target, JsonObject functionJson)
     * 	throws FunctionMismatchException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Modifies given Subevents or RPGLObjects according to given parameters.
     * 	</p>
     *
     *  @param source       the RPGLObject which invoked a Subevent
     *  @param target       the RPGLObject the Subevent is being directed at
     *  @param subevent     the Subevent being invoked
     *  @param functionJson a JsonObject containing additional information necessary for the function to be executed
     * 	@throws FunctionMismatchException if functionJson is for a different function than the one being executed
     */
    public abstract void execute(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject functionJson) throws FunctionMismatchException;

}
