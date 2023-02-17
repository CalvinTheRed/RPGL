package org.rpgl.scenarios;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        RPGLContext context = new RPGLContext();
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
                context
        );

        // each target takes 48 damage
        assertEquals(4, knight1.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));
        assertEquals(4, knight2.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));
        assertEquals(4, knight3.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));
        assertEquals(4, knight4.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));
        assertEquals(4, knight5.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));
        assertEquals(4, knight6.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee");
        knight1.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, context);
        assertEquals(171, youngRedDragon.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee");
        knight2.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, context);
        assertEquals(164, youngRedDragon.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee");
        knight3.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, context);
        assertEquals(157, youngRedDragon.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee");
        knight4.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, context);
        assertEquals(150, youngRedDragon.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee");
        knight5.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, context);
        assertEquals(143, youngRedDragon.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));

        // dragon takes 7 damage
        mainhandAttack = RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee");
        knight6.invokeEvent(new RPGLObject[] {youngRedDragon}, mainhandAttack, context);
        assertEquals(136, youngRedDragon.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"));
    }

}
