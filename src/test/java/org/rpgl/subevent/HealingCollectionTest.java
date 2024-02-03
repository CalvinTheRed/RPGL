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
        Subevent subevent = new HealingCollection()
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
    @DisplayName("returns healing collection")
    void returnsHealingCollection(){
        HealingCollection healingCollection = new HealingCollection()
                .joinSubeventData(new JsonObject() {{
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
    @DisplayName("adds healing")
    void addsHealing() {
        HealingCollection healingCollection = new HealingCollection()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "healing": [ ]
                    }*/
                    this.putJsonArray("healing", new JsonArray());
                }})
                .addHealing(new JsonObject() {{
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
                }});

        String expected = """
                [{"bonus":2,"dice":[{"size":6}]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "extra healing dice and bonus should be applied properly"
        );

    }

    @Test
    @DisplayName("defaults to empty array")
    void defaultsToEmptyArray() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        HealingCollection healingCollection = new HealingCollection()
                .setSource(source)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                []""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should default to [ ] if no healing is defined"
        );
    }

    @Test
    @DisplayName("interprets healing formulae")
    void interpretsHealingFormulae() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        HealingCollection healingCollection = new HealingCollection()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(source);

        healingCollection.prepareHealing(new DummyContext());

        String expected = """
                [{"bonus":10,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "prepare should correctly interpret healing instructions"
        );
    }

}
