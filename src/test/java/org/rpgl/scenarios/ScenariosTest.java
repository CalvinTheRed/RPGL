package org.rpgl.scenarios;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for miscellaneous scenarios. Tests here are designed to stress test RPGL at a high level.
 *
 * @author Calvin Withun
 */
public class ScenariosTest {

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
    }

    @Test
    @DisplayName("one round of boring combat between knights and a dragon")
    void oneRoundBoringCombatKnightsVersusDragon() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject knight1 = RPGLFactory.newObject("demo:knight");
        RPGLObject knight2 = RPGLFactory.newObject("demo:knight");
        RPGLObject knight3 = RPGLFactory.newObject("demo:knight");
        RPGLObject knight4 = RPGLFactory.newObject("demo:knight");
        RPGLObject knight5 = RPGLFactory.newObject("demo:knight");
        RPGLObject knight6 = RPGLFactory.newObject("demo:knight");

        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        context.add(knight1);
        context.add(knight2);
        context.add(knight3);
        context.add(knight4);
        context.add(knight5);
        context.add(knight6);

        RPGLEvent youngRedDragonFireBreath;
        RPGLEvent mainhandAttack;

        youngRedDragonFireBreath = RPGLFactory.newEvent("demo:young_red_dragon_fire_breath");
        youngRedDragon.invokeEvent(
                new RPGLObject[] {knight1, knight2, knight3, knight4, knight5, knight6},
                youngRedDragonFireBreath,
                youngRedDragon.getResourceObjects(),
                context
        );

        // each target takes 48 damage
        assertEquals(4, knight1.getHealthData().getInteger("current"));
        assertEquals(4, knight2.getHealthData().getInteger("current"));
        assertEquals(4, knight3.getHealthData().getInteger("current"));
        assertEquals(4, knight4.getHealthData().getInteger("current"));
        assertEquals(4, knight5.getHealthData().getInteger("current"));
        assertEquals(4, knight6.getHealthData().getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("std_items:longsword_melee");
        knight1.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, new ArrayList<>(), context);
        assertEquals(171, youngRedDragon.getHealthData().getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("std_items:longsword_melee");
        knight2.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, new ArrayList<>(), context);
        assertEquals(164, youngRedDragon.getHealthData().getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("std_items:longsword_melee");
        knight3.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, new ArrayList<>(), context);
        assertEquals(157, youngRedDragon.getHealthData().getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("std_items:longsword_melee");
        knight4.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, new ArrayList<>(), context);
        assertEquals(150, youngRedDragon.getHealthData().getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("std_items:longsword_melee");
        knight5.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, new ArrayList<>(), context);
        assertEquals(143, youngRedDragon.getHealthData().getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("std_items:longsword_melee");
        knight6.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, new ArrayList<>(), context);
        assertEquals(136, youngRedDragon.getHealthData().getInteger("current"));
    }

    @Test
    @DisplayName("wrathful smite test")
    void wrathfulSmiteTest() throws Exception {
        RPGLObject knight1 = RPGLFactory.newObject("demo:knight");
        RPGLObject knight2 = RPGLFactory.newObject("demo:knight");

        DummyContext context = new DummyContext();
        context.add(knight1);
        context.add(knight2);

        // knight 1 uses wrathful smite

        knight1.invokeEvent(
                new RPGLObject[] { knight1 },
                RPGLFactory.newEvent("demo:wrathful_smite"),
                new ArrayList<>(),
                context
        );

        assertEquals("Wrathful Smite 1", knight1.getEffectObjects().get(0).getName(),
                "knight 1` should have 1 effect (wrathful_smite_1)"
        );

        // cool! now he attacks

        knight1.invokeEvent(
                new RPGLObject[] { knight2 },
                RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee"),
                new ArrayList<>(),
                context
        );

        assertEquals(52-4-3-3, knight2.getHealthData().getInteger("current"),
                "knight should get hit and take some damage"
        );

        // check effects

        assertEquals(0, knight1.getEffectObjects().size(),
            "knight 1 should no longer be affected by wrathful_smite_1"
        );

        assertEquals("Frightened (Wrathful Smite)", knight2.getEffectObjects().get(0).getName(),
                "knight 2 should be affected by wrathful_smite 2"
        );

        // knight 2 tries to counter-attack

        knight2.invokeEvent(
                new RPGLObject[] { knight1 },
                RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee"),
                new ArrayList<>(),
                context
        );

        assertEquals(52, knight1.getHealthData().getInteger("current"),
                "knight 2 should have missed from disadvantage and dealt no damage to knight 1"
        );
    }

    @Test
    @DisplayName("dragons wrestling and biting")
    void dragonsWrestlingAndBiting() throws Exception {
        RPGLObject youngRedDragon1 = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject youngRedDragon2 = RPGLFactory.newObject("demo:young_red_dragon");

        DummyContext context = new DummyContext();
        context.add(youngRedDragon1);
        context.add(youngRedDragon2);

        RPGLEvent youngRedDragonBiteAttack;

        youngRedDragonBiteAttack = RPGLFactory.newEvent("demo:young_red_dragon_bite_attack");
        youngRedDragon1.invokeEvent(
                new RPGLObject[] {youngRedDragon2},
                youngRedDragonBiteAttack,
                new ArrayList<>(),
                context
        );

        // dragon takes 16 damage
        assertEquals(178-16, youngRedDragon2.getHealthData().getInteger("current"),
                "dragon should take 16 piercing damage (5+5+6) and be immune to fire damage"
        );
    }

}
