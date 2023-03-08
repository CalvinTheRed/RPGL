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
import org.rpgl.subevent.DamageCollection;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddDamage class.
 *
 * @author Calvin Withun
 */
public class AddDamageTest {

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
        Function function = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("unpackDamage returns damage with unpacked damage dice")
    void unpackDamage_returnsDamageWithUnpackedDamageDice() {
        AddDamage addDamage = new AddDamage();
        JsonArray damage = new JsonArray() {{
            /*[
                {
                    "type": "fire",
                    "dice": [
                        { "count": 2, "size": 6, "determined": [ 3 ] },
                        { "count": 2, "size": 4, "determined": [ 2 ] },
                    ]
                },{
                    "type": "cold",
                    "dice": [
                        { "count": 1, "size": 10, "determined": [ 5 ] }
                    ]
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("type", "fire");
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
                        this.putInteger("size", 4);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(2);
                        }});
                    }});
                }});
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("type", "cold");
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("count", 1);
                        this.putInteger("size", 10);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(5);
                        }});
                    }});
                }});
            }});
        }};

        String expected = """
                [{"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[2],"size":4},{"determined":[2],"size":4}],"type":"fire"},{"dice":[{"determined":[5],"size":10}],"type":"cold"}]""";
        assertEquals(expected, addDamage.unpackDamage(damage).toString(),
                "calling unpackCompactedDamageDice should unpack the compacted damage dice passed to it"
        );
    }

    @Test
    @DisplayName("execute adds damage to subevent")
    void execute_addsDamageToSubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_damage",
                "damage": [
                    {
                        "type": "fire",
                        "dice": [
                            { "count": 1, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 2
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "fire");
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
            }});
        }};

        addDamage.execute(source, target, damageCollection, functionJson, context);

        String expected = """
                [{"bonus":2,"dice":[{"determined":[3],"size":6}],"type":"fire"}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add damage to DamageCollection subevent"
        );
    }

}
