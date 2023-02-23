package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                        "type": "fire",
                        "dice": [
                            { "size": 4, "roll": 1, "determined": [ 4 ] },
                            { "size": 4, "roll": 2, "determined": [ 4 ] },
                            { "size": 4, "roll": 3, "determined": [ 4 ] },
                            { "size": 4, "roll": 4, "determined": [ 4 ] }
                        ],
                        "bonus": 1
                    },{
                        "type": "cold",
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
                    this.putString("type", "fire");
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
                    this.putString("type", "cold");
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
    @DisplayName("rerollTypedDiceLessThanOrEqualTo re-roll all ones (fire damage)")
    void rerollTypedDiceLessThanOrEqualTo_rerollAllOnes_fireDamage() {
        damageRoll.rerollTypedDiceLessThanOrEqualTo(1, "fire");

        String expected = """
                [{"bonus":1,"dice":[{"determined":[],"roll":4,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}],"type":"fire"},{"bonus":1,"dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}],"type":"cold"}]""";
        assertEquals(expected, damageRoll.subeventJson.getJsonArray("damage").toString(),
                "the fire die which had a roll of 1 should be re-rolled to a 4"
        );
    }

    @Test
    @DisplayName("setTypedDiceLessThanOrEqualTo set all ones to twos (fire damage)")
    void setTypedDiceLessThanOrEqualTo_setAllOnesToTwos_fireDamage() {
        damageRoll.setTypedDiceLessThanOrEqualTo(1, 2, "fire");

        String expected = """
                [{"bonus":1,"dice":[{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}],"type":"fire"},{"bonus":1,"dice":[{"determined":[4],"roll":1,"size":4},{"determined":[4],"roll":2,"size":4},{"determined":[4],"roll":3,"size":4},{"determined":[4],"roll":4,"size":4}],"type":"cold"}]""";
        assertEquals(expected, damageRoll.subeventJson.getJsonArray("damage").toString(),
                "the fire die which had a roll of 1 should be set to a 2"
        );
    }

    @Test
    @DisplayName("getDamage returns total typed damage values")
    void getDamage_returnsTotalTypedDamageValues() {
        String expected = """
                {"cold":11,"fire":11}""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "cold and fire damage should both be 11 (4+3+2+1+1)"
        );
    }

    @Test
    @DisplayName("roll all dice roll to fours")
    void roll_allDiceRollToOnes() {
        damageRoll.roll();

        String expected = """
                [{"bonus":1,"dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}],"type":"fire"},{"bonus":1,"dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}],"type":"cold"}]""";
        assertEquals(expected, damageRoll.subeventJson.getJsonArray("damage").toString(),
                "all dice should roll to 4"
        );
    }

    @Test
    @DisplayName("prepare all dice roll to fours")
    void prepare_allDiceRollToOnes() throws Exception {
        damageRoll.prepare(new RPGLContext());

        String expected = """
                [{"bonus":1,"dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}],"type":"fire"},{"bonus":1,"dice":[{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4},{"determined":[],"roll":4,"size":4}],"type":"cold"}]""";
        assertEquals(expected, damageRoll.subeventJson.getJsonArray("damage").toString(),
                "all dice should roll to 4"
        );
    }

}
