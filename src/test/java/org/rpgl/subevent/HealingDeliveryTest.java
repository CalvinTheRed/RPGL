package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.HealingDelivery class.
 *
 * @author Calvin Withun
 */
public class HealingDeliveryTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
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
        Subevent subevent = new HealingDelivery();
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
    @DisplayName("getHealing returns stored healing amount")
    void getHealing_returnsStoredDamageValues() {
        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "bonus": 2,
                        "dice": [
                            { "roll": 3, "size": 6 }
                        ]
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("bonus", 2);
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 3);
                            this.putInteger("size", 6);
                        }});
                    }});
                }});
            }});
        }});

        assertEquals(5, healingDelivery.getHealing(),
                "getHealing should return the healing being conveyed by the subevent"
        );
    }

    @Test
    @DisplayName("maximizeHealingDice maximizes all healing dice")
    void maximizeHealingDice_maximizesAllHealingDice() {
        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "roll": 1, "size": 4 },
                            { "roll": 1, "size": 6 },
                            { "roll": 1, "size": 8 }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 4);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 8);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        healingDelivery.maximizeHealingDice();

        assertEquals(4+6+8, healingDelivery.getHealing(),
                "getHealing should return the maximum healing possible given die sizes"
        );
    }

}
