package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.subevent.InfoSubevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
    @DisplayName("generateRequired generates correct required count")
    void generateRequired_generatesCorrectRequiredCount() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");

        assertEquals(4, RPGLResource.generateRequired(resource.getRefreshCriterion().getJsonObject(0).getJsonObject("required_generator")),
                "required count of 4 should be generated in test mode"
        );
    }

    @Test
    @DisplayName("exhaust exhausts resource properly")
    void exhaust_exhaustsResourceProperly() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");
        resource.exhaust();

        assertTrue(resource.getExhausted(),
                "resource should be exhausted"
        );

        String expected = """
                [{"actor":"source","chance":100,"completed":0,"required":4,"required_generator":{"bonus":0,"dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "Refresh criterion should generate a required amount with 0 completed"
        );
    }

    @Test
    @DisplayName("refresh refreshes resource properly")
    void refresh_refreshesResourceProperly() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");
        resource.exhaust();
        resource.refresh();

        assertFalse(resource.getExhausted(),
                "resource should not be exhausted"
        );

        String expected = """
                [{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":0,"dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "Resource should have 0 completed and 0 required"
        );
    }

    @Test
    @DisplayName("checkCriterion partial criterion satisfaction")
    void checkCriterion_partialCriterionSatisfaction() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");
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
    @DisplayName("checkCriterion criterion satisfaction (single requirement)")
    void checkCriterion_criterionSatisfaction_singleRequirement() {
        RPGLResource resource = RPGLFactory.newResource("demo:pact_spell_slot");
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
    @DisplayName("checkCriterion criterion satisfaction (multiple requirements)")
    void checkCriterion_criterionSatisfaction_multipleRequirements() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");
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
                "Criterion should not yet be met for refreshing resource (1 of 4)"
        );
        assertFalse(resource.getExhausted(),
                "Resource should not be exhausted"
        );
        assertEquals(0, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "Resource should reset to 0 completions upon being refreshed"
        );
    }

    @Test
    @DisplayName("checkCriterion resolves successfully for source actor")
    void checkCriterion_resolvesSuccessfullyForSourceActor() {
        RPGLResource resource = RPGLFactory.newResource("demo:action");
        resource.exhaust();

        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
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
    @DisplayName("checkCriterion resolves successfully for target actor")
    void checkCriterion_resolvesSuccessfullyForTargetActor() {
        RPGLResource resource = RPGLFactory.newResource("demo:action");
        resource.exhaust();
        // manually edit resource criterion for testing
        resource.getRefreshCriterion().getJsonObject(0).putString("actor", "target");

        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
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
    @DisplayName("processSubevent does nothing when resource not exhausted")
    void processSubevent_doesNothingWhenResourceNotExhausted() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");

        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        source.addResource(resource);

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("long_rest");
        infoSubevent.setSource(source);
        infoSubevent.setTarget(target);

        resource.processSubevent(infoSubevent, source);

        assertEquals(0, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "resource should not increment completed counter on criterion if it is not exhausted"
        );
    }

    @Test
    @DisplayName("processSubevent increments completed counter when resource exhausted")
    void processSubevent_incrementsCompletedCounterWhenResourceExhausted() {
        RPGLResource resource = RPGLFactory.newResource("demo:necrotic_husk");
        resource.exhaust();

        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        source.addResource(resource);

        InfoSubevent infoSubevent = new InfoSubevent();
        infoSubevent.addTag("long_rest");
        infoSubevent.setSource(source);
        infoSubevent.setTarget(target);

        resource.processSubevent(infoSubevent, source);

        assertEquals(1, resource.getRefreshCriterion().getJsonObject(0).getInteger("completed"),
                "resource should increment completed counter on criterion if it is exhausted"
        );
    }

}
