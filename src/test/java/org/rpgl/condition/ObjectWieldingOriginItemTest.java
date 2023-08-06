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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new ObjectWieldingOriginItem();
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
    @DisplayName("evaluate returns false when origin item is null")
    void evaluate_returnsFalseWhenOriginItemIsNull() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        Condition condition = new ObjectWieldingOriginItem();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "object_wielding_origin_item",
                "object": {
                    "object": "source",
                    "from": "effect"
                }
            }*/
            this.putString("condition", "object_wielding_origin_item");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("object", "source");
                this.putString("from", "effect");
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);

        assertFalse(condition.evaluate(effect, null, conditionJson, context),
                "should return false when there is no origin item"
        );
    }

    @Test
    @DisplayName("evaluate returns false when origin item is not being wielded")
    void evaluate_returnsFalseWhenOriginItemIsNotBeingWielded() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        object.giveItem(item.getUuid());

        Condition condition = new ObjectWieldingOriginItem();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "object_wielding_origin_item",
                "object": {
                    "object": "source",
                    "from": "effect"
                }
            }*/
            this.putString("condition", "object_wielding_origin_item");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("object", "source");
                this.putString("from", "effect");
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);
        effect.setOriginItem(item.getUuid());

        assertFalse(condition.evaluate(effect, null, conditionJson, context),
                "should return false when origin item is not wielded"
        );
    }

    @Test
    @DisplayName("evaluate returns true when origin item is being wielded")
    void evaluate_returnsTrueWhenOriginItemIsBeingWielded() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        Condition condition = new ObjectWieldingOriginItem();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "object_wielding_origin_item",
                "object": {
                    "object": "source",
                    "from": "effect"
                }
            }*/
            this.putString("condition", "object_wielding_origin_item");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("object", "source");
                this.putString("from", "effect");
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);
        effect.setTarget(object);
        effect.setOriginItem(item.getUuid());

        assertTrue(condition.evaluate(effect, null, conditionJson, context),
                "should return true when origin item is wielded"
        );
    }

}
