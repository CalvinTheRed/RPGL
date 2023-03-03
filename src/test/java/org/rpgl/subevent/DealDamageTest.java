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
 * Testing class for the org.rpgl.subevent.DealDamage class.
 *
 * @author Calvin Withun
 */
public class DealDamageTest {

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
        Subevent subevent = new DealDamage();
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
    @DisplayName("deliverDamage damage is delivered")
    void deliverDamage_damageIsDelivered() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "deal_damage",
                "tags": [ ],
                "damage": {
                    "cold": 10
                }
            }*/
            this.putString("subevent", "deal_damage");
            this.putJsonArray("tags", new JsonArray());
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.setTarget(target);
        dealDamage.deliverDamage(context);

        assertEquals(42, target.getHealthData().getInteger("current"),
                "10 damage should be delivered (52-10=42)"
        );
    }

    @Test
    @DisplayName("getTargetDamage no target damage by default")
    void getTargetDamage_noTargetDamageByDefault() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "deal_damage",
                "tags": [ ]
            }*/
            this.putString("subevent", "deal_damage");
            this.putJsonArray("tags", new JsonArray());
        }});

        dealDamage.setSource(source);
        dealDamage.setTarget(target);
        JsonObject targetDamage = dealDamage.getTargetDamage(context);

        assertEquals("{}", targetDamage.toString(),
                "target damage object should be empty by default"
        );
    }

    @Test
    @DisplayName("getBaseDamage base damage calculated correctly")
    void getBaseDamage_baseDamageCalculatedProperly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "deal_damage",
                "tags": [ ],
                "damage": [
                    {
                        "type": "force",
                        "dice": [
                            { "count": 1, "size": 4, "determined": [ 2 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putString("subevent", "deal_damage");
            this.putJsonArray("tags", new JsonArray());
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "force");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
                            this.putInteger("size", 4);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.setTarget(target);
        dealDamage.getBaseDamage(context);

        String expected = """
                {"force":3}""";
        assertEquals(expected, dealDamage.json.getJsonObject("damage").toString(),
                "base damage should be 3 force damage"
        );
    }

    @Test
    @DisplayName("invoke deals correct damage")
    void invoke_dealsCorrectDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();
        dealDamage.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "deal_damage",
                "damage": [
                    {
                        "type": "force",
                        "dice": [
                            { "count": 1, "size": 4, "determined": [ 2 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putString("subevent", "deal_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "force");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
                            this.putInteger("size", 4);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});

        dealDamage.setSource(source);
        dealDamage.prepare(context);
        dealDamage.setTarget(target);
        dealDamage.invoke(context);

        assertEquals(49, target.getHealthData().getInteger("current"),
                "invoking DealDamage should deal 3 points of damage (52-3=49)"
        );
    }

}
