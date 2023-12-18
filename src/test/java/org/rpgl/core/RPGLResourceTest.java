package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.subevent.InfoSubevent;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.core.RPGLItem class.
 *
 * @author Calvin Withun
 */
public class RPGLResourceTest {

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
    @DisplayName("generates required count")
    void generatesRequiredCount() {
        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");

        assertEquals(4, RPGLResource.generateRequired(resource.getRefreshCriterion().getJsonObject(0).getJsonObject("required_generator")),
                "required count of 4 should be generated in test mode"
        );
    }

    @Test
    @DisplayName("exhausts and refreshes")
    void exhaustsAndRefreshes() {
        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
        String expected;

        resource.exhaust();
        assertTrue(resource.getExhausted(),
                "resource should be exhausted"
        );

        expected = """
                [{"actor":"source","chance":100,"completed":0,"required":4,"required_generator":{"bonus":0,"dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "Refresh criterion should generate a required amount with 0 completed"
        );

        resource.refresh();
        assertFalse(resource.getExhausted(),
                "resource should not be exhausted"
        );

        expected = """
                [{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":0,"dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "Resource should have 0 completed and 0 required"
        );
    }

    @Test
    @DisplayName("increments completed count")
    void incrementsCompletedCount() {
        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
        resource.exhaust();

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("long_rest");

        assertFalse(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), null),
                "Criterion should not yet be met for refreshing resource (1 of 4)"
        );
        assertTrue(resource.getExhausted(),
                "Resource should still be exhausted"
        );
        assertEquals(1, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "Resource should have recorded 1 completion (of 4)"
        );
    }

    @Test
    @DisplayName("refreshes when all completions are done")
    void refreshesWhenAllCompletionsAreDone() {
        RPGLResource resource = RPGLFactory.newResource("std:common/spell_slot/pact_magic/01");
        resource.exhaust();

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("short_rest");

        assertTrue(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), null),
                "Criterion should be met for refreshing resource (1 of 1)"
        );
        assertFalse(resource.getExhausted(),
                "Resource should not be exhausted"
        );
        assertEquals(0, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "Resource should reset to 0 completions upon being refreshed"
        );
    }

    @Test
    @DisplayName("resets completions upon refresh")
    void resetsCompletionsUponRefresh() {
        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
        resource.exhaust();

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("long_rest");

        assertFalse(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), null),
                "Criterion should not yet be met for refreshing resource (1 of 4)"
        );
        assertTrue(resource.getExhausted(),
                "Resource should still be exhausted"
        );
        assertEquals(1, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "Resource should have recorded 1 completion (of 4)"
        );

        assertFalse(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), null),
                "Criterion should not yet be met for refreshing resource (2 of 4)"
        );
        assertFalse(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), null),
                "Criterion should not yet be met for refreshing resource (3 of 4)"
        );
        assertTrue(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), null),
                "Criterion should be met for refreshing resource (4 of 4)"
        );
        assertFalse(resource.getExhausted(),
                "Resource should not be exhausted"
        );
        assertEquals(0, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "Resource should reset to 0 completions upon being refreshed"
        );
    }

    @Test
    @DisplayName("checks criterion (source actor)")
    void checksCriterion_sourceActor() {
        RPGLResource resource = RPGLFactory.newResource("std:common/action/01");
        resource.exhaust();

        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.addResource(resource);

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("start_turn");
        infoSubevent.setSource(source);
        infoSubevent.setTarget(target);

        assertFalse(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), target),
                "criterion should not be satisfied when actor is target if the criterion requires the source"
        );
        assertTrue(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), source),
                "criterion should be satisfied when actor is source if the criterion requires the source"
        );
    }

    @Test
    @DisplayName("checks criterion (target actor)")
    void checksCriterion_targetActor() {
        RPGLResource resource = RPGLFactory.newResource("std:common/action/01");
        resource.exhaust();
        // manually edit resource criterion for testing
        resource.getRefreshCriterion().getJsonObject(0).putString("actor", "target");

        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.addResource(resource);

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("start_turn");
        infoSubevent.setSource(source);
        infoSubevent.setTarget(target);

        assertFalse(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), source),
                "criterion should not be satisfied when actor is source if the criterion requires the target"
        );
        assertTrue(resource.checkCriterion(infoSubevent, resource.getRefreshCriterion().getJsonObject(0), target),
                "criterion should be satisfied when actor is target if the criterion requires the target"
        );
    }

    @Test
    @DisplayName("only updates completed counter while exhausted")
    void onlyUpdatesCompletedCounterWhileExhausted() {
        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");

        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.addResource(resource);

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("long_rest");
        infoSubevent.setSource(object);
        infoSubevent.setTarget(object);

        resource.processSubevent(infoSubevent, object);

        assertEquals(0, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "resource should not increment completed counter on criterion if it is not exhausted"
        );
    }

    @Test
    @DisplayName("increments completed counter when processing subevent")
    void incrementsCompletedCounterWhenProcessingSubevent() {
        RPGLResource resource = RPGLFactory.newResource("std:class/warlock/the_undead_patron/necrotic_husk");
        resource.exhaust();

        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.addResource(resource);

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("long_rest");
        infoSubevent.setSource(object);
        infoSubevent.setTarget(object);

        resource.processSubevent(infoSubevent, object);

        assertEquals(1, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "resource should increment completed counter on criterion if it is exhausted"
        );
    }

}
