package org.rpgl.scenarios;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
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
    @DisplayName("flametongue test")
    void flametongueTest() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("bugtest:dummy");
        RPGLItem flametongue = RPGLFactory.newItem("std:flametongue_scimitar");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.giveItem(flametongue.getUuid());
        source.equipItem(flametongue.getUuid(), "mainhand");
        assertEquals(1, source.getEffectObjects().size(),
                "equipping the flametongue should provide 1 effect to the wielder"
        );

        List<RPGLEvent> events = source.getEventObjects(context);
        RPGLEvent flametongueAttack = TestUtils.getEventById(events, "std:scimitar_melee");
        assertNotNull(TestUtils.getEventById(events, "std:flametongue_command_word_activate"));
        assertNull(TestUtils.getEventById(events, "std:flametongue_command_word_deactivate"));
        assertNotNull(flametongueAttack);
        assertEquals(flametongue.getUuid(), flametongueAttack.getOriginItem());

        // make an attack, not activated
        source.invokeEvent(
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:scimitar_melee"),
                new ArrayList<>() {{
                    this.add(TestUtils.getResourceById(source.getResourceObjects(), "std:action"));
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
                new RPGLObject[] {
                        source
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:flametongue_command_word_activate"),
                new ArrayList<>() {{
                    this.add(TestUtils.getResourceById(source.getResourceObjects(), "std:bonus_action"));
                }},
                context
        );
        assertTrue(flametongue.hasTag("flametongue"),
                "flametongue tag should be added from the activation command word event"
        );

        // make an attack, activated
        source.invokeEvent(
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:scimitar_melee"),
                new ArrayList<>() {{
                    this.add(TestUtils.getResourceById(source.getResourceObjects(), "std:action"));
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
                new RPGLObject[] {
                        source
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:flametongue_command_word_deactivate"),
                new ArrayList<>() {{
                    this.add(TestUtils.getResourceById(source.getResourceObjects(), "std:bonus_action"));
                }},
                context
        );
        assertFalse(flametongue.hasTag("flametongue"),
                "flametongue tag should be removed from the deactivation command word event"
        );

        // make an attack, activated
        source.invokeEvent(
                new RPGLObject[] {
                        target
                },
                TestUtils.getEventById(source.getEventObjects(context), "std:scimitar_melee"),
                new ArrayList<>() {{
                    this.add(TestUtils.getResourceById(source.getResourceObjects(), "std:action"));
                }},
                context
        );
        assertEquals(1000-3-3, target.getHealthData().getInteger("current"),
                "Dummy should take 6 damage from being hit (6 slashing)"
        );
    }

}
