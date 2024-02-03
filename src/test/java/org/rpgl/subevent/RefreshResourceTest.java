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
 * Testing class for the org.rpgl.subevent.RefreshResource class.
 *
 * @author Calvin Withun
 */
public class RefreshResourceTest {

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
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("refreshes resources (prioritizes low potency)")
    void refreshesResources_prioritizesLowPotency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            resource.exhaust();
            object.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag":"spell_slot",
                "count": 5,
                "minimum_potency": 3,
                "selection_mode", "low_first"
            }*/
            this.putString("resource_tag", "spell_slot");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 3);
            this.putString("selection_mode", "low_first");
        }});
        refreshResource.setSource(object);
        refreshResource.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        refreshResource.setTarget(object);
        refreshResource.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        boolean[] expectedExhaustValues = {
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
        };

        for (RPGLResource resource : object.getResourceObjects()) {
            int potency = resource.getPotency();
            boolean expected = expectedExhaustValues[potency - 1];
            assertEquals(expected, resource.getExhausted(),
                    "resource with potency " + potency + " should " + (expected ? "" : "not ") + "be exhausted"
            );
        }
    }

    @Test
    @DisplayName("refreshes resources (prioritizes high potency)")
    void refreshesResources_prioritizesHighPotency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            resource.exhaust();
            object.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag":"spell_slot",
                "count": 5,
                "maximum_potency": 7
            }*/
            this.putString("resource_tag", "spell_slot");
            this.putInteger("count", 5);
            this.putInteger("maximum_potency", 7);
            this.putString("selection_mode", "high_first");
        }});
        refreshResource.setSource(object);
        refreshResource.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        refreshResource.setTarget(object);
        refreshResource.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        boolean[] expectedExhaustValues = {
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
        };

        for (RPGLResource resource : object.getResourceObjects()) {
            int potency = resource.getPotency();
            boolean expected = expectedExhaustValues[potency - 1];
            assertEquals(expected, resource.getExhausted(),
                    "resource with potency " + potency + " should " + (expected ? "" : "not ") + "be exhausted"
            );
        }
    }

    @Test
    @DisplayName("refreshes resources (default priority)")
    void refreshesResources_defaultPriority() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLResource spellSlot = RPGLFactory.newResource("std:common/spell_slot/01");
        spellSlot.exhaust();
        spellSlot.setPotency(1);
        object.addResource(spellSlot);

        RPGLResource pactSpellSlot = RPGLFactory.newResource("std:common/spell_slot/pact_magic/01");
        pactSpellSlot.exhaust();
        pactSpellSlot.setPotency(1);
        object.addResource(pactSpellSlot);

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag":"pact_spell_slot",
                "count": 2
            }*/
            this.putString("resource_tag", "pact_spell_slot");
            this.putInteger("count", 2);
        }});
        refreshResource.setSource(object);
        refreshResource.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        refreshResource.setTarget(object);
        refreshResource.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertTrue(spellSlot.getExhausted(),
                "non-matching resource should be exhausted"
        );
        assertFalse(pactSpellSlot.getExhausted(),
                "matching resource should not be exhausted"
        );
    }

}
