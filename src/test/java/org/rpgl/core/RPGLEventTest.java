package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.InsufficientResourcePotencyException;
import org.rpgl.exception.ResourceCountException;
import org.rpgl.exception.ResourceMismatchException;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.core.RPGLEvent class.
 *
 * @author Calvin Withun
 */
public class RPGLEventTest {

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
    @DisplayName("verifies resources are sufficient")
    void verifiesResourcesAreSufficient() {
        RPGLEvent event = RPGLFactory.newEvent("std:spell/cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:common/action/01");
        RPGLResource spellSlot = RPGLFactory.newResource("std:common/spell_slot/01");

        event.verifyResourcesSatisfyCost(List.of(action, spellSlot));
    }

    @Test
    @DisplayName("verifies number of resources")
    void verifiesNumberOfResources() {
        RPGLEvent event = RPGLFactory.newEvent("std:spell/cure_wounds");
        assertThrows(ResourceCountException.class,
                () -> event.verifyResourcesSatisfyCost(List.of()),
                "resources should not satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("verifies resource potencies")
    void verifiesResourcePotencies() {
        RPGLEvent event = RPGLFactory.newEvent("std:spell/cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:common/action/01");
        RPGLResource spellSlot = RPGLFactory.newResource("std:common/spell_slot/01");
        spellSlot.setPotency(0);

        assertThrows(InsufficientResourcePotencyException.class,
                () -> event.verifyResourcesSatisfyCost(List.of(action, spellSlot)),
                "resources should not satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("verifies resource tags")
    void verifiesResourceTags() {
        RPGLEvent event = RPGLFactory.newEvent("std:spell/cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:common/action/01");
        RPGLResource bonusAction = RPGLFactory.newResource("std:common/bonus_action/01");

        assertThrows(ResourceMismatchException.class,
                () -> event.verifyResourcesSatisfyCost(List.of(action, bonusAction)),
                "resources should not satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("scales fields")
    void scalesFields() {
        RPGLEvent event = RPGLFactory.newEvent("std:spell/cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:common/action/01");
        RPGLResource spellSlot = RPGLFactory.newResource("std:common/spell_slot/01");
        spellSlot.setPotency(9);

        event.scale(List.of(action, spellSlot));

        assertEquals(9, event.seekInteger("subevents[0].healing[0].dice[0].count"),
                "dice count should be increased by 1 for each potency beyond 1"
        );
    }

}
