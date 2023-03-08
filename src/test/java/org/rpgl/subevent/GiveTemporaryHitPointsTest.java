package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.GiveTemporaryHitPoints class.
 *
 * @author Calvin Withun
 */
public class GiveTemporaryHitPointsTest {

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
        Subevent subevent = new GiveTemporaryHitPoints();
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
    @DisplayName("invoke gives temporary hit points and applies rider effect (no prior temporary hit points)")
    void invoke_givesTemporaryHitPointsAndAppliesRiderEffect_noPriorTemporaryHitPoints() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        GiveTemporaryHitPoints giveTemporaryHitPoints = new GiveTemporaryHitPoints();
        giveTemporaryHitPoints.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "give_temporary_hit_points",
                "temporary_hit_points": {
                    "dice": [ ],
                    "bonus": 10
                },
                "rider_effects": [
                    "demo:fire_immunity"
                ]
            }*/
            this.putString("subevent", "give_temporary_hit_points");
            this.putJsonObject("temporary_hit_points", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", 10);
            }});
            this.putJsonArray("rider_effects", new JsonArray() {{
                this.addString("demo:fire_immunity");
            }});
        }});
        giveTemporaryHitPoints.setSource(source);
        giveTemporaryHitPoints.prepare(context);
        giveTemporaryHitPoints.setTarget(target);
        giveTemporaryHitPoints.invoke(context);

        assertEquals(10, target.getHealthData().getInteger("temporary"),
                "the subevent should grant 10 temporary hit points to the target"
        );
        List<RPGLEffect> effects = target.getEffectObjects();
        assertEquals(1, effects.size(),
                "commoner should have 1 effect after the subevent is invoked"
        );
        assertEquals("demo:fire_immunity", effects.get(0).getId(),
                "the commoner's subevent should match the effect specified in the subevent json"
        );
    }

    @Test
    @DisplayName("invoke gives temporary hit points and applies rider effect (few prior temporary hit points)")
    void invoke_givesTemporaryHitPointsAndAppliesRiderEffect_fewPriorTemporaryHitPoints() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("temporary", 5);

        GiveTemporaryHitPoints giveTemporaryHitPoints = new GiveTemporaryHitPoints();
        giveTemporaryHitPoints.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "give_temporary_hit_points",
                "temporary_hit_points": {
                    "dice": [ ],
                    "bonus": 10
                },
                "rider_effects": [
                    "demo:fire_immunity"
                ]
            }*/
            this.putString("subevent", "give_temporary_hit_points");
            this.putJsonObject("temporary_hit_points", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", 10);
            }});
            this.putJsonArray("rider_effects", new JsonArray() {{
                this.addString("demo:fire_immunity");
            }});
        }});
        giveTemporaryHitPoints.setSource(source);
        giveTemporaryHitPoints.prepare(context);
        giveTemporaryHitPoints.setTarget(target);
        giveTemporaryHitPoints.invoke(context);

        assertEquals(10, target.getHealthData().getInteger("temporary"),
                "the subevent should grant 10 temporary hit points to the target"
        );
        List<RPGLEffect> effects = target.getEffectObjects();
        assertEquals(1, effects.size(),
                "commoner should have 1 effect after the subevent is invoked"
        );
        assertEquals("demo:fire_immunity", effects.get(0).getId(),
                "the commoner's subevent should match the effect specified in the subevent json"
        );
    }

    @Test
    @DisplayName("invoke does not give temporary hit points or apply rider effect (many prior temporary hit points)")
    void invoke_doesNotGiveTemporaryHitPointsOrApplyRiderEffect_manyPriorTemporaryHitPoints() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("temporary", 15);

        GiveTemporaryHitPoints giveTemporaryHitPoints = new GiveTemporaryHitPoints();
        giveTemporaryHitPoints.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "give_temporary_hit_points",
                "temporary_hit_points": {
                    "dice": [ ],
                    "bonus": 10
                },
                "rider_effects": [
                    "demo:fire_immunity"
                ]
            }*/
            this.putString("subevent", "give_temporary_hit_points");
            this.putJsonObject("temporary_hit_points", new JsonObject() {{
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", 10);
            }});
            this.putJsonArray("rider_effects", new JsonArray() {{
                this.addString("demo:fire_immunity");
            }});
        }});
        giveTemporaryHitPoints.setSource(source);
        giveTemporaryHitPoints.prepare(context);
        giveTemporaryHitPoints.setTarget(target);
        giveTemporaryHitPoints.invoke(context);

        assertEquals(15, target.getHealthData().getInteger("temporary"),
                "the subevent should not grant new temporary hit points to the target"
        );
        assertEquals(0, target.getEffectObjects().size(),
                "commoner should have 0 effects after the subevent is invoked"
        );
    }

    @Test
    @DisplayName("unpackTemporaryHitPointsData unpacks all passed dice")
    void unpackTemporaryHitPointsData_unpacksAllPassedDice() {
        GiveTemporaryHitPoints giveTemporaryHitPoints = new GiveTemporaryHitPoints();
        JsonObject temporaryHitPointsData = new JsonObject() {{
            /*{
                "dice": [
                    { "count": 2, "size": 6, "determined": [ 3 ] },
                    { "count": 2, "size": 10, "determined": [ 5 ] }
                ],
                "bonus": 2
            }*/
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("count", 2);
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(3);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("count", 2);
                    this.putInteger("size", 10);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(5);
                    }});
                }});
            }});
            this.putInteger("bonus", 2);
        }};

        String expected = """
                {"bonus":2,"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[5],"size":10},{"determined":[5],"size":10}]}""";
        assertEquals(expected, giveTemporaryHitPoints.unpackTemporaryHitPointsData(temporaryHitPointsData).toString(),
                "returned data should contain an unpacked representation of the passed dice"
        );
    }

    @Test
    @DisplayName("getTemporaryHitPoints returns correct temporary hit points value")
    void getTemporaryHitPoints_returnsCorrectTemporaryHitPointsValue() {
        GiveTemporaryHitPoints giveTemporaryHitPoints = new GiveTemporaryHitPoints();
        JsonObject temporaryHitPointsData = new JsonObject() {{
            /*{
                "dice": [
                    { "size": 6, "determined": [ 3 ] },
                    { "size": 6, "determined": [ 3 ] }
                ],
                "bonus": 2
            }*/
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
            this.putInteger("bonus", 2);
        }};

        assertEquals(8, giveTemporaryHitPoints.getTemporaryHitPoints(temporaryHitPointsData),
                "getTemporaryHitPoints should roll all dice and add them to the bonus (3+3+2=8)"
        );
    }

}
