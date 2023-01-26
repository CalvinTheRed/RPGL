package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for subevent.CriticalHitDamageDiceCollection class.
 *
 * @author Calvin Withun
 */
public class CriticalHitDamageDiceCollectionTest {

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
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
                "CriticalHitDamageDiceCollection Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent accurately reports its typed damage")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10 }
                            ],
                            "bonus": 10
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10 }
                            ],
                            "bonus": 10
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        assertEquals(subeventJson.get("damage").toString(), typedDamageArray.toString(),
                "CriticalHitDamageDiceCollection Subevent did not accurately report its typed damage."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent accurately reports the presence of a damage type")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [
                        { "type": "fire" }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(criticalHitDamageDiceCollection.includesDamageType("fire"),
                "CriticalHitDamageDiceCollection Subevent should return true for a damage type which is present."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent accurately reports the absence of a damage type")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [ ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(criticalHitDamageDiceCollection.includesDamageType("fire"),
                "CriticalHitDamageDiceCollection Subevent should return false for a damage type which is not present."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent can add existing typed damage (bonus)")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [
                        { "type": "fire" }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = """
                {
                    "type": "fire",
                    "bonus": 10
                }
                """;
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        criticalHitDamageDiceCollection.addExistingTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        assertEquals(10L, typedDamageBonus,
                "CriticalHitDamageDiceCollection Subevent didn't add the correct typed damage bonus."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent can add existing typed damage (dice)")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [
                        { "type": "fire" }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = """
                {
                    "type": "fire",
                    "dice": [
                        { "size": 10 }
                    ]
                }
                """;
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        criticalHitDamageDiceCollection.addExistingTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        assertEquals(typedDamageJson.get("dice").toString(), typedDamage.get("dice").toString(),
                "CriticalHitDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent can add new typed damage (bonus)")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [ ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = """
                {
                    "type": "fire",
                    "bonus": 10
                }
                """;
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        criticalHitDamageDiceCollection.addNewTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        assertEquals(10L, typedDamageBonus,
                "CriticalHitDamageDiceCollection Subevent didn't add the correct typed damage bonus."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent can add new typed damage (dice)")
    void test7() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [ ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = """
                {
                    "type": "fire",
                    "dice": [
                        { "size": 10 }
                    ]
                }
                """;
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        criticalHitDamageDiceCollection.addNewTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        assertEquals(typedDamageJson.get("dice").toString(), typedDamage.get("dice").toString(),
                "CriticalHitDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent can add typed damage")
    void test8() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [
                        { "type": "fire" }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageArrayString = """
                [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 10 }
                        ],
                        "bonus": 10
                    },
                    {
                        "type": "cold",
                        "dice": [
                            { "size": 10 }
                        ],
                        "bonus": 10
                    }
                ]
                """;
        JsonArray typedDamageArray = JsonParser.parseArrayString(typedDamageArrayString);
        criticalHitDamageDiceCollection.addTypedDamage(typedDamageArray);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray finalTypedDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        assertEquals(typedDamageArray.toString(), finalTypedDamageArray.toString(),
                "CriticalHitDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

    @Test
    @DisplayName("CriticalHitDamageDiceCollection Subevent can double its damage dice")
    void test9() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CriticalHitDamageDiceCollection();
        String subeventJsonString = """
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 1 }
                            ],
                            "bonus": 1
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 1 },
                                { "size": 10, "determined": 1 }
                            ],
                            "bonus": 1
                        }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = (CriticalHitDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        criticalHitDamageDiceCollection.doubleDice();

        /*
         * Verify subevent behaves as expected
         */
        String expectedDamageArrayString = """
                [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 10, "determined": 1 },
                            { "size": 10, "determined": 1 }
                        ],
                        "bonus": 1
                    },
                    {
                        "type": "cold",
                        "dice": [
                            { "size": 10, "determined": 1 },
                            { "size": 10, "determined": 1 },
                            { "size": 10, "determined": 1 },
                            { "size": 10, "determined": 1 }
                        ],
                        "bonus": 1
                    }
                ]
                """;
        JsonArray expectedDamageArray = JsonParser.parseArrayString(expectedDamageArrayString);
        JsonArray actualDamageArray = criticalHitDamageDiceCollection.getDamageDiceCollection();
        assertEquals(expectedDamageArray.toString(), actualDamageArray.toString(),
                "CriticalHitDamageDiceCollection Subevent didn't double damage dice correctly."
        );
    }

}
