package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by an RPGLEffect in order to change the fallout of a Subevent or to precipitate a new Subevent.
 *
 * @author Calvin Withun
 */
public abstract class Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(Function.class);

    /**
     * A map of all Functions which can be used in the JSON of an RPGLEffect.
     */
    public static final Map<String, Function> FUNCTIONS = new HashMap<>();

    /**
     * This method populates Function.FUNCTIONS.
     *
     * @param includeTestingFunctions whether testing-only Functions should be loaded into RPGL
     */
    public static void initialize(boolean includeTestingFunctions) {
        Function.FUNCTIONS.clear();

        if (includeTestingFunctions) {
            Function.FUNCTIONS.put("dummy_function", new DummyFunction());
        }
    }

    /**
     * Verifies that the additional information provided to <code>execute(...)</code> is intended for the Function
     * type being executed.
     *
     * @param expected     the expected functionId
     * @param functionJson a JsonObject containing additional information necessary for the function to be executed
     *
     * @throws FunctionMismatchException if functionJson is for a different function than the one being executed
     */
    void verifyFunction(String expected, JsonObject functionJson) throws FunctionMismatchException {
        if (!expected.equals(functionJson.getString("function"))) {
            FunctionMismatchException e = new FunctionMismatchException(expected, functionJson.getString("function"));
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Modifies given Subevents or RPGLObjects according to given parameters.
     *
     * @param source       the RPGLObject which invoked a Subevent
     * @param target       the RPGLObject the Subevent is being directed at
     * @param subevent     the Subevent being invoked
     * @param functionJson a JsonObject containing additional information necessary for the function to be executed
     *
     * @throws FunctionMismatchException if functionJson is for a different function than the one being executed
     */
    public abstract void execute(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject functionJson, RPGLContext context) throws FunctionMismatchException;

}
