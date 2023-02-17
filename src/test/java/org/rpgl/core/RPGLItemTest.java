package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPGLItemTest {

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
    }

    @Test
    @DisplayName("get and set attack ability (dex to str)")
    void getSetAttackAbility_changesDexToStr() {
        RPGLItem item = RPGLFactory.newItem("demo:rapier");
        item.setAttackAbility("melee", "str");

        assertEquals("str", item.getAttackAbility("melee"));
    }

    @Test
    @DisplayName("defaultAttackAbilities reset changes (melee)")
    void defaultAttackAbilities_resetChanges_melee() {
        RPGLItem item = RPGLFactory.newItem("demo:rapier");
        item.setAttackAbility("melee", "str");
        item.defaultAttackAbilities();

        assertEquals("dex", item.getAttackAbility("melee"));
    }

    @Test
    @DisplayName("defaultAttackAbilities reset changes (thrown)")
    void defaultAttackAbilities_resetChanges_thrown() {
        RPGLItem item = RPGLFactory.newItem("demo:dagger");
        item.setAttackAbility("thrown", "str");
        item.defaultAttackAbilities();

        assertEquals("dex", item.getAttackAbility("thrown"));
    }

    @Test
    @DisplayName("defaultAttackAbilities reset changes (ranged)")
    void defaultAttackAbilities_resetChanges_ranged() {
        RPGLItem item = RPGLFactory.newItem("demo:heavy_crossbow");
        item.setAttackAbility("ranged", "cha");
        item.defaultAttackAbilities();

        assertEquals("dex", item.getAttackAbility("ranged"));
    }

    @Test
    @DisplayName("getWhileEquippedEffectObjects returns correct effects")
    void getWhileEquippedEffectObjects_returnsCorrectEffects() {
        RPGLItem frostbrand = RPGLFactory.newItem("demo:frostbrand");

        List<RPGLEffect> effects = frostbrand.getWhileEquippedEffectObjects();

        assertEquals(1, effects.size(),
                "demo:frostbrand should have 1 effect while equipped"
        );
        assertEquals("demo:fire_immunity", effects.get(0).getId(),
                "demo:frostbrand should have the demo:fire_immunity effect while equipped"
        );
    }

    @Test
    @DisplayName("updateEquippedEffects effects should store the passed object as its new source and target")
    void updateEquippedEffects_effectsShouldStorePassedObjectAsNewSourceAndTarget() {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        RPGLItem frostbrand = RPGLFactory.newItem("demo:frostbrand");

        frostbrand.updateEquippedEffects(knight);

        assertEquals(knight.getUuid(), frostbrand.getWhileEquippedEffectObjects().get(0).getSource(),
                "demo:frostbrand effects should have demo:knight as a source after updating"
        );
        assertEquals(knight.getUuid(), frostbrand.getWhileEquippedEffectObjects().get(0).getTarget(),
                "demo:frostbrand effects should have demo:knight as a target after updating"
        );
    }

}
