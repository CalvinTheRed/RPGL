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
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for miscellaneous scenarios. Tests here are designed to stress test RPGL at a high level.
 *
 * @author Calvin Withun
 */
public class ScenariosTest {

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
    @DisplayName("flametongue test")
    void flametongueTest() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        // configure source dummy
        source.addResource(RPGLFactory.newResource("std:common/action/01"));
        source.addResource(RPGLFactory.newResource("std:common/bonus_action/01"));
        source.getAbilityScores().putInteger("str", 16);
        RPGLItem flametongue = RPGLFactory.newItem("std:weapon/melee/martial/scimitar/flametongue");
        source.giveItem(flametongue.getUuid());
        source.equipItem(flametongue.getUuid(), "mainhand");
        assertEquals(1, source.getEffectObjects().size(),
                "equipping the flametongue should provide 1 effect to the wielder"
        );

        List<RPGLEvent> events = source.getEventObjects(context);
        RPGLEvent flametongueAttack = TestUtils.getEventById(events, "std:item/weapon/melee/martial/scimitar/melee");
        assertNotNull(TestUtils.getEventById(events, "std:item/magic/flametongue/activate"));
        assertNull(TestUtils.getEventById(events, "std:item/magic/flametongue/deactivate"));
        assertNotNull(flametongueAttack);
        assertEquals(flametongue.getUuid(), flametongueAttack.getOriginItem());

