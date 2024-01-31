package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Testing class for the org.rpgl.core.RPGLResourceTemplate class.
 *
 * @author Calvin Withun
 */
public class RPGLResourceTemplateTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("creates new resources")
    void createsNewResources() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("std").getResourceTemplate("common/action/01");
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
        assertEquals("std:common/action/01", resource.getId(),
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
    @DisplayName("unpacks refresh criterion generator dice")
    void unpacksRefreshCriterionGeneratorDice() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("std").getResourceTemplate("class/warlock/the_undead_patron/necrotic_husk");
        RPGLResource resource = new RPGLResource();
        resourceTemplate.setup(resource);

        RPGLResourceTemplate.processRefreshCriterionGenerators(resource);

        String expected = """
                [{"required_generator":{"bonus":0,"dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "required generators should have unpacked all compact dice representations"
        );
    }

    @Test
    @DisplayName("sets default required generator")
    void setsDefaultRequiredGenerator() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("std").getResourceTemplate("common/spell_slot/pact_magic/01");
        RPGLResource resource = new RPGLResource();
        resourceTemplate.setup(resource);

        RPGLResourceTemplate.processRefreshCriterion(resource);

        String expected = """
                [{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":1,"dice":[]},"subevent":"info_subevent","tags":["short_rest"]},{"actor":"source","chance":100,"completed":0,"required":0,"required_generator":{"bonus":1,"dice":[]},"subevent":"info_subevent","tags":["long_rest"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "required generators should contain inferred values for any unspecified optional values"
        );
    }

}
