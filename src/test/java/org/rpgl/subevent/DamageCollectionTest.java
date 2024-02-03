package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DamageCollection class.
 *
 * @author Calvin Withun
 */
public class DamageCollectionTest {

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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new DamageCollection();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("adds damage")
    void addsDamage() {
        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", new JsonArray());
        }});

        damageCollection.addDamage(new JsonObject() {{
            /*{
                "damage_type": "fire",
                "dice": [
                    { "size": 4, "determined": [ 2 ] },
                    { "size": 4, "determined": [ 2 ] }
                ],
                "bonus": 5
            }*/
            this.putString("damage_type", "fire");
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 4);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(2);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 4);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(2);
                    }});
                }});
            }});
            this.putInteger("bonus", 5);
        }});

        String expected = """
                [{"bonus":5,"damage_type":"fire","dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "new damage values should be added"
        );
    }

    @Test
    @DisplayName("recognizes present damage type")
    void recognizesPresentDamageType() {
        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        assertTrue(damageCollection.includesDamageType("fire"),
                "includesDamageType() should return true when type is present"
        );
    }

    @Test
    @DisplayName("recognizes absent damage type")
    void recognizesAbsentDamageType() {
        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", new JsonArray());
        }});

        assertFalse(damageCollection.includesDamageType("fire"),
                "includesDamageType() should return false when type is absent"
        );
    }

    @Test
    @DisplayName("interprets damage formulae")
    void interpretsDamageFormulae() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 10
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
        }});

        damageCollection.setSource(source);
        damageCollection.prepareDamage(new DummyContext());

        String expected = """
                [{"bonus":10,"damage_type":"fire","dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "prepare should correctly interpret damage instructions"
        );
    }

}
