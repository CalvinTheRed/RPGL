package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.RerollDamageDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class RerollDamageDiceMatchingOrBelowTest {

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
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 1, 6 ] },
                            { "size": 6, "determined": [ 2, 6 ] },
                            { "size": 6, "determined": [ 3, 6 ] },
                            { "size": 6, "determined": [ 4, 6 ] }
                        ],
                        "bonus": 0
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "size": 6, "determined": [ 1, 6 ] },
                            { "size": 6, "determined": [ 2, 6 ] },
                            { "size": 6, "determined": [ 3, 6 ] },
                            { "size": 6, "determined": [ 4, 6 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
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
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
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
        Function function = new RerollDamageDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context, List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute re-rolls all dice at or below two (fire only)")
    void execute_rerollsAllDiceAtOrBelowTwo_fireOnly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        damageRoll.setSource(source);
        damageRoll.prepare(context, List.of());
        damageRoll.setTarget(target);

        RerollDamageDiceMatchingOrBelow rerollDamageDiceMatchingOrBelow = new RerollDamageDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "reroll_damage_dice_matching_or_below",
                "threshold": 2,
                "damage_type": "fire"
            }*/
            this.putString("function", "reroll_damage_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putString("damage_type", "fire");
        }};

        rerollDamageDiceMatchingOrBelow.execute(null, damageRoll, functionJson, context, List.of());

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6},{"determined":[6],"roll":3,"size":6},{"determined":[6],"roll":4,"size":6}]},{"bonus":0,"damage_type":"cold","dice":[{"determined":[6],"roll":1,"size":6},{"determined":[6],"roll":2,"size":6},{"determined":[6],"roll":3,"size":6},{"determined":[6],"roll":4,"size":6}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should re-roll all fire damage dice which rolled 2 or lower (cold=1+2+3+4=10, fire=6+6+3+4=19)"
        );
    }

    @Test
    @DisplayName("execute re-rolls all dice at or below two (all damage types)")
    void execute_rerollsAllDiceAtOrBelowTwo_allDamageTypes() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        damageRoll.setSource(source);
        damageRoll.prepare(context, List.of());
        damageRoll.setTarget(target);

        RerollDamageDiceMatchingOrBelow rerollDamageDiceMatchingOrBelow = new RerollDamageDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "reroll_damage_dice_matching_or_below",
                "threshold": 2,
                "damage_type": ""
            }*/
            this.putString("function", "reroll_damage_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putString("damage_type", "");
        }};

        rerollDamageDiceMatchingOrBelow.execute(null, damageRoll, functionJson, context, List.of());

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6},{"determined":[6],"roll":3,"size":6},{"determined":[6],"roll":4,"size":6}]},{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6},{"determined":[6],"roll":3,"size":6},{"determined":[6],"roll":4,"size":6}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should set all damage dice which rolled 2 or lower to 3 (cold=6+6+3+4=19, fire=6+6+3+4=19)"
        );
    }

}
