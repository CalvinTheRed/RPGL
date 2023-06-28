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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
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
    @DisplayName("runLowFirst refreshes resources correctly (full count can be met)")
    void runLowFirst_refreshesResourcesCorrectly_fullCountCanBeMet() {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            resource.exhaust();
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 5,
                "minimum_potency": 3
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 3);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.runLowFirst();

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

        for (RPGLResource resource : target.getResourceObjects()) {
            int potency = resource.getPotency();
            boolean expected = expectedExhaustValues[potency - 1];
            assertEquals(expected, resource.getExhausted(),
                    "resource with potency " + potency + " should " + (expected ? "" : "not ") + "be exhausted"
            );
        }
    }

    @Test
    @DisplayName("runLowFirst refreshes resources correctly (full count can not be met)")
    void runLowFirst_refreshesResourcesCorrectly_fullCountCanNotBeMet() {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            resource.exhaust();
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 5,
                "minimum_potency": 3,
                "maximum_potency": 5
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 3);
            this.putInteger("maximum_potency", 5);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.runLowFirst();

        boolean[] expectedExhaustValues = {
                true,
                true,
                false,
                false,
                false,
                true,
                true,
                true,
                true,
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
    @DisplayName("runLowFirst refreshes resources correctly skipping refreshed resources")
    void runLowFirst_refreshesResourcesCorrectlySkippingRefreshedResources() {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            if (i > 4) {
                resource.exhaust();
            }
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 5,
                "minimum_potency": 3
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 3);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.runLowFirst();

        for (RPGLResource resource : target.getResourceObjects()) {
            assertFalse(resource.getExhausted(),
                    "resource with potency " + resource.getPotency() + " should not be exhausted"
            );
        }
    }

    @Test
    @DisplayName("runHighFirst refreshes resources correctly (full count can be met)")
    void runHighFirst_refreshesResourcesCorrectly_fullCountCanBeMet() {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            resource.exhaust();
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 5,
                "maximum_potency": 7
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 5);
            this.putInteger("maximum_potency", 7);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.runHighFirst();

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

        for (RPGLResource resource : target.getResourceObjects()) {
            int potency = resource.getPotency();
            boolean expected = expectedExhaustValues[potency - 1];
            assertEquals(expected, resource.getExhausted(),
                    "resource with potency " + potency + " should " + (expected ? "" : "not ") + "be exhausted"
            );
        }
    }

    @Test
    @DisplayName("runHighFirst refreshes resources correctly (full count can not be met)")
    void runHighFirst_refreshesResourcesCorrectly_fullCountCanNotBeMet() {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            resource.exhaust();
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 5,
                "minimum_potency": 5,
                "maximum_potency": 7
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 5);
            this.putInteger("maximum_potency", 7);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.runHighFirst();

        boolean[] expectedExhaustValues = {
                true,
                true,
                true,
                true,
                false,
                false,
                false,
                true,
                true,
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
    @DisplayName("runHighFirst refreshes resources correctly skipping refreshed resources")
    void runHighFirst_refreshesResourcesCorrectlySkippingRefreshedResources() {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            if (i < 6) {
                resource.exhaust();
            }
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 5,
                "maximum_potency": 5
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 5);
            this.putInteger("maximum_potency", 5);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.runHighFirst();

        for (RPGLResource resource : target.getResourceObjects()) {
            assertFalse(resource.getExhausted(),
                    "resource with potency " + resource.getPotency() + " should not be exhausted"
            );
        }
    }

    @Test
    @DisplayName("invoke refreshes resources correctly (low first)")
    void invoke_refreshesResourcesCorrectly_lowFirst() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            resource.exhaust();
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 2,
                "selection_mode": "low_first"
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 2);
            this.putString("selection_mode", "low_first");
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.invoke(context);

        boolean[] expectedExhaustValues = {
                false,
                false,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
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
    @DisplayName("invoke refreshes resources correctly (high first)")
    void invoke_refreshesResourcesCorrectly_highFirst() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("demo:spell_slot");
            resource.setPotency(i);
            resource.exhaust();
            target.addResource(resource);
        }

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 2,
                "selection_mode": "high_first"
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 2);
            this.putString("selection_mode", "high_first");
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.invoke(context);

        boolean[] expectedExhaustValues = {
                true,
                true,
                true,
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
    @DisplayName("invoke refreshes only matching resources")
    void invoke_refreshesOnlyMatchingResources() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLResource spellSlot = RPGLFactory.newResource("demo:spell_slot");
        spellSlot.exhaust();
        spellSlot.setPotency(1);
        target.addResource(spellSlot);

        RPGLResource pactSpellSlot = RPGLFactory.newResource("demo:pact_spell_slot");
        pactSpellSlot.exhaust();
        pactSpellSlot.setPotency(1);
        target.addResource(pactSpellSlot);

        RefreshResource refreshResource = new RefreshResource();
        refreshResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:spell_slot",
                "count": 2
            }*/
            this.putString("resource", "demo:spell_slot");
            this.putInteger("count", 2);
        }});
        refreshResource.setSource(source);
        refreshResource.setTarget(target);

        refreshResource.invoke(context);

        assertFalse(spellSlot.getExhausted(),
                "matching resource should not be exhausted"
        );
        assertTrue(pactSpellSlot.getExhausted(),
                "non-matching resource should be exhausted"
        );
    }

}
