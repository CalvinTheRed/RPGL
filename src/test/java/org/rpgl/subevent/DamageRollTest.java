package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DamageRoll class.
 *
 * @author Calvin Withun
 */
public class DamageRollTest {

    private DamageRoll damageRoll;

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
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
                            { "size": 4, "roll": 1, "determined": [ 4 ] },
                            { "size": 4, "roll": 2, "determined": [ 4 ] },
                            { "size": 4, "roll": 3, "determined": [ 4 ] },
                            { "size": 4, "roll": 4, "determined": [ 4 ] }
                        ],
                        "bonus": 1
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "size": 4, "roll": 1, "determined": [ 4 ] },
                            { "size": 4, "roll": 2, "determined": [ 4 ] },
                            { "size": 4, "roll": 3, "determined": [ 4 ] },
                            { "size": 4, "roll": 4, "determined": [ 4 ] }
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
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 1);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 2);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 3);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 4);
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 1);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 2);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 3);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                            this.putInteger("size", 4);
                            this.putInteger("roll", 4);
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new DamageRoll();
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
    @DisplayName("rerollTypedDiceMatchingOrBelow re-roll all ones (fire damage)")
    void rerollTypedDiceMatchingOrBelow_rerollAllOnes_fireDamage() {
        damageRoll.rerollTypedDiceMatchingOrBelow(1, "fire");

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[],"roll":4,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "the fire die which had a roll of 1 should be re-rolled to a 4"
        );
    }

    @Test
    @DisplayName("setTypedDiceMatchingOrBelow set all ones to twos (fire damage)")
    void setTypedDiceMatchingOrBelow_setAllOnesToTwos_fireDamage() {
        damageRoll.setTypedDiceMatchingOrBelow(1, 2, "fire");

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "the fire die which had a roll of 1 should be set to a 2"
        );
    }

    @Test
    @DisplayName("getDamage returns total typed damage values")
    void getDamage_returnsTotalTypedDamageValues() {
        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "cold and fire damage should both be 11 (4+3+2+1+1)"
        );
    }

    @Test
    @DisplayName("roll all dice roll to fours")
    void roll_allDiceRollToOnes() {
        damageRoll.roll();

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all dice should roll to 4"
        );
    }

    @Test
    @DisplayName("prepare all dice roll to fours")
    void prepare_allDiceRollToOnes() throws Exception {
        damageRoll.prepare(new DummyContext());

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all dice should roll to 4"
        );
    }

    @Test
    @DisplayName("maximizeDamageDice maximizes damage correctly (fire only)")
    void maximizeTypedDamageDice_maximizesDamageCorrectly_fireOnly() {
        damageRoll.maximizeTypedDamageDice("fire");

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all fire dice should maximize to 4, while cold dice are unchanged"
        );
    }

    @Test
    @DisplayName("maximizeDamageDice maximizes damage correctly (all damage)")
    void maximizeTypedDamageDice_maximizesDamageCorrectly_allDamage() {
        damageRoll.maximizeTypedDamageDice(null);

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all dice should maximize to 4"
        );
    }

    @Test
    @DisplayName("includesDamageType returns true (damage type included)")
    void includesDamageType_returnsTrue_damageTypeIncluded() {
        assertTrue(damageRoll.includesDamageType("fire"),
                "should return true when damage type is included"
        );
    }

    @Test
    @DisplayName("includesDamageType returns true (damage type not included)")
    void includesDamageType_returnsFalse_damageTypeNotIncluded() {
        assertFalse(damageRoll.includesDamageType("acid"),
                "should return false when damage type is not included"
        );
    }

}
