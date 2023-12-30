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
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.ObjectWieldingOriginItem class.
 *
 * @author Calvin Withun
 */
public class ObjectWieldingOriginItemTest {

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new ObjectWieldingOriginItem().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates false (origin item null)")
    void evaluatesFalse_originItemNull() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        assertFalse(new ObjectWieldingOriginItem().evaluate(effect, null, new JsonObject() {{
            /*{
                "condition": "object_wielding_origin_item",
                "object": {
                    "from": "effect",
                    "object": "source"
                }
            }*/
            this.putString("condition", "object_wielding_origin_item");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "effect");
                this.putString("object", "source");
            }});
        }}, new DummyContext()),
                "should evaluate false when origin item is null"
        );
    }

    @Test
    @DisplayName("evaluates false (origin item not wielded)")
    void evaluatesFalse_originItemNotWielded() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        object.giveItem(item.getUuid());

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);
        effect.setOriginItem(item.getUuid());

        assertFalse(new ObjectWieldingOriginItem().evaluate(effect, null, new JsonObject() {{
            /*{
                "condition": "object_wielding_origin_item",
                "object": {
                    "from": "effect",
                    "object": "source"
                }
            }*/
            this.putString("condition", "object_wielding_origin_item");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "effect");
                this.putString("object", "source");
            }});
        }}, new DummyContext()),
                "should evaluate false when origin item is not wielded"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);
        effect.setOriginItem(item.getUuid());

        assertTrue(new ObjectWieldingOriginItem().evaluate(effect, null, new JsonObject() {{
            /*{
                "condition": "object_wielding_origin_item",
                "object": {
                    "from": "effect",
                    "object": "source"
                }
            }*/
            this.putString("condition", "object_wielding_origin_item");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "effect");
                this.putString("object", "source");
            }});
        }}, new DummyContext()),
                "should evaluate true when origin item is being wielded"
        );
    }

}
