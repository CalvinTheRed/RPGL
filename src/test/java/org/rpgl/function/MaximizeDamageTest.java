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
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.MaximizeDamage class.
 *
 * @author Calvin Withun
 */
public class MaximizeDamageTest {

    private DamageRoll damageRoll;
    private DamageDelivery damageDelivery;

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
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
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
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
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "roll": 1, "size": 6 },
                            { "roll": 1, "size": 6 },
                        ],
                        "bonus": 0
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "roll": 1, "size": 6 },
                            { "roll": 1, "size": 6 },
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
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
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
        Function function = new MaximizeDamage();
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
    @DisplayName("execute maximizes specific damage type for DamageRoll")
    void execute_maximizesSpecificDamageTypeForDamageRoll() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        damageRoll.setSource(source);
        damageRoll.prepare(context, List.of());
        damageRoll.setTarget(target);

        MaximizeDamage maximizeDamage = new MaximizeDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_damage",
                "damage_type": "fire"
            }*/
            this.putString("function", "maximize_damage");
            this.putString("damage_type", "fire");
        }};

        maximizeDamage.execute(null, damageRoll, functionJson, context, List.of());

        String expected = """
                [{"bonus":2,"damage_type":"fire","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]},{"bonus":2,"damage_type":"cold","dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":1,"size":6}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should maximize fire damage (cold 1+1+2=4 fire 6+6+2=14)"
        );
    }

    @Test
    @DisplayName("execute maximizes every damage type for DamageRoll")
    void execute_maximizesEveryDamageTypeForDamageRoll() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        damageRoll.setSource(source);
        damageRoll.prepare(context, List.of());
        damageRoll.setTarget(target);

        MaximizeDamage maximizeDamage = new MaximizeDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_damage"
            }*/
            this.putString("function", "maximize_damage");
        }};

        maximizeDamage.execute(null, damageRoll, functionJson, context, List.of());

        String expected = """
                [{"bonus":2,"damage_type":"fire","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]},{"bonus":2,"damage_type":"cold","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should maximize all damage (cold 6+6+2=14 fire 6+6+2=14)"
        );
    }

    @Test
    @DisplayName("execute maximizes specific damage type for DamageDelivery")
    void execute_maximizesSpecificDamageTypeForDamageDelivery() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        damageDelivery.setSource(source);
        damageDelivery.prepare(context, List.of());
        damageDelivery.setTarget(target);

        MaximizeDamage maximizeDamage = new MaximizeDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_damage",
                "damage_type": "fire"
            }*/
            this.putString("function", "maximize_damage");
            this.putString("damage_type", "fire");
        }};

        maximizeDamage.execute(null, damageDelivery, functionJson, context, List.of());

        String expected = """
                {"cold":2,"fire":12}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "execute should maximize fire damage only"
        );
    }

    @Test
    @DisplayName("execute maximizes every damage type for DamageDelivery")
    void execute_maximizesEveryDamageTypeForDamageDelivery() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        damageDelivery.setSource(source);
        damageDelivery.prepare(context, List.of());
        damageDelivery.setTarget(target);

        MaximizeDamage maximizeDamage = new MaximizeDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_damage"
            }*/
            this.putString("function", "maximize_damage");
        }};

        maximizeDamage.execute(null, damageDelivery, functionJson, context, List.of());

        String expected = """
                {"cold":12,"fire":12}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "execute should maximize all damage"
        );
    }

}
