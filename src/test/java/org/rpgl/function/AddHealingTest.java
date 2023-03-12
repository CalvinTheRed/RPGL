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
import org.rpgl.subevent.HealingCollection;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddHealing class.
 *
 * @author Calvin Withun
 */
public class AddHealingTest {

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
        Function function = new AddHealing();
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
    @DisplayName("unpackCompactedDamageDice returns damage with unpacked damage dice")
    void unpackHealing_returnsHealingWithUnpackedHealingDice() {
        AddHealing addHealing = new AddHealing();
        JsonObject healing = new JsonObject() {{
            /*{
                "dice": [
                    { "count": 2, "size": 6, "determined": [ 3 ] },
                    { "count": 2, "size": 10, "determined": [ 5 ] },
                ],
                "bonus": 2
            }*/
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("count", 2);
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(3);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("count", 2);
                    this.putInteger("size", 10);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(5);
                    }});
                }});
            }});
            this.putInteger("bonus", 2);
        }};

        String expected = """
                {"bonus":2,"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[5],"size":10},{"determined":[5],"size":10}]}""";
        assertEquals(expected, addHealing.unpackHealing(healing).toString(),
                "unpackHealing should return an object with all healing dice unpacked"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent")
    void execute_addsHealingToSubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context);

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": {
                    "dice": [
                        { "count": 1, "size": 6, "determined": [ 3 ] }
                    ],
                    "bonus": 2
                }
           }*/
            this.putString("function", "add_healing");
            this.putJsonObject("healing", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("count", 1);
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                }});
                this.putInteger("bonus", 2);
            }});
        }};

        addHealing.execute(null, healingCollection, functionJson, context);

        String expected = """
                {"bonus":2,"dice":[{"determined":[3],"size":6}]}""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add healing to HealingCollection subevent"
        );
    }

}
