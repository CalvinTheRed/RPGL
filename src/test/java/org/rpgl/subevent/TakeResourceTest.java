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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.TakeResource class.
 *
 * @author Calvin Withun
 */
public class TakeResourceTest {

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
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("only takes resources with temporary tag")
    void onlyTakesResourcesWithTemporaryTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
        object.addResource(resource);

        TakeResource takeResource = new TakeResource();
        takeResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag": "necrotic_husk"
            }*/
            this.putString("resource_tag", "necrotic_husk");
        }});
        takeResource.setSource(object);
        takeResource.prepare(new DummyContext(), List.of());
        takeResource.setTarget(object);
        takeResource.invoke(new DummyContext(), List.of());

        assertEquals(1, object.getResourceObjects().size(),
                "target should not have a non-temporary resource taken away"
        );

        resource.addTag("temporary");
        takeResource.invoke(new DummyContext(), List.of());

        assertEquals(0, object.getResourceObjects().size(),
                "resource should be taken away once it has temporary tag"
        );
    }

    @Test
    @DisplayName("removes all matching resources by default")
    void removesAllMatchingResourcesByDefault() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 0; i < 5; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
            resource.addTag("temporary");
            object.addResource(resource);
        }

        TakeResource takeResource = new TakeResource();
        takeResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag": "necrotic_husk"
            }*/
            this.putString("resource_tag", "necrotic_husk");
        }});
        takeResource.setSource(object);
        takeResource.prepare(new DummyContext(), List.of());
        takeResource.setTarget(object);

        takeResource.invoke(new DummyContext(), List.of());

        assertEquals(0, object.getResourceObjects().size(),
                "target should have all matching resources taken away when count is not specified"
        );
    }

    @Test
    @DisplayName("removes specific number of resources")
    void removesSpecificNumberOfResources() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 0; i < 5; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
            resource.addTag("temporary");
            object.addResource(resource);
        }

        TakeResource takeResource = new TakeResource();
        takeResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag": "necrotic_husk",
                "count": 3
            }*/
            this.putString("resource_tag", "necrotic_husk");
            this.putInteger("count", 3);
        }});
        takeResource.setSource(object);
        takeResource.setTarget(object);

        takeResource.invoke(new DummyContext(), List.of());

        assertEquals(2, object.getResourceObjects().size(),
                "target have 3 of 5 resources taken away when count of 3 is specified"
        );
    }

}
