package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.HealTest class.
 *
 * @author Calvin Withun
 */
public class HealTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
                () -> subevent.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("deliverHealing conveys healing to target")
    void deliverHealing_conveysHealingToTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.setSource(source);
        heal.setTarget(target);

        heal.deliverHealing(context, 10);

        assertEquals(20, target.getHealthData().getInteger("current"),
                "target should recover 10 hit points (10+10=20)"
        );
    }

    @Test
    @DisplayName("getTargetHealing contains no dice and no bonus (default)")
    void getTargetHealing_containsNoDiceAndNoBonus_default() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Heal heal = new Heal();
        heal.setSource(source);
        heal.setTarget(target);

        JsonObject targetHealingJson = heal.getTargetHealing(context);

        String expected = """
                {"bonus":0,"dice":[]}""";
        assertEquals(expected, targetHealingJson.toString(),
                "default target healing should contain no dice and have no bonus"
        );
    }

    @Test
    @DisplayName("getBaseHealing returns correct base healing")
    void getBaseHealing_returnsCorrectBaseHealing() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": {
                    "dice": [
                        { "size": 6, "determined": [ 1 ] },
                        { "size": 6, "determined": [ 2 ] }
                    ],
                    "bonus": 2
                }
            }*/
            this.putJsonObject("healing", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(1);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(2);
                        }});
                    }});
                }});
                this.putInteger("bonus", 2);
            }});
        }});

        heal.setSource(source);
        heal.getBaseHealing(context);

        String expected = """
                {"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6}]}""";
        assertEquals(expected, heal.json.getJsonObject("healing").toString(),
                "base healing should be rolled and in accordance with subevent-specified healing"
        );
    }

    @Test
    @DisplayName("invoke target is healed correctly")
    void invoke_targetIsHealedCorrectly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "heal",
                "healing": {
                    "dice": [
                        { "size": 6, "roll": 1 },
                        { "size": 6, "roll": 2 }
                    ],
                    "bonus": 2
                }
            }*/
            this.putString("subevent", "heal");
            this.putJsonObject("healing", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putInteger("roll", 1);
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putInteger("roll", 2);
                    }});
                }});
                this.putInteger("bonus", 2);
            }});
        }});

        heal.setSource(source);
        heal.setTarget(target);
        heal.invoke(context);

        assertEquals(15, target.getHealthData().getInteger("current"),
                "invoking heal should restore 5 hit points (10+5=15)"
        );
    }

    @Test
    @DisplayName("prepare base healing is rolled and stored")
    void prepare_baseHealingIsRolledAndStored() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": {
                    "dice": [
                        { "size": 6, "determined": [ 1 ] },
                        { "size": 6, "determined": [ 2 ] }
                    ],
                    "bonus": 2
                }
            }*/
            this.putJsonObject("healing", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(1);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(2);
                        }});
                    }});
                }});
                this.putInteger("bonus", 2);
            }});
        }});

        heal.setSource(source);
        heal.prepare(context);

        String expected = """
                {"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6}]}""";
        assertEquals(expected, heal.json.getJsonObject("healing").toString(),
                "base healing should be rolled and stored after calling prepare"
        );
    }

    @Test
    @DisplayName("invoke comprehensive test")
    void invoke_comprehensiveTest() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "heal",
                "healing": {
                    "dice": [
                        { "size": 6, "determined": [ 1 ] },
                        { "size": 6, "determined": [ 2 ] }
                    ],
                    "bonus": 2
                }
            }*/
            this.putString("subevent", "heal");
            this.putJsonObject("healing", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(1);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(2);
                        }});
                    }});
                }});
                this.putInteger("bonus", 2);
            }});
        }});

        heal.setSource(source);
        heal.prepare(context);
        heal.setTarget(target);
        heal.invoke(context);

        assertEquals(15, target.getHealthData().getInteger("current"),
                "target should recover 5 hit points from Heal subevent"
        );
    }

}
