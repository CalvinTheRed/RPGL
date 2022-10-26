package org.rpgl.core;

import org.junit.jupiter.api.*;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.function.DummyFunction;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(2, object.getProficiencyBonus(),
                "Object dummy:dummy_hollow should have a proficiency bonus of +2."
        );
    }

    @Test
    @DisplayName("Object can calculate ability score modifiers")
    void test4() {
        assertEquals(-2L, RPGLObject.getAbilityModifier(7L),
                "Ability score of 7 should have a modifier of -2."
        );
        assertEquals(-1L, RPGLObject.getAbilityModifier(8L),
                "Ability score of 8 should have a modifier of -1."
        );
        assertEquals(-1L, RPGLObject.getAbilityModifier(9L),
                "Ability score of 9 should have a modifier of -1."
        );
        assertEquals(0L, RPGLObject.getAbilityModifier(10L),
                "Ability score of 10 should have a modifier of 0."
        );
        assertEquals(0L, RPGLObject.getAbilityModifier(11L),
                "Ability score of 11 should have a modifier of 0."
        );
        assertEquals(1L, RPGLObject.getAbilityModifier(12L),
                "Ability score of 12 should have a modifier of +1."
        );
        assertEquals(1L, RPGLObject.getAbilityModifier(13L),
                "Ability score of 13 should have a modifier of +1."
        );
        assertEquals(2L, RPGLObject.getAbilityModifier(14L),
                "Ability score of 14 should have a modifier of +2."
        );
    }

    @Test
    @DisplayName("Object can calculate ability score modifiers from ability scores")
    void test5() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assertEquals(-2L, object.getAbilityModifier("str"),
                "Object dummy:dummy_hollow str score of 7 should have modifier of -2."
        );
        assertEquals(-1L, object.getAbilityModifier("dex"),
                "Object dummy:dummy_hollow dex score of 8 should have modifier of -1."
        );
        assertEquals(-1L, object.getAbilityModifier("con"),
                "Object dummy:dummy_hollow con score of 9 should have modifier of -1."
        );
        assertEquals(0L, object.getAbilityModifier("int"),
                "Object dummy:dummy_hollow int score of 10 should have modifier of 0."
        );
        assertEquals(0L, object.getAbilityModifier("wis"),
                "Object dummy:dummy_hollow wis score of 11 should have modifier of 0."
        );
        assertEquals(1L, object.getAbilityModifier("cha"),
                "Object dummy:dummy_hollow cha score of 12 should have modifier of +1."
        );
    }

    @Test
    @DisplayName("Object can process dummy Event without error")
    void test6() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLEvent event = RPGLFactory.newEvent("dummy:dummy");
        object.invokeEvent(new RPGLObject[] {object}, event);
    }

    @Test
    @DisplayName("Object can process dummy Event with dummy Effect")
    void test7() throws Exception {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLEffect effect = RPGLFactory.newEffect("dummy:dummy");
        object.addEffect(effect);
        UUIDTable.register(effect);

        RPGLEvent event = RPGLFactory.newEvent("dummy:dummy");
        object.invokeEvent(new RPGLObject[] {object}, event);

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
        object.addEffect(effect1);
        object.addEffect(effect2);
        UUIDTable.register(effect1);
        UUIDTable.register(effect2);

        RPGLEvent event = RPGLFactory.newEvent("dummy:dummy");
        object.invokeEvent(new RPGLObject[] {object}, event);

        assertEquals(1, DummyFunction.counter,
                "Effect dummy:dummy should increment DummyFunction counter when Event dummy:dummy is invoked, but only once."
        );
    }

}
