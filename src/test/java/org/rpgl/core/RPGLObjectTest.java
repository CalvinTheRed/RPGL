package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.junit.jupiter.api.*;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.function.DummyFunction;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for core.RPGLObject class.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTest {

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
        DummyFunction.resetCounter();
    }

    @Test
    @DisplayName("Object can add and remove effects")
    void test1() {
        RPGLObject object = RPGLFactory.newObject("test:blank");
        RPGLEffect effect = RPGLFactory.newEffect("test:dummy");
        assert object != null;
        assert effect != null;
        assertTrue(object.addEffect(effect),
                "RPGLObject should be able to add a new Effect to itself."
        );
        assertTrue(object.removeEffect(effect),
                "RPGLObject should be able to remove an Effect which was applied to it."
        );
    }

    @Test
    @DisplayName("Object can report effects applied to it")
    void test2() {
        RPGLObject object = RPGLFactory.newObject("test:blank");
        RPGLEffect effect1 = RPGLFactory.newEffect("test:dummy");
        RPGLEffect effect2 = RPGLFactory.newEffect("test:dummy");
        assert object != null;
        assert effect1 != null;
        assert effect2 != null;
        object.addEffect(effect1);
        object.addEffect(effect2);
        RPGLEffect[] effectsArray = object.getEffects();

        assertEquals(effect1, effectsArray[0],
                "RPGLObject should have particular Effect applied at index 0."
        );
        assertEquals(effect2, effectsArray[1],
                "RPGLObject should have particular Effect applied at index 1."
        );
    }

    @Test
    @DisplayName("Object can report proficiency bonus")
    void test3() throws Exception {
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        JsonArray contextArray = new JsonArray();
        assert object != null;
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        assertEquals(2, object.getProficiencyBonus(context),
                "RPGLObject should have a proficiency bonus of +2."
        );
    }

    @Test
    @DisplayName("Object can calculate ability score modifiers")
    void test4() {
        assertEquals(-2L, RPGLObject.getAbilityModifierFromAbilityScore(7L),
                "Ability score of 7 should have a modifier of -2."
        );
        assertEquals(-1L, RPGLObject.getAbilityModifierFromAbilityScore(8L),
                "Ability score of 8 should have a modifier of -1."
        );
        assertEquals(-1L, RPGLObject.getAbilityModifierFromAbilityScore(9L),
                "Ability score of 9 should have a modifier of -1."
        );
        assertEquals(0L, RPGLObject.getAbilityModifierFromAbilityScore(10L),
                "Ability score of 10 should have a modifier of 0."
        );
        assertEquals(0L, RPGLObject.getAbilityModifierFromAbilityScore(11L),
                "Ability score of 11 should have a modifier of 0."
        );
        assertEquals(1L, RPGLObject.getAbilityModifierFromAbilityScore(12L),
                "Ability score of 12 should have a modifier of +1."
        );
        assertEquals(1L, RPGLObject.getAbilityModifierFromAbilityScore(13L),
                "Ability score of 13 should have a modifier of +1."
        );
        assertEquals(2L, RPGLObject.getAbilityModifierFromAbilityScore(14L),
                "Ability score of 14 should have a modifier of +2."
        );
    }

    @Test
    @DisplayName("Object can calculate ability score modifiers from ability scores")
    void test5() throws Exception {
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        JsonArray contextArray = new JsonArray();
        assert object != null;
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        assertEquals(1L, object.getAbilityModifierFromAbilityScore(context, "str"),
                "str score of 12 should have modifier of +1."
        );
    }

    @Test
    @DisplayName("Object can process dummy Event without error")
    void test6() throws Exception {
        RPGLObject object = RPGLFactory.newObject("test:blank");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        object.invokeEvent(
                new RPGLObject[] {object},
                Objects.requireNonNull(RPGLFactory.newEvent("test:dummy")),
                context
        );
    }

    @Test
    @DisplayName("Object can process dummy Event with dummy Effect")
    void test7() throws Exception {
        RPGLObject object = RPGLFactory.newObject("test:blank");
        RPGLEffect effect = RPGLFactory.newEffect("test:dummy");
        assert object != null;
        assert effect != null;
        object.addEffect(effect);
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        object.invokeEvent(
                new RPGLObject[] {object},
                Objects.requireNonNull(RPGLFactory.newEvent("test:dummy")),
                context
        );

        assertEquals(1, DummyFunction.counter,
                "Effect dummy:dummy should increment DummyFunction counter when Event dummy:dummy is invoked."
        );
    }

    @Test
    @DisplayName("Object can process dummy Event with 2 dummy Effects")
    void test8() throws Exception {
        RPGLObject object = RPGLFactory.newObject("test:blank");
        RPGLEffect effect1 = RPGLFactory.newEffect("test:dummy");
        RPGLEffect effect2 = RPGLFactory.newEffect("test:dummy");
        assert object != null;
        assert effect1 != null;
        assert effect2 != null;
        object.addEffect(effect1);
        object.addEffect(effect2);
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        RPGLEvent event = RPGLFactory.newEvent("test:dummy");
        assert event != null;
        object.invokeEvent(new RPGLObject[] {object}, event, context);

        assertEquals(1, DummyFunction.counter,
                "RPGLEffect test:dummy should increment DummyFunction counter when RPGLEvent test:dummy is invoked, but only once."
        );
    }

}
