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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.TemporaryHitPointCollection class.
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointCollectionTest {

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
    @DisplayName("getTemporaryHitPointCollection returns object storing bonus and dice")
    void getTemporaryHitPointCollection_returnsObjectStoringBonusAndDice(){
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
    @DisplayName("addTemporaryHitPoints extra temporary hit points are added properly")
    void addTemporaryHitPoints_extraTemporaryHitPointsAreAddedProperly() {
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
    @DisplayName("prepare sets default values")
    void prepare_setsDefaultValues() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepare(context);

        String expected = """
                []""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should default to [ ] if no temporary hit points are defined"
        );
    }

    @Test
    @DisplayName("prepareTemporaryHitPoints interprets temporary hit points (range)")
    void prepareTemporaryHitPoints_interpretsTemporaryHitPoints_range() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "temporary_hit_point_formula": "range",
                        "dice": [ ],
                        "bonus": 10
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("temporary_hit_point_formula", "range");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
        }});

        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepareTemporaryHitPoints(context);

        String expected = """
                [{"bonus":10,"dice":[]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should correctly interpret temporary hit points instructions"
        );
    }

    @Test
    @DisplayName("prepareTemporaryHitPoints interprets temporary hit points (modifier)")
    void prepareTemporaryHitPoints_interpretsTemporaryHitPoints_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        source.getAbilityScores().putInteger("dex", 20);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "temporary_hit_point_formula": "modifier",
                        "ability": "dex",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("temporary_hit_point_formula", "modifier");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepareTemporaryHitPoints(context);

        String expected = """
                [{"bonus":5,"dice":[]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should correctly interpret temporary hit points instructions"
        );
    }

    @Test
    @DisplayName("prepareTemporaryHitPoints interprets temporary hit points (ability)")
    void prepareTemporaryHitPoints_interpretsTemporaryHitPoints_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        source.getAbilityScores().putInteger("dex", 20);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "temporary_hit_point_formula": "ability",
                        "ability": "dex",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("temporary_hit_point_formula", "ability");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepareTemporaryHitPoints(context);

        String expected = """
                [{"bonus":20,"dice":[]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should correctly interpret temporary hit points instructions"
        );
    }

    @Test
    @DisplayName("prepareTemporaryHitPoints interprets temporary hit points (proficiency)")
    void prepareTemporaryHitPoints_interpretsTemporaryHitPoints_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        TemporaryHitPointCollection temporaryHitPointCollection = new TemporaryHitPointCollection();
        temporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "temporary_hit_point_formula": "proficiency",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("temporary_hit_point_formula", "proficiency");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        temporaryHitPointCollection.setSource(source);
        temporaryHitPointCollection.prepareTemporaryHitPoints(context);

        String expected = """
                [{"bonus":2,"dice":[]}]""";
        assertEquals(expected, temporaryHitPointCollection.getTemporaryHitPointsCollection().toString(),
                "prepare should correctly interpret temporary hit points instructions"
        );
    }

}
