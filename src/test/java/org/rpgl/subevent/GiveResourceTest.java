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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
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
    @DisplayName("gives single resource by default")
    void givesSingleResourceByDefault() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GiveResource giveResource = new GiveResource();
        giveResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:class/warlock/the_undead_patron/necrotic_husk"
            }*/
            this.putString("resource", "std:class/warlock/the_undead_patron/necrotic_husk");
        }});
        giveResource.setSource(source);
        giveResource.prepare(new DummyContext(), List.of());
        giveResource.setTarget(target);
        giveResource.invoke(new DummyContext(), List.of());

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
    @DisplayName("overrides count and potency")
    void overridesCountAndPotency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GiveResource giveResource = new GiveResource();
        giveResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource":"std:class/warlock/the_undead_patron/necrotic_husk",
                "count": 2,
                "potency": 2
            }*/
            this.putString("resource", "std:class/warlock/the_undead_patron/necrotic_husk");
            this.putInteger("count", 2);
            this.putInteger("potency", 2);
        }});
        giveResource.setSource(source);
        giveResource.setTarget(target);

        giveResource.invoke(new DummyContext(), List.of());

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
