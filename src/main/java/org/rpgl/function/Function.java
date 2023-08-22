package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public final String functionId;

    /**
     * This method populates Function.FUNCTIONS.
     *
     * @param includeTestingFunctions whether testing-only Functions should be loaded into RPGL
     */
    public static void initialize(boolean includeTestingFunctions) {
        Function.FUNCTIONS.clear();

        Function.FUNCTIONS.put("add_attack_ability", new AddAttackAbility());
        Function.FUNCTIONS.put("add_bonus", new AddBonus());
        Function.FUNCTIONS.put("add_damage", new AddDamage());
        Function.FUNCTIONS.put("add_event", new AddEvent());
        Function.FUNCTIONS.put("add_healing", new AddHealing());
        Function.FUNCTIONS.put("add_object_tag", new AddObjectTag());
        Function.FUNCTIONS.put("add_subevent_tag", new AddSubeventTag());
        Function.FUNCTIONS.put("add_temporary_hit_points", new AddTemporaryHitPoints());
        Function.FUNCTIONS.put("end_effect", new EndEffect());
        Function.FUNCTIONS.put("grant_advantage", new GrantAdvantage());
        Function.FUNCTIONS.put("grant_disadvantage", new GrantDisadvantage());
        Function.FUNCTIONS.put("grant_immunity", new GrantImmunity());
        Function.FUNCTIONS.put("grant_resistance", new GrantResistance());
        Function.FUNCTIONS.put("grant_vulnerability", new GrantVulnerability());
        Function.FUNCTIONS.put("invoke_subevent", new InvokeSubevent());
        Function.FUNCTIONS.put("maximize_damage", new MaximizeDamage());
        Function.FUNCTIONS.put("maximize_healing", new MaximizeHealing());
        Function.FUNCTIONS.put("maximize_temporary_hit_points", new MaximizeTemporaryHitPoints());
        Function.FUNCTIONS.put("reroll_damage_dice_matching_or_below", new RerollDamageDiceMatchingOrBelow());
        Function.FUNCTIONS.put("reroll_healing_dice_matching_or_below", new RerollHealingDiceMatchingOrBelow());
        Function.FUNCTIONS.put("revoke_immunity", new RevokeImmunity());
        Function.FUNCTIONS.put("revoke_resistance", new RevokeResistance());
        Function.FUNCTIONS.put("revoke_vulnerability", new RevokeVulnerability());
        Function.FUNCTIONS.put("set_base", new SetBase());
        Function.FUNCTIONS.put("set_damage_dice_matching_or_below", new SetDamageDiceMatchingOrBelow());
        Function.FUNCTIONS.put("set_healing_dice_matching_or_below", new SetHealingDiceMatchingOrBelow());
        Function.FUNCTIONS.put("set_minimum", new SetMinimum());

        if (includeTestingFunctions) {
            Function.FUNCTIONS.put("dummy_function", new DummyFunction());
        }
    }

    public Function(String functionId) {
        this.functionId = functionId;
    }

    /**
     * Verifies that the additional information provided to <code>execute(...)</code> is intended for the Function
     * type being executed.
     *
     * @param functionJson a JsonObject containing additional information necessary for the function to be executed
     *
     * @throws FunctionMismatchException if functionJson is for a different function than the one being executed
     */
    void verifyFunction(JsonObject functionJson) throws FunctionMismatchException {
        if (!Objects.equals(this.functionId, functionJson.getString("function"))) {
            FunctionMismatchException e = new FunctionMismatchException(this.functionId, functionJson.getString("function"));
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * This method facilitates the execution of a Function. It verifies the Function and then runs it.
     *
     * @param effect       the RPGLEffect containing this Function
     * @param subevent     a Subevent being invoked
     * @param functionJson a JsonObject containing additional information necessary for the Function to be executed
     * @param context      the context in which the Function is being executed
     *
     * @throws Exception if an exception occurs
     */
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        this.verifyFunction(functionJson);
        this.run(effect, subevent, functionJson, context);
    }

    /**
     * This method contains the logic definitive of the Function.
     *
     * @param effect       the RPGLEffect containing this Function
     * @param subevent     a Subevent being invoked
     * @param functionJson a JsonObject containing additional information necessary for the Function to be executed
     * @param context      the context in which the Function is being executed
     *
     * @throws Exception if an exception occurs
     */
    public abstract void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception;

}
