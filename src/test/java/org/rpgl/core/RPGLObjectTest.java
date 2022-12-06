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
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLEffect effect = RPGLFactory.newEffect("dummy:dummy");
        assert object != null;
        assert effect != null;
        assertTrue(object.addEffect(effect),
                "Object dummy:dummy_hollow should be able to add a new Effect to itself."
        );
        assertTrue(object.removeEffect(effect),
                "Object dummy:dummy_hollow should be able to remove an Effect applied to it."
        );
    }

    @Test
    @DisplayName("Object can report effects applied to it")
    void test2() {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLEffect effect1 = RPGLFactory.newEffect("dummy:dummy");
        RPGLEffect effect2 = RPGLFactory.newEffect("dummy:dummy");
        assert object != null;
        assert effect1 != null;
        assert effect2 != null;
        object.addEffect(effect1);
        object.addEffect(effect2);
        RPGLEffect[] effectsArray = object.getEffects();

        assertEquals(effect1, effectsArray[0],
                "Object dummy:dummy_hollow should have particular Effect dummy:dummy applied at index 0."
        );
        assertEquals(effect2, effectsArray[1],
                "Object dummy:dummy_hollow should have particular Effect dummy:dummy applied at index 1."
        );
    }

    @Test
    @DisplayName("Object can report proficiency bonus")
    void test3() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        assert object != null;
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        assertEquals(2, object.getProficiencyBonus(context),
                "Object dummy:dummy_hollow should have a proficiency bonus of +2."
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
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        assert object != null;
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        assertEquals(-2L, object.getAbilityModifierFromAbilityScore(context, "str"),
                "Object dummy:dummy_hollow str score of 7 should have modifier of -2."
        );
        assertEquals(-1L, object.getAbilityModifierFromAbilityScore(context, "dex"),
                "Object dummy:dummy_hollow dex score of 8 should have modifier of -1."
        );
        assertEquals(-1L, object.getAbilityModifierFromAbilityScore(context, "con"),
                "Object dummy:dummy_hollow con score of 9 should have modifier of -1."
        );
        assertEquals(0L, object.getAbilityModifierFromAbilityScore(context, "int"),
                "Object dummy:dummy_hollow int score of 10 should have modifier of 0."
        );
        assertEquals(0L, object.getAbilityModifierFromAbilityScore(context, "wis"),
                "Object dummy:dummy_hollow wis score of 11 should have modifier of 0."
        );
        assertEquals(1L, object.getAbilityModifierFromAbilityScore(context, "cha"),
                "Object dummy:dummy_hollow cha score of 12 should have modifier of +1."
        );
    }

    @Test
    @DisplayName("Object can process dummy Event without error")
    void test6() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        RPGLEvent event = RPGLFactory.newEvent("dummy:dummy");
        assert event != null;
        object.invokeEvent(new RPGLObject[] {object}, event, context);
    }

    @Test
    @DisplayName("Object can process dummy Event with dummy Effect")
    void test7() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLEffect effect = RPGLFactory.newEffect("dummy:dummy");
        assert object != null;
        assert effect != null;
        object.addEffect(effect);
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        RPGLEvent event = RPGLFactory.newEvent("dummy:dummy");
        assert event != null;
        object.invokeEvent(new RPGLObject[] {object}, event, context);

        assertEquals(1, DummyFunction.counter,
                "Effect dummy:dummy should increment DummyFunction counter when Event dummy:dummy is invoked."
        );
    }

    @Test
    @DisplayName("Object can process dummy Event with 2 dummy Effects")
    void test8() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLEffect effect1 = RPGLFactory.newEffect("dummy:dummy");
        RPGLEffect effect2 = RPGLFactory.newEffect("dummy:dummy");
        assert object != null;
        assert effect1 != null;
        assert effect2 != null;
        object.addEffect(effect1);
        object.addEffect(effect2);
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        RPGLEvent event = RPGLFactory.newEvent("dummy:dummy");
        assert event != null;
        object.invokeEvent(new RPGLObject[] {object}, event, context);

        assertEquals(1, DummyFunction.counter,
                "Effect dummy:dummy should increment DummyFunction counter when Event dummy:dummy is invoked, but only once."
        );
    }

}
