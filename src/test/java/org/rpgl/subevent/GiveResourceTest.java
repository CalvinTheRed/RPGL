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
 * Testing class for the org.rpgl.subevent.GiveResource class.
 *
 * @author Calvin Withun
 */
public class GiveResourceTest {

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
        Subevent subevent = new GiveResource();
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
    @DisplayName("invoke gives one resource of potency one when no count specified")
    void invoke_givesOneResourceOfPotencyOneWhenNoCountSpecified() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        GiveResource giveResource = new GiveResource();
        giveResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:class/warlock/the_undead_patron/necrotic_husk"
            }*/
            this.putString("resource", "std:class/warlock/the_undead_patron/necrotic_husk");
        }});
        giveResource.setSource(source);
        giveResource.setTarget(target);

        giveResource.invoke(context, List.of());

        assertEquals(1, target.getResourceObjects().size(),
                "target should be given one resource"
        );
        assertEquals("std:class/warlock/the_undead_patron/necrotic_husk", target.getResourceObjects().get(0).getId(),
                "resource should be the correct type"
        );
        assertEquals(1, target.getResourceObjects().get(0).getPotency(),
                "resource should default to potency of 1"
        );
        assertTrue(target.getResourceObjects().get(0).hasTag("temporary"),
                "resource should be given the temporary tag"
        );
        assertFalse(target.getResourceObjects().get(0).getExhausted(),
                "resource should not be exhausted when first provided"
        );
    }

    @Test
    @DisplayName("invoke gives correct number of resources when count specified")
    void invoke_givesCorrectNumberOfResourcesWhenCountSpecified() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        GiveResource giveResource = new GiveResource();
        giveResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:class/warlock/the_undead_patron/necrotic_husk",
                "count": 2
            }*/
            this.putString("resource", "std:class/warlock/the_undead_patron/necrotic_husk");
            this.putInteger("count", 2);
        }});
        giveResource.setSource(source);
        giveResource.setTarget(target);

        giveResource.invoke(context, List.of());

        assertEquals(2, target.getResourceObjects().size(),
                "target should be given two resources"
        );
        for (RPGLResource resource : target.getResourceObjects()) {
            assertEquals("std:class/warlock/the_undead_patron/necrotic_husk", resource.getId(),
                    "resource should be the correct type"
            );
            assertTrue(resource.hasTag("temporary"),
                    "resource should be given the temporary tag"
            );
            assertFalse(resource.getExhausted(),
                    "resource should not be exhausted when first provided"
            );
        }
    }

}
