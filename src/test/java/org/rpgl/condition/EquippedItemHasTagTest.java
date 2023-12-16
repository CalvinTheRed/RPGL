package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.ConditionMismatchException;
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
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new EquippedItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        DummyContext context = new DummyContext();

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson, context),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate returns false when slot is empty")
    void evaluate_returnsFalseWhenSlotIsEmpty() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new EquippedItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": "mainhand",
                "tag": "test"
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("slot", "mainhand");
            this.putString("tag", "test");
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);

        assertFalse(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate false when indicated slot is empty"
        );
    }

    @Test
    @DisplayName("evaluate returns true when equipped item has tag")
    void evaluate_returnsTrueWhenEquippedItemHasTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new EquippedItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": "mainhand",
                "tag": "test"
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("slot", "mainhand");
            this.putString("tag", "test");
        }};

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);

        assertTrue(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate true when indicated tag is present"
        );
    }

    @Test
    @DisplayName("evaluate returns false when equipped item does not have tag")
    void evaluate_returnsFalseWhenEquippedItemDoesNotHaveTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new EquippedItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "equipped_item_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "slot": "mainhand",
                "tag": "test"
            }*/
            this.putString("condition", "equipped_item_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("slot", "mainhand");
            this.putString("tag", "test");
        }};

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);

        assertFalse(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate false when indicated tag is not present"
        );
    }

}
