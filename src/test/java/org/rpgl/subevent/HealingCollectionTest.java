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
 * Testing class for the org.rpgl.subevent.HealingCollection class.
 *
 * @author Calvin Withun
 */
public class HealingCollectionTest {

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
        Subevent subevent = new HealingCollection();
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
    @DisplayName("getHealingCollection returns object storing bonus and dice")
    void getHealingCollection_returnsObjectStoringBonusAndDice(){
        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "roll": 1 },
                            { "roll": 6 }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
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
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "getHealingCollection should return an object with the subevent's bonus and dice stored inside"
        );
    }

    @Test
    @DisplayName("addHealing extra healing is added properly")
    void addHealing_extraHealingIsAddedProperly() {
        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [ ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        JsonObject extraHealing = new JsonObject() {{
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

        healingCollection.addHealing(extraHealing);
        expected = """
                [{"bonus":0,"dice":[]},{"bonus":2,"dice":[{"size":6}]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "extra healing dice and bonus should be applied properly"
        );

        healingCollection.addHealing(extraHealing);
        expected = """
                [{"bonus":0,"dice":[]},{"bonus":2,"dice":[{"size":6}]},{"bonus":2,"dice":[{"size":6}]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "extra healing dice and bonus should not delete or override any old data"
        );
    }

    @Test
    @DisplayName("prepare sets default values")
    void prepare_setsDefaultValues() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_collection"
            }*/
            this.putString("subevent", "healing_collection");
        }});

        healingCollection.setSource(source);
        healingCollection.prepare(context);

        String expected = """
                []""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should default to [ ] if no healing is defined"
        );
    }

    @Test
    @DisplayName("prepareHealing interprets healing (range)")
    void prepareHealing_interpretsHealing_range() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_collection",
                "healing": [
                    {
                        "healing_formula": "range",
                        "dice": [ ],
                        "bonus": 10
                    }
                ]
            }*/
            this.putString("subevent", "healing_collection");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("healing_formula", "range");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
        }});

        healingCollection.setSource(source);
        healingCollection.prepareHealing(context);

        String expected = """
                [{"bonus":10,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should correctly interpret healing instructions"
        );
    }

    @Test
    @DisplayName("prepareHealing interprets healing (modifier)")
    void prepareHealing_interpretsHealing_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        source.getAbilityScores().putInteger("dex", 20);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_collection",
                "healing": [
                    {
                        "healing_formula": "modifier",
                        "ability": "dex",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putString("subevent", "healing_collection");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("healing_formula", "modifier");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        healingCollection.setSource(source);
        healingCollection.prepareHealing(context);

        String expected = """
                [{"bonus":5,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should correctly interpret healing instructions"
        );
    }

    @Test
    @DisplayName("prepareHealing interprets healing (ability)")
    void prepareHealing_interpretsHealing_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        source.getAbilityScores().putInteger("dex", 20);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_collection",
                "healing": [
                    {
                        "healing_formula": "ability",
                        "ability": "dex",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putString("subevent", "healing_collection");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("healing_formula", "ability");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        healingCollection.setSource(source);
        healingCollection.prepareHealing(context);

        String expected = """
                [{"bonus":20,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should correctly interpret healing instructions"
        );
    }

    @Test
    @DisplayName("prepareHealing interprets healing (proficiency)")
    void prepareHealing_interpretsHealing_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_collection",
                "healing": [
                    {
                        "healing_formula": "proficiency",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putString("subevent", "healing_collection");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("healing_formula", "proficiency");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        healingCollection.setSource(source);
        healingCollection.prepareHealing(context);

        String expected = """
                [{"bonus":2,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should correctly interpret healing instructions"
        );
    }

}
