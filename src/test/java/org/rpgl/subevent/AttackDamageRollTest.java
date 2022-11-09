package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.math.Die;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AttackDamageRollTest {

    @BeforeAll
    static void beforeAll() {
        Die.setTesting(true);
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "not_a_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "AttackDamageRoll Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent returns the correct final damage values")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "roll": 10 },
                                { "size": 10, "roll": 10 }
                            ],
                            "bonus": 2
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "roll": 10 },
                                { "size": 10, "roll": 10 }
                            ],
                            "bonus": 2
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 22,
                    "cold": 22
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not accurately report rolled damage"
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent can roll dice for damage")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 10 }
                            ],
                            "bonus": 2
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 10 }
                            ],
                            "bonus": 2
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 12,
                    "cold": 12
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not roll dice correctly"
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent can re-roll typed dice below a given value")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "roll": 1, "determined_reroll": 10 },
                                { "size": 10, "roll": 2, "determined_reroll": 10 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "roll": 1, "determined_reroll": 10 },
                                { "size": 10, "roll": 2, "determined_reroll": 10 }
                            ]
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.rerollTypedDiceLessThanOrEqualTo(1L, "fire");

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 12,
                    "cold": 3
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not re-roll dice correctly"
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent can re-roll all dice below a given value")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "roll": 1, "determined_reroll": 10 },
                                { "size": 10, "roll": 2, "determined_reroll": 10 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "roll": 1, "determined_reroll": 10 },
                                { "size": 10, "roll": 2, "determined_reroll": 10 }
                            ]
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.rerollTypedDiceLessThanOrEqualTo(1L, null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 12,
                    "cold": 12
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not re-roll dice correctly"
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent can set typed dice below a given value")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "roll": 1 },
                                { "size": 10, "roll": 2 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "roll": 1 },
                                { "size": 10, "roll": 2 }
                            ]
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.setTypedDiceLessThanOrEqualTo(1L, 10L, "fire");

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 12,
                    "cold": 3
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not set dice correctly"
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent can set all dice below a given value")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "roll": 1 },
                                { "size": 10, "roll": 2 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "roll": 1 },
                                { "size": 10, "roll": 2 }
                            ]
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.setTypedDiceLessThanOrEqualTo(1L, 10L, null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 12,
                    "cold": 12
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not set dice correctly"
        );
    }

    @Test
    @DisplayName("AttackDamageRoll Subevent prepare method works")
    void test7() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_damage_roll",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 10 }
                            ],
                            "bonus": 2
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 10 }
                            ],
                            "bonus": 2
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.prepare(null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 12,
                    "cold": 12
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getDamage().toString(),
                "AttackDamageRoll Subevent did not roll dice correctly"
        );
    }

}
