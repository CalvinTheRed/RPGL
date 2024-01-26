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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
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
    @DisplayName("re-rolls low damage dice (specific damage type)")
    void rerollsLowDamageDice_specificDamageType() {
        damageRoll.rerollDamageDiceMatchingOrBelow(1, "fire");

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[],"roll":4,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "the fire die which had a roll of 1 should be re-rolled to a 4"
        );
    }

    @Test
    @DisplayName("re-rolls low damage dice (all damage types)")
    void rerollsLowDamageDice_allDamageTypes() {
        damageRoll.rerollDamageDiceMatchingOrBelow(1, null);

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[],"roll":4,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[],"roll":4,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all die which had a roll of 1 should be re-rolled to a 4"
        );
    }

    @Test
    @DisplayName("sets low damage dice (specific damage type)")
    void setsLowDamageDice_specificDamageType() {
        damageRoll.setDamageDiceMatchingOrBelow(1, 2, "fire");

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "the fire die which had a roll of 1 should be set to a 2"
        );
    }

    @Test
    @DisplayName("sets low damage dice (all damage types)")
    void setsLowDamageDice_allDamageTypes() {
        damageRoll.setDamageDiceMatchingOrBelow(1, 2, null);

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all dice which had a roll of 1 should be set to a 2"
        );
    }

    @Test
    @DisplayName("gets damage")
    void getsDamage() {
        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "cold and fire damage should both be 11 (4+3+2+1+1)"
        );
    }

    @Test
    @DisplayName("prepares and rolls damage")
    void preparesAndRollsDamage() throws Exception {
        damageRoll.prepare(new DummyContext());

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all dice should roll to 4"
        );
    }

    @Test
    @DisplayName("maximizes damage (specific damage type)")
    void maximizesDamage_specificDamageType() {
        damageRoll.maximizeDamageDice("fire");

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all fire dice should maximize to 4, while cold dice are unchanged"
        );
    }

    @Test
    @DisplayName("maximizes damage (all damage types)")
    void maximizesDamage_allDamageTypes() {
        damageRoll.maximizeDamageDice(null);

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4}]},{"bonus":1,"damage_type":"cold","dice":[{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4},{"determined":[4],"roll":4,"size":4}]}]""";
        assertEquals(expected, damageRoll.json.getJsonArray("damage").toString(),
                "all dice should maximize to 4"
        );
    }

    @Test
    @DisplayName("recognizes present damage type")
    void recognizesPresentDamageType() {
        assertTrue(damageRoll.includesDamageType("fire"),
                "should return true when damage type is included"
        );
    }

    @Test
    @DisplayName("recognizes absent damage type")
    void recognizesAbsentDamageType() {
        assertFalse(damageRoll.includesDamageType("acid"),
                "should return false when damage type is not included"
        );
    }

}