        // make an attack, not activated
        source.invokeEvent(
                target.getPosition(),
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context),
                        "std:item/weapon/melee/martial/scimitar/melee"
                ),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("action").get(0));
                }},
                context
        );
        assertEquals(1000-3-3, target.getHealthData().getInteger("current"),
                "Dummy should take 6 damage from being hit (6 slashing)"
        );

        // RESET DUMMY HEALTH TO 1000
        TestUtils.resetObjectHealth(target, context);

        // activate
        source.invokeEvent(
                source.getPosition(),
                new RPGLObject[] {
                        source
                },
                TestUtils.getEventById(source.getEventObjects(context),
                        "std:item/magic/flametongue/activate"
                ),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("bonus_action").get(0));
                }},
                context
        );
        assertTrue(flametongue.hasTag("flametongue"),
                "flametongue tag should be added from the activation command word event"
        );

        // make an attack, activated
        source.invokeEvent(
                target.getPosition(),
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context),
                        "std:item/weapon/melee/martial/scimitar/melee"
                ),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("action").get(0));
                }},
                context
        );
        assertEquals(1000-3-3-3-3, target.getHealthData().getInteger("current"),
                "Dummy should take 12 damage from being hit (6 slashing, 6 fire)"
        );

        // RESET DUMMY HEALTH TO 1000
        TestUtils.resetObjectHealth(target, context);

        // deactivate
        source.invokeEvent(
                source.getPosition(),
                new RPGLObject[] {
                        source
                },
                TestUtils.getEventById(source.getEventObjects(context),
                        "std:item/magic/flametongue/deactivate"
                ),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("bonus_action").get(0));
                }},
                context
        );
        assertFalse(flametongue.hasTag("flametongue"),
                "flametongue tag should be removed from the deactivation command word event"
        );

        // make an attack, activated
        source.invokeEvent(
                target.getPosition(),
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context),
                        "std:item/weapon/melee/martial/scimitar/melee"
                ),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("action").get(0));
                }},
                context
        );
        assertEquals(1000-3-3, target.getHealthData().getInteger("current"),
                "Dummy should take 6 damage from being hit (6 slashing)"
        );
    }

    @Test
    @DisplayName("wrathful smite test")
    void wrathfulSmiteTest() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        // give resources

        source.giveEvent("std:spell/wrathful_smite");
        source.addResource(RPGLFactory.newResource("std:common/action/01"));
        source.addResource(RPGLFactory.newResource("std:common/bonus_action/01"));
        source.addResource(RPGLFactory.newResource("std:common/spell_slot/02"));

        target.addResource(RPGLFactory.newResource("std:common/action/01"));

        // give equipment

        RPGLItem dagger = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        source.giveItem(dagger.getUuid());
        source.equipItem(dagger.getUuid(), "right_hand");

        // assign ability scores

        source.getAbilityScores().putInteger("str", 20);
        source.getAbilityScores().putInteger("cha", 20); // save DC 15

        target.getAbilityScores().putInteger("wis", 12); // save bonus +1, save DC 10

        // start doing stuff

        source.invokeEvent(
                source.getPosition(),
                new RPGLObject[] {
                        source
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:spell/wrathful_smite"),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("bonus_action").get(0));
                    this.add(source.getResourcesWithTag("spell_slot").get(0));
                }},
                context
        );

        assertEquals(2, Objects.requireNonNull(TestUtils.getEffectById(source.getEffectObjects(), "std:spell/wrathful_smite/passive"))
                        .seek("subevent_filters.damage_collection[0].functions[1].damage[0].dice[0].count"),
                "wrathful smite passive effect should exist and have 2 extra damage dice, rather than 1, due to upcasting"
        );

        source.invokeEvent(
                target.getPosition(),
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:item/weapon/melee/simple/dagger/melee"),
                new ArrayList<>() {{
                    this.add(source.getResourcesWithTag("action").get(0));
                }},
                context
        );

        assertEquals(1000-2-5-3-3, target.getHealthData().getInteger("current"),
                "target should no longer have all of its hit points"
        );
        assertNull(TestUtils.getEffectById(source.getEffectObjects(), "std:spell/wrathful_smite/passive"),
                "source should not have the passive wrathful smite applied any longer"
        );
        assertNotNull(TestUtils.getEffectById(target.getEffectObjects(), "std:spell/wrathful_smite/fear"),
                "target should have the wrathful smite fear applied"
        );

        source.invokeInfoSubevent(context, "end_turn");
        target.invokeInfoSubevent(context, "start_turn");

        target.invokeEvent(
                target.getPosition(),
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(target.getEventObjects(context), "std:special/spell/wrathful_smite/repeat_save"),
                new ArrayList<>() {{
                    this.add(target.getResourcesWithTag("action").get(0));
                }},
                context
        );

        // With the current check bonus, target can pass if target generates the DC, but not if source generates the DC.
        // This assertion is to show that source is the one generating the DC.
        assertNotNull(TestUtils.getEffectById(target.getEffectObjects(), "std:spell/wrathful_smite/fear"),
                "target should still have the wrathful smite fear applied after a failed save"
        );

        // try again with a better save bonus

        target.invokeInfoSubevent(context, "end_turn");
        target.invokeInfoSubevent(context, "start_turn");

        target.getAbilityScores().putInteger("wis", 20); // save bonus +5

        target.invokeEvent(
                target.getPosition(),
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(target.getEventObjects(context), "std:special/spell/wrathful_smite/repeat_save"),
                new ArrayList<>() {{
                    this.add(target.getResourcesWithTag("action").get(0));
                }},
                context
        );

        assertNull(TestUtils.getEffectById(target.getEffectObjects(), "std:spell/wrathful_smite/fear"),
                "target should not still have the wrathful smite fear applied after a successful save"
        );
    }

    @Test
    @DisplayName("summon undead test")
    void summonUndeadTest() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(summoner);

        summoner.getEvents().addString("std:spell/summon_undead/skeletal");
        summoner.addResource(RPGLFactory.newResource("std:common/action/01"));
        summoner.addResource(RPGLFactory.newResource("std:common/spell_slot/04"));
        summoner.getAbilityScores().putInteger("int", 20);
        summoner.setProficiencyBonus(3);

        summoner.invokeEvent(
                TestUtils.TEST_ARRAY_10_10_10,
                new RPGLObject[] {
                        summoner
                },
                TestUtils.getEventById(summoner.getEventObjects(context), "std:spell/summon_undead/skeletal"),
                new ArrayList<>() {{
                    this.add(summoner.getResourcesWithTag("action").get(0));
                    this.add(summoner.getResourcesWithTag("spell_slot").get(0));
                }},
                context
        );

        context.remove(summoner);
        RPGLObject summonedUndead = context.getContextObjects().get(0);
        context.add(summoner);

        assertEquals(4, summonedUndead.getLevel("std:summon/summon_undead"),
                "level 4 with level 4 spell slot"
        );
        assertEquals(30, summonedUndead.getMaximumHitPoints(context),
                "30 hit points with level 4 spell slot"
        );
        assertEquals(15, summonedUndead.getBaseArmorClass(context),
                "AC 15 with level 4 spell slot"
        );

        summoner.getAbilityScores().putInteger("dex", 28); // set AC to 19 to avoid hit
        summonedUndead.invokeEvent(
                summoner.getPosition(),
                new RPGLObject[] { summoner },
                TestUtils.getEventById(summonedUndead.getEventObjects(context), "std:spell/summon_undead/grave_bolt"),
                List.of(summonedUndead.getResourcesWithTag("action").get(0)),
                context
        );
        assertEquals(1000, summoner.getHealthData().getInteger("current"),
                "dummy should have been missed and taken no damage"
        );

        summonedUndead.invokeInfoSubevent(context, "end_turn");
        summonedUndead.invokeInfoSubevent(context, "start_turn");

        summoner.getAbilityScores().putInteger("dex", 26); // set AC to 18 to suffer hit
        summonedUndead.invokeEvent(
                summoner.getPosition(),
                new RPGLObject[] { summoner },
                TestUtils.getEventById(summonedUndead.getEventObjects(context), "std:spell/summon_undead/grave_bolt"),
                List.of(summonedUndead.getResourcesWithTag("action").get(0)),
                context
        );
        assertEquals(1000-2-2-3-4, summoner.getHealthData().getInteger("current"),
                "dummy should have been hit and damaged"
        );
    }

}
