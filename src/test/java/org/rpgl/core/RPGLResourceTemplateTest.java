package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Testing class for the org.rpgl.core.RPGLResourceTemplate class.
 *
 * @author Calvin Withun
 */
public class RPGLResourceTemplateTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
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
    @DisplayName("newInstance comprehensive test using std_resources:action template")
    void newInstance_actionTemplate() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("std_resources").getResourceTemplate("action");
        RPGLResource resource = resourceTemplate.newInstance();
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, resource.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Action", resource.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("This resource allows you to take actions on your turn.", resource.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );
        assertEquals("std_resources:action", resource.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected= """
                ["action"]""";
        assertEquals(expected, resource.getTags().toString(),
                "incorrect field value: " + RPGLTaggableTO.TAGS_ALIAS
        );

        assertEquals(1, resource.getPotency(),
                "incorrect field value: " + RPGLResourceTO.POTENCY_ALIAS
        );
        assertFalse(resource.getExhausted(),
                "incorrect field value: " + RPGLResourceTO.EXHAUSTED_ALIAS
        );
        expected= """
                [{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":1,"dice":[]},"subevent":"info_subevent","tags":["start_turn"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "incorrect field value: " + RPGLResourceTO.REFRESH_CRITERION_ALIAS
        );
    }

    @Test
    @DisplayName("processRefreshCriterionGenerators unpacks dice correctly")
    void processRefreshCriterionGenerators_unpacksDiceCorrectly() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("demo").getResourceTemplate("necrotic_husk");
        RPGLResource resource = new RPGLResource();
        resource.join(resourceTemplate);

        RPGLResourceTemplate.processRefreshCriterionGenerators(resource);

        String expected;

        expected = """
                [{"required_generator":{"bonus":0,"dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "required generators should have unpacked all compact dice representations"
        );
    }

    @Test
    @DisplayName("processRefreshCriterion infers optional required generator values correctly")
    void processRefreshCriterion_infersOptionalRequiredGeneratorValuesCorrectly() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("demo").getResourceTemplate("pact_spell_slot");
        RPGLResource resource = new RPGLResource();
        resource.join(resourceTemplate);

        RPGLResourceTemplate.processRefreshCriterion(resource);

        String expected = """
                [{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":1,"dice":[]},"subevent":"info_subevent","tags":["short_rest"]},{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":1,"dice":[]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "required generators should contain inferred values for any unspecified optional values"
        );
    }

}