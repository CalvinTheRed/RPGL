package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
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
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new OriginItemHasTag().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true (subevent origin item)")
    void evaluatesTrue_subeventOriginItem() throws Exception {
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");

        Subevent subevent = new DummySubevent();
        subevent.setOriginItem(item.getUuid());

        assertTrue(new OriginItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "subevent",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "subevent");
            this.putString("tag", "test");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "should evaluate true when indicated tag is present"
        );
    }

    @Test
    @DisplayName("evaluates false (subevent origin item)")
    void evaluatesFalse_subeventOriginItem() throws Exception {
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");

        Subevent subevent = new DummySubevent();
        subevent.setOriginItem(item.getUuid());

        assertFalse(new OriginItemHasTag().evaluate(null, subevent, new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "subevent",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "subevent");
            this.putString("tag", "test");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "should evaluate false when indicated tag is not present"
        );
    }

    @Test
    @DisplayName("evaluates true (effect origin item)")
    void evaluatesTrue_effectOriginItem() throws Exception {
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        item.addTag("test");

        RPGLEffect effect = new RPGLEffect();
        effect.setOriginItem(item.getUuid());

        assertTrue(new OriginItemHasTag().evaluate(effect, null, new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "effect",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "effect");
            this.putString("tag", "test");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "should evaluate true when indicated tag is present"
        );
    }

    @Test
    @DisplayName("evaluates false (effect origin item)")
    void evaluatesFalse_effectOriginItem() throws Exception {
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");

        RPGLEffect effect = new RPGLEffect();
        effect.setOriginItem(item.getUuid());

        assertFalse(new OriginItemHasTag().evaluate(effect, null, new JsonObject() {{
            /*{
                "condition": "origin_item_has_tag",
                "origin_item": "effect",
                "tag": "test"
            }*/
            this.putString("condition", "origin_item_has_tag");
            this.putString("origin_item", "effect");
            this.putString("tag", "test");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "should evaluate false when indicated tag is not present"
        );
    }

}
