package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.ExhaustResource class.
 *
 * @author Calvin Withun
 */
public class ExhaustResourceTest {

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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new TakeResource();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("exhausts resources (prioritize low potency)")
    void exhaustsResources_prioritizeLowPotency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 4,
                "minimum_potency": 3,
                "selection_mode": "low_first"
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 4);
            this.putInteger("minimum_potency", 3);
            this.putString("selection_mode", "low_first");
        }});
        exhaustResource.setSource(source);
        exhaustResource.prepare(new DummyContext());
        exhaustResource.setTarget(target);
        exhaustResource.invoke(new DummyContext());

        boolean[] expectedExhaustValues = {
                false,
                false,
                true,
                true,
                true,
                true,
                false,
                false,
                false,
        };

        for (RPGLResource resource : target.getResourceObjects()) {
            int potency = resource.getPotency();
            boolean expected = expectedExhaustValues[potency - 1];
            assertEquals(expected, resource.getExhausted(),
                    "resource with potency " + potency + " should " + (expected ? "" : "not ") + "be exhausted"
            );
        }
    }

    @Test
    @DisplayName("exhausts resources (prioritizes high potency")
    void runHighFirst_exhaustsResourcesCorrectly_fullCountCanBeMet() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 4,
                "maximum_potency": 7,
                "selection_mode": "high_first"
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 4);
            this.putInteger("maximum_potency", 7);
            this.putString("selection_mode", "high_first");
        }});
        exhaustResource.setSource(source);
        exhaustResource.prepare(new DummyContext());
        exhaustResource.setTarget(target);
        exhaustResource.invoke(new DummyContext());

        boolean[] expectedExhaustValues = {
                false,
                false,
                false,
                true,
                true,
                true,
                true,
                false,
                false,
        };

        for (RPGLResource resource : target.getResourceObjects()) {
            int potency = resource.getPotency();
            boolean expected = expectedExhaustValues[potency - 1];
            assertEquals(expected, resource.getExhausted(),
                    "resource with potency " + potency + " should " + (expected ? "" : "not ") + "be exhausted"
            );
        }
    }

    @Test
    @DisplayName("exhausts resources (default priority)")
    void exhaustsResources_defaultPriority() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLResource spellSlot = RPGLFactory.newResource("std:common/spell_slot/01");
        spellSlot.setPotency(1);
        target.addResource(spellSlot);

        RPGLResource pactSpellSlot = RPGLFactory.newResource("std:common/spell_slot/pact_magic/01");
        pactSpellSlot.setPotency(1);
        target.addResource(pactSpellSlot);

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 2
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 2);
        }});
        exhaustResource.setSource(source);
        exhaustResource.prepare(new DummyContext());
        exhaustResource.setTarget(target);
        exhaustResource.invoke(new DummyContext());

        assertTrue(spellSlot.getExhausted(),
                "matching resource should be exhausted"
        );
        assertFalse(pactSpellSlot.getExhausted(),
                "non-matching resource should not be exhausted"
        );
    }

}
