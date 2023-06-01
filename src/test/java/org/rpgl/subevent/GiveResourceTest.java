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
 * Testing class for the org.rpgl.subevent.GiveResource class.
 *
 * @author Calvin Withun
 */
public class GiveResourceTest {

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
        Subevent subevent = new GiveResource();
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
    @DisplayName("invoke gives one resource when no count specified")
    void invoke_givesOneResourceWhenNoCountSpecified() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        GiveResource giveResource = new GiveResource();
        giveResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:necrotic_husk"
            }*/
            this.putString("resource", "demo:necrotic_husk");
        }});
        giveResource.setSource(source);
        giveResource.setTarget(target);

        giveResource.invoke(context);

        assertEquals(1, target.getResourceObjects().size(),
                "target should be given one resource"
        );
        assertEquals("demo:necrotic_husk", target.getResourceObjects().get(0).getId(),
                "resource should be the correct type"
        );
        assertTrue(target.getResourceObjects().get(0).hasTag("temporary"),
                "resource should be given the temporary tag"
        );
        assertFalse(target.getResourceObjects().get(0).getExhausted(),
                "resource should not be exhausted when first provided"
        );
    }

    @Test
    @DisplayName("invoke gives correct number of resources when specified")
    void invoke_givesCorrectNumberOfResourcesWhenSpecified() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        GiveResource giveResource = new GiveResource();
        giveResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"demo:necrotic_husk",
                "count": 2
            }*/
            this.putString("resource", "demo:necrotic_husk");
            this.putInteger("count", 2);
        }});
        giveResource.setSource(source);
        giveResource.setTarget(target);

        giveResource.invoke(context);

        assertEquals(2, target.getResourceObjects().size(),
                "target should be given two resources"
        );
        for (RPGLResource resource : target.getResourceObjects()) {
            assertEquals("demo:necrotic_husk", resource.getId(),
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
