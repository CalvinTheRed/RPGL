package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.CriticalHitDamageCollection class.
 *
 * @author Calvin Withun
 */
public class CriticalHitDamageCollectionTest {

    private CriticalHitDamageCollection criticalHitDamageCollection;

    @Test
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new CriticalHitDamageCollection();
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

    @BeforeEach
    void beforeEach() {
        criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 1
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 1
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
                                this.addInteger(3);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});
    }

    @Test
    @DisplayName("doubles dice")
    void doublesDice() {
        criticalHitDamageCollection.doubleDice();

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}]}]""";
        assertEquals(expected, criticalHitDamageCollection.getDamageCollection().toString(),
                "the number of dice should be doubled after calling doubleDice()"
        );
    }

    @Test
    @DisplayName("recognizes present damage type")
    void recognizesPresentDamageType() {
        assertTrue(criticalHitDamageCollection.includesDamageType("fire"),
                "should return true for present damage type"
        );
    }

    @Test
    @DisplayName("recognizes absent damage type")
    void recognizesAbsentDamageType() {
        assertFalse(criticalHitDamageCollection.includesDamageType("not_a_damage_type"),
                "should return false for absent damage type"
        );
    }

    @Test
    @DisplayName("adds damage")
    void addsDamage() {
        criticalHitDamageCollection.addDamage(new JsonObject() {{
            /*{
                "damage_type": "acid",
                "dice": [
                    { "size": 4, "determined": [ 2 ] }
                ],
                "bonus": 5
            }*/
            this.putString("damage_type", "acid");
            this.putJsonArray("dice", new JsonArray() {{
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
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[5],"size":10}]},{"bonus":5,"damage_type":"acid","dice":[{"determined":[2],"size":4}]}]""";
        assertEquals(expected, criticalHitDamageCollection.getDamageCollection().toString(),
                "new damage should be included in the subevent damage report"
        );
    }

}
