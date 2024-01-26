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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.TemporaryHitPointCollection class.
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointCollectionTest {

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
        Subevent subevent = new TemporaryHitPointCollection();
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
    @DisplayName("returns temporary hit points")
    void returnsTemporaryHitPoints(){
        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [
                            { "roll": 1 },
                            { "roll": 6 }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 6);
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        String expected = """
                [{"bonus":2,"dice":[{"roll":1},{"roll":6}]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "getHealingCollection should return an object with the subevent's bonus and dice stored inside"
        );
    }

    @Test
    @DisplayName("adds temporary hit points")
    void addsTemporaryHitPoints() {
        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [ ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        JsonObject extraTemporaryHitPoints = new JsonObject() {{
            /*{
                "dice": [
                    { "size": 6 }
                ],
                "bonus": 2
            }*/
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 6);
                }});
            }});
            this.putInteger("bonus", 2);
        }};
        String expected;

        temporaryHitPointCollection.addTemporaryHitPoints(extraTemporaryHitPoints);
        expected = """
                [{"bonus":0,"dice":[]},{"bonus":2,"dice":[{"size":6}]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "extra temporary hit point dice and bonus should be applied properly"
        );

        temporaryHitPointCollection.addTemporaryHitPoints(extraTemporaryHitPoints);
        expected = """
                [{"bonus":0,"dice":[]},{"bonus":2,"dice":[{"size":6}]},{"bonus":2,"dice":[{"size":6}]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "extra temporary hit point dice and bonus should not delete or override any old data"
        );
    }

    @Test
    @DisplayName("defaults to empty array")
    void defaultsToEmptyArray() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepare(new DummyContext());

        String expected = """
                []""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should default to [ ] if no temporary hit points are defined"
        );
    }

    @Test
    @DisplayName("interprets temporary hit point formulae")
    void prepareTemporaryHitPoints_interpretsTemporaryHitPoints() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "formula": "range",
                        "dice": [ ],
                        "bonus": 10
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
        }});

        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepareTemporaryHitPoints(new DummyContext());

        String expected = """
                [{"bonus":10,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should correctly interpret temporary hit points instructions"
        );
    }

}
