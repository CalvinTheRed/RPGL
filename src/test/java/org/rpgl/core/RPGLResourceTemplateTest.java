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
    @DisplayName("newInstance comprehensive test using demo:action template")
    void newInstance_actionTemplate() {
        RPGLResourceTemplate resourceTemplate = DatapackLoader.DATAPACKS.get("demo").getResourceTemplate("action");
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
        assertEquals("demo:action", resource.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected= """
                ["action"]""";
        assertEquals(expected, resource.getTags().toString(),
                "incorrect field value: " + RPGLTaggableTO.TAGS_ALIAS
        );

        assertEquals(0, resource.getPotency(),
                "incorrect field value: " + RPGLResourceTO.POTENCY_ALIAS
        );
        assertFalse(resource.getExhausted(),
                "incorrect field value: " + RPGLResourceTO.EXHAUSTED_ALIAS
        );
        expected= """
                [{"chance":100,"completed":0,"required":1,"required_generator":{"bonus":1,"dice":[]},"subevent":"info_subevent","tags":["starting_turn"]}]""";
        assertEquals(expected, resource.getRefreshCriterion().toString(),
                "incorrect field value: " + RPGLResourceTO.REFRESH_CRITERION_ALIAS
        );
    }

}
