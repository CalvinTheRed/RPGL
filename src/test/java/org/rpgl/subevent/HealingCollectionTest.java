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
 * Testing class for the org.rpgl.subevent.HealingCollection class.
 *
 * @author Calvin Withun
 */
public class HealingCollectionTest {

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
        Subevent subevent = new HealingCollection();
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
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        String expected = """
                []""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should default to [ ] if no healing is defined"
        );
    }

    @Test
    @DisplayName("prepareHealing interprets healing correctly")
    void prepareHealing_interpretsHealingCorrectly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [ ],
                        "bonus": 10
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
        }});

        healingCollection.setSource(source);
        healingCollection.prepareHealing(context);

        String expected = """
                [{"bonus":10,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should correctly interpret healing instructions"
        );
    }

}
