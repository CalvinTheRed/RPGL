package org.rpgl.datapack;

import org.junit.jupiter.api.*;
import org.rpgl.core.*;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("dummy").getEffectTemplate("dummy");
        assertNotNull(effectTemplate,
                "Effect template dummy:dummy failed to load."
        );
        assertEquals("Dummy Effect", effectTemplate.get("name"),
                "Effect template dummy:dummy failed to load the correct content."
        );
    }

    @Test
    @DisplayName("verify event templates can be loaded")
    void test2() {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("dummy").getEventTemplate("dummy");
        assertNotNull(eventTemplate,
                "Event template dummy:dummy failed to load."
        );
        assertEquals("Dummy Event", eventTemplate.get("name"),
                "Event template dummy:dummy failed to load the correct content."
        );
    }

    @Test
    @DisplayName("verify item templates can be loaded")
    void test3() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("dummy").getItemTemplate("dummy");
        assertNotNull(itemTemplate,
                "Item template dummy:dummy failed to load."
        );
        assertEquals("Dummy Item", itemTemplate.get("name"),
                "Item template dummy:dummy failed to load the correct content."
        );
    }

    @Test
    @DisplayName("verify object templates can be loaded")
    void test4() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("dummy").getObjectTemplate("dummy");
        assertNotNull(objectTemplate,
                "Object template dummy:dummy failed to load."
        );
        assertEquals("Dummy Object", objectTemplate.get("name"),
                "Object template dummy:dummy failed to load the correct content."
        );
    }

}
