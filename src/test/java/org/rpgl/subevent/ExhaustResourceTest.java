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
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

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
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("runLowFirst exhausts resources correctly (full count can be met)")
    void runLowFirst_exhaustsResourcesCorrectly_fullCountCanBeMet() {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 5,
                "minimum_potency": 3
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 3);
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.runLowFirst();

        boolean[] expectedExhaustValues = {
                false,
                false,
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
    @DisplayName("runLowFirst exhausts resources correctly (full count can not be met)")
    void runLowFirst_exhaustsResourcesCorrectly_fullCountCanNotBeMet() {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 5,
                "minimum_potency": 3,
                "maximum_potency": 5
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 3);
            this.putInteger("maximum_potency", 5);
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.runLowFirst();

        boolean[] expectedExhaustValues = {
                false,
                false,
                true,
                true,
                true,
                false,
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
    @DisplayName("runLowFirst exhausts resources correctly skipping exhausted resources")
    void runLowFirst_exhaustsResourcesCorrectlySkippingExhaustedResources() {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            if (i < 5) {
                resource.exhaust();
            }
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 5
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 5);
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.runLowFirst();

        for (RPGLResource resource : target.getResourceObjects()) {
            assertTrue(resource.getExhausted(),
                    "resource with potency " + resource.getPotency() + " should be exhausted"
            );
        }
    }

    @Test
    @DisplayName("runHighFirst exhausts resources correctly (full count can be met)")
    void runHighFirst_exhaustsResourcesCorrectly_fullCountCanBeMet() {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 5,
                "maximum_potency": 7
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 5);
            this.putInteger("maximum_potency", 7);
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.runHighFirst();

        boolean[] expectedExhaustValues = {
                false,
                false,
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
    @DisplayName("runHighFirst exhausts resources correctly (full count can not be met)")
    void runHighFirst_exhaustsResourcesCorrectly_fullCountCanNotBeMet() {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 5,
                "minimum_potency": 5,
                "maximum_potency": 7
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 5);
            this.putInteger("minimum_potency", 5);
            this.putInteger("maximum_potency", 7);
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.runHighFirst();

        boolean[] expectedExhaustValues = {
                false,
                false,
                false,
                false,
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
    @DisplayName("runHighFirst exhausts resources correctly skipping exhausted resources")
    void runHighFirst_exhaustsResourcesCorrectlySkippingExhaustedResources() {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            if (i > 5) {
                resource.exhaust();
            }
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 5,
                "maximum_potency": 5
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 5);
            this.putInteger("maximum_potency", 5);
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.runHighFirst();

        for (RPGLResource resource : target.getResourceObjects()) {
            assertTrue(resource.getExhausted(),
                    "resource with potency " + resource.getPotency() + " should be exhausted"
            );
        }
    }

    @Test
    @DisplayName("invoke exhausts resources correctly (low first)")
    void invoke_exhaustsResourcesCorrectly_lowFirst() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 2,
                "selection_mode": "low_first"
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 2);
            this.putString("selection_mode", "low_first");
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.invoke(context, List.of());

        boolean[] expectedExhaustValues = {
                true,
                true,
                false,
                false,
                false,
                false,
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
    @DisplayName("invoke exhausts resources correctly (high first)")
    void invoke_exhaustsResourcesCorrectly_highFirst() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 1; i < 10; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/01");
            resource.setPotency(i);
            target.addResource(resource);
        }

        ExhaustResource exhaustResource = new ExhaustResource();
        exhaustResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:common/spell_slot/01",
                "count": 2,
                "selection_mode": "high_first"
            }*/
            this.putString("resource", "std:common/spell_slot/01");
            this.putInteger("count", 2);
            this.putString("selection_mode", "high_first");
        }});
        exhaustResource.setSource(source);
        exhaustResource.setTarget(target);

        exhaustResource.invoke(context, List.of());

        boolean[] expectedExhaustValues = {
                false,
                false,
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
    @DisplayName("invoke exhausts only matching resources")
    void invoke_exhaustsOnlyMatchingResources() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

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
        exhaustResource.setTarget(target);

        exhaustResource.invoke(context, List.of());

        assertTrue(spellSlot.getExhausted(),
                "matching resource should be exhausted"
        );
        assertFalse(pactSpellSlot.getExhausted(),
                "non-matching resource should not be exhausted"
        );
    }

}
