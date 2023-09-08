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
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.OriginItemHasTag class.
 *
 * @author Calvin Withun
 */
public class OriginItemHasTagTest {

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
        Condition condition = new OriginItemHasTag();
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
    @DisplayName("evaluate returns true when origin item has tag (subevent origin item)")
    void evaluate_returnsTrueWhenOriginItemHasTag_subeventOriginItem() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new OriginItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "subevent",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "subevent");
            this.putString("tag", "test");
        }};

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);
        subevent.setOriginItem(item.getUuid());

        assertTrue(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate true when indicated tag is present"
        );
    }

    @Test
    @DisplayName("evaluate returns true when origin item has tag (effect origin item)")
    void evaluate_returnsTrueWhenOriginItemHasTag_effectOriginItem() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new OriginItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "effect",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "effect");
            this.putString("tag", "test");
        }};

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);
        effect.setOriginItem(item.getUuid());

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);

        assertTrue(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate true when indicated tag is present"
        );
    }

    @Test
    @DisplayName("evaluate returns false when origin item does not have tag (subevent origin item)")
    void evaluate_returnsFalseWhenOriginItemDoesNotHaveTag_subeventOriginItem() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new OriginItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "subevent",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "subevent");
            this.putString("tag", "test");
        }};

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);
        subevent.setOriginItem(item.getUuid());

        assertFalse(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate false when indicated tag is not present"
        );
    }

    @Test
    @DisplayName("evaluate returns false when origin item does not have tag (effect origin item)")
    void evaluate_returnsFalseWhenOriginItemDoesNotHaveTag_effectOriginItem() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(object);

        Condition condition = new OriginItemHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "effect",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "effect");
            this.putString("tag", "test");
        }};

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);
        effect.setOriginItem(item.getUuid());

        Subevent subevent = new DummySubevent();
        subevent.setSource(object);
        subevent.setTarget(object);

        assertFalse(condition.evaluate(effect, subevent, conditionJson, context),
                "condition should evaluate false when indicated tag is not present"
        );
    }

}
