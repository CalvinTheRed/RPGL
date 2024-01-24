package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.EquippedItemHasTag class.
 *
 * @author Calvin Withun
 */
public class EquippedItemHasTagTest {

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
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new EquippedItemHasTag().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates false (empty slot)")
    void evaluatesFalse_emptySlot() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);

        assertFalse(new EquippedItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": [ "mainhand" ],
                "tag": "test"
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonArray("slot", new JsonArray() {{
                this.addString("mainhand");
            }});
            this.putString("tag", "test");
        }}, new DummyContext()),
                "condition should evaluate false when indicated slot is empty"
        );

        assertFalse(new EquippedItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": [ "mainhand" ],
                "tag": "test",
                "invert": true
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonArray("slot", new JsonArray() {{
                this.addString("mainhand");
            }});
            this.putString("tag", "test");
            this.putBoolean("invert", true);
        }}, new DummyContext()),
                "condition should evaluate false when indicated slot is empty"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item;

        item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "offhand");

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);

        assertTrue(new EquippedItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": [ "mainhand", "offhand" ],
                "tag": "test"
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonArray("slot", new JsonArray() {{
                this.addString("mainhand");
                this.addString("offhand");
            }});
            this.putString("tag", "test");
        }}, new DummyContext()),
                "condition should evaluate true when an item in an indicated slot has tag"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);

        assertFalse(new EquippedItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": [ "mainhand" ],
                "tag": "test"
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonArray("slot", new JsonArray() {{
                this.addString("mainhand");
            }});
            this.putString("tag", "test");
        }}, new DummyContext()),
                "condition should evaluate false when no items in the indicated slots have tag"
        );
    }

    @Test
    @DisplayName("evaluates true (inverted)")
    void evaluatesTrue_inverted() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item;

        item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "offhand");

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);

        assertTrue(new EquippedItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": [ "mainhand", "offhand" ],
                "tag": "test",
                "invert": true
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonArray("slot", new JsonArray() {{
                this.addString("mainhand");
                this.addString("offhand");
            }});
            this.putString("tag", "test");
            this.putBoolean("invert", true);
        }}, new DummyContext()),
                "condition should evaluate true when an item in an indicated slot does not have tag"
        );
    }

    @Test
    @DisplayName("evaluates false (inverted)")
    void evaluatesFalse_inverted() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item;

        item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "offhand");

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);

        assertFalse(new EquippedItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": [ "mainhand", "offhand" ],
                "tag": "test",
                "invert": true
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonArray("slot", new JsonArray() {{
                this.addString("mainhand");
                this.addString("offhand");
            }});
            this.putString("tag", "test");
            this.putBoolean("invert", true);
        }}, new DummyContext()),
                "condition should evaluate false when an item in an indicated slot has tag"
        );
    }
}
