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
    @DisplayName("invoke only takes resources with temporary tag")
    void invoke_onlyTakesResourcesWithTemporaryTag() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
        target.addResource(resource);

        TakeResource takeResource = new TakeResource();
        takeResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag": "necrotic_husk"
            }*/
            this.putString("resource_tag", "necrotic_husk");
        }});
        takeResource.setSource(source);
        takeResource.prepare(context, List.of());
        takeResource.setTarget(target);

        takeResource.invoke(context, List.of());

        assertEquals(1, target.getResourceObjects().size(),
                "target should not have a non-temporary resource taken away"
        );

        resource.addTag("temporary");
        takeResource.invoke(context, List.of());

        assertEquals(0, target.getResourceObjects().size(),
                "resource should be taken away once it has temporary tag"
        );
    }

    @Test
    @DisplayName("invoke removes all matching resources when no count specified")
    void invoke_removesAllMatchingResourcesWhenNoCountSpecified() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 0; i < 5; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
            resource.addTag("temporary");
            target.addResource(resource);
        }

        TakeResource takeResource = new TakeResource();
        takeResource.joinSubeventData(new JsonObject() {{
            /*{
                "resource_tag": "necrotic_husk"
            }*/
            this.putString("resource_tag", "necrotic_husk");
        }});
        takeResource.setSource(source);
        takeResource.prepare(context, List.of());
        takeResource.setTarget(target);

        takeResource.invoke(context, List.of());

        assertEquals(0, target.getResourceObjects().size(),
                "target should have all matching resources taken away when count is not specified"
        );
    }

    @Test
    @DisplayName("invoke removes correct number of resources when count specified")
    void invoke_removesCorrectNumberOfResourcesWhenCountSpecified() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        for (int i = 0; i < 5; i++) {
            RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
            resource.addTag("temporary");
            target.addResource(resource);
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
        takeResource.setSource(source);
        takeResource.setTarget(target);

        takeResource.invoke(context, List.of());

        assertEquals(2, target.getResourceObjects().size(),
                "target have 3 of 5 resources taken away when count of 3 is specified"
        );
    }

}
