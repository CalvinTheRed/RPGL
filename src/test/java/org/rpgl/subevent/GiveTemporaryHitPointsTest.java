package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
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
 * Testing class for the org.rpgl.subevent.GiveTemporaryHitPoints class.
 *
 * @author Calvin Withun
 */
public class GiveTemporaryHitPointsTest {

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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new GiveTemporaryHitPoints()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "not_a_subevent"
                    }*/
                    this.putString("subevent", "not_a_subevent");
                }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("gives temporary hit points and rider")
    void givesTemporaryHitPointsAndRider() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        new GiveTemporaryHitPoints()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "temporary_hit_points": [
                            {
                                "formula": "range",
                                "dice": [ ],
                                "bonus": 10
                            }
                        ],
                        "rider_effects": [
                            "std:common/damage/immunity/fire"
                        ]
                    }*/
                    this.putJsonArray("temporary_hit_points", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("formula", "range");
                            this.putJsonArray("dice", new JsonArray());
                            this.putInteger("bonus", 10);
                        }});
                    }});
                    this.putJsonArray("rider_effects", new JsonArray() {{
                        this.addString("std:common/damage/immunity/fire");
                    }});
                }})
                .setSource(source)
                .prepare(context, TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(context, TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(10, target.getHealthData().getInteger("temporary"),
                "the subevent should grant 10 temporary hit points to the target"
        );
        List<RPGLEffect> effects = target.getEffectObjects();
        assertEquals(1, effects.size(),
                "commoner should have 1 effect after the subevent is invoked"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(0).getId(),
                "the commoner's subevent should match the effect specified in the subevent json"
        );
    }

    @Test
    @DisplayName("replaces fewer temporary hit points and gives rider")
    void replacesFewerTemporaryHitPointsAndGivesRider() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("temporary", 5);

        new GiveTemporaryHitPoints()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "temporary_hit_points": [
                            {
                                "formula": "range",
                                "dice": [ ],
                                "bonus": 10
                            }
                        ],
                        "rider_effects": [
                            "std:common/damage/immunity/fire"
                        ]
                    }*/
                    this.putJsonArray("temporary_hit_points", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("formula", "range");
                            this.putJsonArray("dice", new JsonArray());
                            this.putInteger("bonus", 10);
                        }});
                    }});
                    this.putJsonArray("rider_effects", new JsonArray() {{
                        this.addString("std:common/damage/immunity/fire");
                    }});
                }})
                .setSource(source)
                .prepare(context, TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(context, TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(10, target.getHealthData().getInteger("temporary"),
                "the subevent should grant 10 temporary hit points to the target"
        );
        List<RPGLEffect> effects = target.getEffectObjects();
        assertEquals(1, effects.size(),
                "commoner should have 1 effect after the subevent is invoked"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(0).getId(),
                "the commoner's subevent should match the effect specified in the subevent json"
        );
    }

    @Test
    @DisplayName("fails to replace larger temporary hit points or apply rider")
    void failsToReplaceLargerTemporaryHitPointsOrApplyRider() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("temporary", 15);

        new GiveTemporaryHitPoints()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "temporary_hit_points": [
                            {
                                "formula": "range",
                                "dice": [ ],
                                "bonus": 10
                            }
                        ],
                        "rider_effects": [
                            "std:common/damage/immunity/fire"
                        ]
                    }*/
                    this.putJsonArray("temporary_hit_points", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("formula", "range");
                            this.putJsonArray("dice", new JsonArray());
                            this.putInteger("bonus", 10);
                        }});
                    }});
                    this.putJsonArray("rider_effects", new JsonArray() {{
                        this.addString("std:common/damage/immunity/fire");
                    }});
                }})
                .setSource(source)
                .prepare(context, TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(context, TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(15, target.getHealthData().getInteger("temporary"),
                "the subevent should not grant new temporary hit points to the target"
        );
        assertEquals(0, target.getEffectObjects().size(),
                "commoner should have 0 effects after the subevent is invoked"
        );
    }

}
