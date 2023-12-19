package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.TemporaryHitPointsDelivery class.
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointsDeliveryTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new TemporaryHitPointsDelivery();
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
    @DisplayName("getTemporaryHitPoints returns stored temporary hit point amount")
    void getTemporaryHitPoints_returnsStoredDamageValues() {
        TemporaryHitPointsDelivery temporaryHitPointsDelivery = new TemporaryHitPointsDelivery();
        temporaryHitPointsDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "bonus": 2,
                        "dice": [
                            { "roll": 3, "size": 6 }
                        ]
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
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

        assertEquals(5, temporaryHitPointsDelivery.getTemporaryHitPoints(),
                "getTemporaryHitPoints should return the temporary hit points being conveyed by the subevent"
        );
    }

    @Test
    @DisplayName("maximizeTemporaryHitPointDice maximizes all temporary hit point dice")
    void maximizeTemporaryHitPointDice_maximizesAllTemporaryHitPointDice() {
        TemporaryHitPointsDelivery temporaryHitPointsDelivery = new TemporaryHitPointsDelivery();
        temporaryHitPointsDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
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
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
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

        temporaryHitPointsDelivery.maximizeTemporaryHitPointDice();

        assertEquals(4+6+8, temporaryHitPointsDelivery.getTemporaryHitPoints(),
                "getTemporaryHitPoints should return the maximum temporary hit points possible given die sizes"
        );
    }

}
