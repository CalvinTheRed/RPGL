package org.rpgl.datapack;

import org.junit.jupiter.api.*;
import org.rpgl.core.*;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing class for datapack.Datapack class.
 *
 * @author Calvin Withun
 */
public class DatapackTest {

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

    @Test
    @DisplayName("verify effect templates can be loaded")
    void test1() {
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("test").getEffectTemplate("dummy");
        assertNotNull(effectTemplate,
                "Effect template test:dummy failed to load."
        );
        assertEquals("Dummy Effect", effectTemplate.get("name"),
                "Effect template test:dummy failed to load the correct content."
        );
    }

    @Test
    @DisplayName("verify event templates can be loaded")
    void test2() {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("test").getEventTemplate("dummy");
        assertNotNull(eventTemplate,
                "Event template test:dummy failed to load."
        );
        assertEquals("Dummy Event", eventTemplate.get("name"),
                "Event template test:dummy failed to load the correct content."
        );
    }

    @Test
    @DisplayName("verify item templates can be loaded")
    void test3() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("test").getItemTemplate("blank");
        assertNotNull(itemTemplate,
                "Item template test:blank failed to load."
        );
        assertEquals("Blank Item", itemTemplate.get("name"),
                "Item template test:blank failed to load the correct content."
        );
    }

    @Test
    @DisplayName("verify object templates can be loaded")
    void test4() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("test").getObjectTemplate("blank");
        assertNotNull(objectTemplate,
                "Object template test:blank failed to load."
        );
        assertEquals("Blank Object", objectTemplate.get("name"),
                "Object template test:blank failed to load the correct content."
        );
    }

}
