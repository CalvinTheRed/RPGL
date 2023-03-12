package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingRoll;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.RerollHealingDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class RerollHealingDiceMatchingOrBelowTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        RPGLCore.initializeTesting();
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new RerollHealingDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute re-rolls all dice at or below two")
    void execute_rerollsAllDiceAtOrBelowTwo() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "dice": [
                    { "size": 6, "determined": [ 1, 6 ] },
                    { "size": 6, "determined": [ 2, 6 ] },
                    { "size": 6, "determined": [ 3, 6 ] },
                    { "size": 6, "determined": [ 4, 6 ] }
                ],
                "bonus": 2
            }*/
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(1);
                        this.addInteger(6);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(2);
                        this.addInteger(6);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(3);
                        this.addInteger(6);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(4);
                        this.addInteger(6);
                    }});
                }});
            }});
            this.putInteger("bonus", 2);
        }});
        healingRoll.setSource(source);
        healingRoll.prepare(context);
        healingRoll.setTarget(target);

        RerollHealingDiceMatchingOrBelow rerollHealingDiceMatchingOrBelow = new RerollHealingDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "reroll_healing_dice_matching_or_below",
                "threshold": 2
            }*/
            this.putString("function", "reroll_healing_dice_matching_or_below");
            this.putInteger("threshold", 2);
        }};

        rerollHealingDiceMatchingOrBelow.execute(null, healingRoll, functionJson, context);

        String expected = """
                {"bonus":2,"dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6},{"determined":[6],"roll":3,"size":6},{"determined":[6],"roll":4,"size":6}]}""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "execute should re-roll all dice which rolled 2 or lower to 3"
        );
    }

}