package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddDamage class.
 *
 * @author Calvin Withun
 */
public class AddDamageTest {

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

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new AddDamage().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("adds damage")
    void addsDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(object);
        damageCollection.prepare(new DummyContext(), List.of());

        new AddDamage().execute(null, damageCollection, new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
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
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
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
        }}, new DummyContext(), List.of());

        String expected = """
                [{"bonus":2,"damage_type":"fire","dice":[{"determined":[3],"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage range to collection"
        );
    }

}
