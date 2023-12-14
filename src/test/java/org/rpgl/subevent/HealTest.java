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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.HealTest class.
 *
 * @author Calvin Withun
 */
public class HealTest {

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
        Subevent subevent = new Heal();
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
    @DisplayName("deliverHealing conveys healing to target")
    void deliverHealing_conveysHealingToTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "roll": 5 }
                        ],
                        "bonus": 5
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 5);
                        }});
                    }});
                    this.putInteger("bonus", 5);
                }});
            }});
        }});
        heal.setSource(source);
        heal.setTarget(target);

        heal.deliverHealing(context, List.of());

        assertEquals(20, target.getHealthData().getInteger("current"),
                "target should recover 10 hit points (10+10=20)"
        );
    }

    @Test
    @DisplayName("getTargetHealing contains no dice and no bonus (default)")
    void getTargetHealing_containsNoDiceAndNoBonus_default() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        Heal heal = new Heal();
        heal.setSource(source);
        heal.prepare(context, List.of());
        heal.setTarget(target);
        heal.getTargetHealing(context, List.of());

        String expected = """
                []""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "default target healing should be 0"
        );
    }

    @Test
    @DisplayName("getBaseHealing returns correct base healing")
    void getBaseHealing_returnsCorrectBaseHealing() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count":2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(source);
        heal.getBaseHealing(context, List.of());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":1,"size":6}]}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "base healing should be rolled and in accordance with subevent-specified healing"
        );
    }

    @Test
    @DisplayName("invoke target is healed correctly")
    void invoke_targetIsHealedCorrectly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(source);
        heal.prepare(context, List.of());
        heal.setTarget(target);
        heal.invoke(context, List.of());

        assertEquals(14, target.getHealthData().getInteger("current"),
                "invoking heal should restore 4 hit points (1+1+2=+4)"
        );
    }

    @Test
    @DisplayName("prepare base healing is rolled and stored")
    void prepare_baseHealingIsRolledAndStored() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(source);
        heal.prepare(context, List.of());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":1,"size":6}]}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "base healing should be rolled and stored after calling prepare"
        );
    }

    @Test
    @DisplayName("invoke comprehensive test")
    void invoke_comprehensiveTest() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(source);
        heal.prepare(context, List.of());
        heal.setTarget(target);
        heal.invoke(context, List.of());

        assertEquals(14, target.getHealthData().getInteger("current"),
                "target should recover 4 hit points from Heal subevent (1+1+2=+4)"
        );
    }

    @Test
    @DisplayName("getBaseHealing calculates correct healing (modifier)")
    void getBaseHealing_calculatesCorrectHealing_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "modifier",
                        "ability": "str",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "modifier");
                    this.putString("ability", "str");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        heal.setSource(source);
        heal.getBaseHealing(context, List.of());

        String expected = """
                [{"bonus":6,"dice":[]}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "healing should equal str mod"
        );
    }

    @Test
    @DisplayName("getBaseHealing calculates correct healing (ability)")
    void getBaseHealing_calculatesCorrectHealing_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "ability",
                        "ability": "str",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "ability");
                    this.putString("ability", "str");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        heal.setSource(source);
        heal.getBaseHealing(context, List.of());

        String expected = """
                [{"bonus":23,"dice":[]}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "healing should equal str score (23)"
        );
    }

    @Test
    @DisplayName("getBaseHealing calculates correct healing (proficiency)")
    void getBaseHealing_calculatesCorrectHealing_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "proficiency",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "proficiency");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        heal.setSource(source);
        heal.getBaseHealing(context, List.of());

        String expected = """
                [{"bonus":4,"dice":[]}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "healing should equal proficiency bonus"
        );
    }

}
