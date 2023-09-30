package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.CriticalDamageCollection class.
 *
 * @author Calvin Withun
 */
public class CriticalDamageCollectionTest {

    private CriticalDamageCollection criticalDamageCollection;

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new CriticalDamageCollection();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @BeforeEach
    void beforeEach() {
        criticalDamageCollection = new CriticalDamageCollection();
        criticalDamageCollection.joinSubeventData(new JsonObject() {{
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
    @DisplayName("doubleDice number of dice in the damage collection are doubled")
    void doubleDice_numberOfDiceIsDoubled() {
        criticalDamageCollection.doubleDice();

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}]}]""";
        assertEquals(expected, criticalDamageCollection.getDamageCollection().toString(),
                "the number of dice should be doubled after calling doubleDice()"
        );
    }

    @Test
    @DisplayName("includesDamageType returns true for present damage type")
    void includesDamageType_returnsTrueForPresentDamageType() {
        assertTrue(criticalDamageCollection.includesDamageType("fire"),
                "should return true for present damage type"
        );
    }

    @Test
    @DisplayName("includesDamageType returns false for absent damage type")
    void includesDamageType_returnsFalseForAbsentDamageType() {
        assertFalse(criticalDamageCollection.includesDamageType("not_a_damage_type"),
                "should return false for absent damage type"
        );
    }

    @Test
    @DisplayName("addDamage adds damage correctly")
    void addDamage_addsDamageCorrectly() {
        criticalDamageCollection.addDamage(new JsonObject() {{
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
        assertEquals(expected, criticalDamageCollection.getDamageCollection().toString(),
                "new damage should be included in the subevent damage report"
        );
    }

}