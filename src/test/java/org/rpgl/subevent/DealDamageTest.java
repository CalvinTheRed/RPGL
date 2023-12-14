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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DealDamage class.
 *
 * @author Calvin Withun
 */
public class DealDamageTest {

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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new DealDamage();
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

    @Test
    @DisplayName("deliverDamage damage is delivered")
    void deliverDamage_damageIsDelivered() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "tags": [ ],
                "damage": [
                    {
                        "damage_type": "cold",
                        "dice": [
                            { "roll": 5 }
                        ],
                        "bonus": 5
                    }
                ]
            }*/
            this.putJsonArray("tags", new JsonArray());
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 5);
                        }});
                    }});
                    this.putInteger("bonus", 5);
                }});
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.setTarget(target);
        dealDamage.deliverDamage(context, List.of());

        assertEquals(42, target.getHealthData().getInteger("current"),
                "10 damage should be delivered (52-10=42)"
        );
    }

    @Test
    @DisplayName("getTargetDamage no target damage by default")
    void getTargetDamage_noTargetDamageByDefault() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "tags": [ ],
                "damage": [ ]
            }*/
            this.putJsonArray("tags", new JsonArray());
            this.putJsonArray("damage", new JsonArray());
        }});

        dealDamage.setSource(source);
        dealDamage.setTarget(target);
        dealDamage.getTargetDamage(context, List.of());

        assertEquals("[]", dealDamage.json.getJsonArray("damage").toString(),
                "target damage object should be empty by default"
        );
    }

    @Test
    @DisplayName("getBaseDamage base damage calculated correctly")
    void getBaseDamage_baseDamageCalculatedProperly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "tags": [ ],
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "force",
                        "dice": [
                            { "count": 1, "size": 4, "determined": [ 2 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putJsonArray("tags", new JsonArray());
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "force");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
                            this.putInteger("size", 4);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.setTarget(target);
        dealDamage.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":1,"damage_type":"force","dice":[{"determined":[],"roll":2,"size":4}]}]""";
        assertEquals(expected, dealDamage.json.getJsonArray("damage").toString(),
                "base damage should be 3 force damage"
        );
    }

    @Test
    @DisplayName("invoke deals correct damage")
    void invoke_dealsCorrectDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "force",
                        "dice": [
                            { "count": 1, "size": 4, "determined": [ 2 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "force");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
                            this.putInteger("size", 4);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.prepare(context, List.of());
        dealDamage.setTarget(target);
        dealDamage.invoke(context, List.of());

        assertEquals(49, target.getHealthData().getInteger("current"),
                "invoking DealDamage should deal 3 points of damage (52-3=49)"
        );
    }

    @Test
    @DisplayName("invoke accommodates vampirism")
    void invoke_accommodatesVampirism() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 11);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "necrotic",
                        "dice": [
                            { "count": 2, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                }
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "necrotic");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.prepare(context, List.of());
        dealDamage.setTarget(target);
        dealDamage.invoke(context, List.of());

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half damage from vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should take damage and not be healed from vampirism"
        );
    }

    @Test
    @DisplayName("includesDamageType returns true (damage type included)")
    void includesDamageType_returnsTrue_damageTypeIncluded() {
        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
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

        assertTrue(dealDamage.includesDamageType("fire"),
                "should return true when damage type is present"
        );
    }

    @Test
    @DisplayName("includesDamageType returns false (damage type not included)")
    void includesDamageType_returnsFalse_damageTypeNotIncluded() {
        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
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

        assertFalse(dealDamage.includesDamageType("cold"),
                "should return false when damage type is not present"
        );
    }

}
