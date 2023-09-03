package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DamageDelivery class.
 *
 * @author Calvin Withun
 */
public class DamageDeliveryTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new DamageDelivery();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("getDamage returns stored damage values")
    void getDamage_returnsStoredDamageValues() {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "bonus": 10,
                        "dice": [ ]
                    },
                    {
                        "damage_type": "cold",
                        "bonus": 10,
                        "dice": [ ]
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                    this.putInteger("bonus", 10);
                    this.putJsonArray("dice", new JsonArray());
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putInteger("bonus", 10);
                    this.putJsonArray("dice", new JsonArray());
                }});
            }});
        }});

        String expected = """
                {"cold":10,"fire":10}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "getDamage should report final damage quantities of each damage type being dealt at once"
        );
    }

    @Test
    @DisplayName("maximizeTypedDamageDice maximizes damage for specified damage type")
    void maximizeTypedDamageDice_maximizesDamageForSpecifiedDamageType() {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "roll": 1, "size": 4 }
                        ],
                        "bonus": 0,
                    },
                    {
                        "damage_type": "cold",
                        "dice": [
                            { "roll": 1, "size": 4 }
                        ],
                        "bonus": 0,
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 4);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 4);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        damageDelivery.maximizeTypedDamageDice("fire");

        String expected = """
                {"cold":1,"fire":4}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "maximizeDamage should only maximize damage for the passed damage type"
        );
    }

    @Test
    @DisplayName("maximizeTypedDamageDice maximizes damage for unspecified damage type")
    void maximizeTypedDamageDice_maximizesDamageForUnspecifiedDamageType() {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "roll": 1, "size": 4 }
                        ],
                        "bonus": 0,
                    },
                    {
                        "damage_type": "cold",
                        "dice": [
                            { "roll": 1, "size": 4 }
                        ],
                        "bonus": 0,
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 4);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 4);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        damageDelivery.maximizeTypedDamageDice(null);

        String expected = """
                {"cold":4,"fire":4}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "maximizeDamage should maximize damage for all damage types when null damage type is passed"
        );
    }

    @Test
    @DisplayName("includesDamageType returns true (damage type included)")
    void includesDamageType_returnsTrue_damageTypeIncluded() {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire"
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                }});
            }});
        }});

        assertTrue(damageDelivery.includesDamageType("fire"),
                "should return true when damage type is included"
        );
    }

    @Test
    @DisplayName("includesDamageType returns false (damage type not included)")
    void includesDamageType_returnsFalse_damageTypeNotIncluded() {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire"
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                }});
            }});
        }});

        assertFalse(damageDelivery.includesDamageType("cold"),
                "should return false when damage type is not included"
        );
    }

}
