package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.InsufficientResourcePotencyException;
import org.rpgl.exception.ResourceCountException;
import org.rpgl.exception.ResourceMismatchException;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.core.RPGLEvent class.
 *
 * @author Calvin Withun
 */
public class RPGLEventTest {

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
    @DisplayName("doResourcesSatisfyCost returns true (resources satisfy cost)")
    void doResourcesSatisfyCost_returnsTrue_resourcesSatisfyCost() {
        RPGLEvent event = RPGLFactory.newEvent("std:cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:action");
        RPGLResource spellSlot = RPGLFactory.newResource("std:spell_slot");

        assertTrue(event.doResourcesSatisfyCost(new ArrayList<>() {{
            this.add(action);
            this.add(spellSlot);
        }}),
                "resources should satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("doResourcesSatisfyCost returns false (resource count mismatch)")
    void doResourcesSatisfyCost_returnsFalse_resourceCountMismatch() {
        RPGLEvent event = RPGLFactory.newEvent("std:cure_wounds");
        assertThrows(ResourceCountException.class,
                () -> event.doResourcesSatisfyCost(new ArrayList<>()),
                "resources should not satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("doResourcesSatisfyCost throws exception (resource potency too low)")
    void doResourcesSatisfyCost_throwsException_resourcePotencyTooLow() {
        RPGLEvent event = RPGLFactory.newEvent("std:cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:action");
        RPGLResource spellSlot = RPGLFactory.newResource("std:spell_slot");
        spellSlot.setPotency(0);

        assertThrows(InsufficientResourcePotencyException.class,
                () -> event.doResourcesSatisfyCost(new ArrayList<>() {{
                    this.add(action);
                    this.add(spellSlot);
                }}),
                "resources should not satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("doResourcesSatisfyCost throws exception (resources don't match cost)")
    void doResourcesSatisfyCost_throwsException_resourcesDontMatchCost() {
        RPGLEvent event = RPGLFactory.newEvent("std:cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:action");
        RPGLResource spellSlot = RPGLFactory.newResource("std:spell_slot");
        spellSlot.setPotency(0);

        assertThrows(ResourceMismatchException.class,
                () -> event.doResourcesSatisfyCost(new ArrayList<>() {{
                    this.add(spellSlot);
                    this.add(action);
                }}),
                "resources should not satisfy resource requirement"
        );
    }

    @Test
    @DisplayName("scale scales target field correctly for resources with extra potency")
    void scale_scalesTargetFieldCorrectlyForResourcesWithExtraPotency() {
        RPGLEvent event = RPGLFactory.newEvent("std:cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:action");
        RPGLResource spellSlot = RPGLFactory.newResource("std:spell_slot");
        spellSlot.setPotency(9);

        event.scale(new ArrayList<>() {{
            this.add(action);
            this.add(spellSlot);
        }});

        assertEquals(9, event.seekInteger("subevents[0].healing[0].dice[0].count"),
                "dice count should be increased by 1 for each potency beyond 1"
        );
    }

    @Test
    @DisplayName("scale does not scale target field when resources have minimum required potency")
    void scale_doesNotScaleTargetFieldWhenResourcesHaveMinimumRequiredPotency() {
        RPGLEvent event = RPGLFactory.newEvent("std:cure_wounds");
        RPGLResource action = RPGLFactory.newResource("std:action");
        RPGLResource spellSlot = RPGLFactory.newResource("std:spell_slot");
        spellSlot.setPotency(1);

        event.scale(new ArrayList<>() {{
            this.add(action);
            this.add(spellSlot);
        }});

        assertEquals(1, event.seekInteger("subevents[0].healing[0].dice[0].count"),
                "dice count should stay at 1 when resource has minimum required potency"
        );
    }

}
