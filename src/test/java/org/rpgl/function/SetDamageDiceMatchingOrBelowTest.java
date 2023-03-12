package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.rpgl.subevent.DamageRoll;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.SetDamageDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class SetDamageDiceMatchingOrBelowTest {

    private DamageRoll damageRoll;

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

    @BeforeEach
    void beforeEach() {
        damageRoll = new DamageRoll();
        damageRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 2 ] },
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    },{
                        "type": "cold",
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 2 ] },
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new SetDamageDiceMatchingOrBelow();
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
    @DisplayName("execute sets all dice at or below two to three (fire only)")
    void execute_setsAllDiceAtOrBelowTwoToThree_fireOnly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        damageRoll.setSource(source);
        damageRoll.prepare(context);
        damageRoll.setTarget(target);

        SetDamageDiceMatchingOrBelow setDamageDiceMatchingOrBelow = new SetDamageDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_damage_dice_matching_or_below",
                "threshold": 2,
                "set": 3,
                "type": "fire"
            }*/
            this.putString("function", "set_damage_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putInteger("set", 3);
            this.putString("type", "fire");
        }};

        setDamageDiceMatchingOrBelow.execute(null, damageRoll, functionJson, context);

        String expected = """
                {"cold":10,"fire":13}""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should set all fire damage dice which rolled 2 or lower to 3 (cold=1+2+3+4=10, fire=3+3+3+4=13)"
        );
    }

    @Test
    @DisplayName("execute sets all dice at or below two to three (all damage types)")
    void execute_setsAllDiceAtOrBelowTwoToThree_allDamageTypes() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        damageRoll.setSource(source);
        damageRoll.prepare(context);
        damageRoll.setTarget(target);

        SetDamageDiceMatchingOrBelow setDamageDiceMatchingOrBelow = new SetDamageDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_damage_dice_matching_or_below",
                "threshold": 2,
                "set": 3,
                "type": ""
            }*/
            this.putString("function", "set_damage_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putInteger("set", 3);
            this.putString("type", "");
        }};

        setDamageDiceMatchingOrBelow.execute(null, damageRoll, functionJson, context);

        String expected = """
                {"cold":13,"fire":13}""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should set all damage dice which rolled 2 or lower to 3 (cold=3+3+3+4=13, fire=3+3+3+4=13)"
        );
    }

}
