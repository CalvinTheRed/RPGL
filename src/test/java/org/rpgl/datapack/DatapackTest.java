package org.rpgl.datapack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.*;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatapackTest {

    @BeforeAll
    static void setup() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
    }

    @AfterAll
    static void cleanup() {
        DatapackLoader.DATAPACKS.clear();
    }

    @Test
    @DisplayName("verify effect template can be loaded")
    void verifyEffectTemplateCanBeLoaded() {
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("test").getEffectTemplate("dodging");
        assertNotNull(
                effectTemplate,
                "Effect template test:dodging failed to load."
        );
    }

    @Test
    @DisplayName("verify event template can be loaded")
    void verifyEventTemplateCanBeLoaded() {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("test").getEventTemplate("dodge");
        assertNotNull(
                eventTemplate,
                "Event template test:dodge failed to load."
        );
    }

    @Test
    @DisplayName("verify item template can be loaded")
    void verifyItemTemplateCanBeLoaded() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("test").getItemTemplate("scimitar");
        assertNotNull(
                itemTemplate,
                "Item template test:scimitar failed to load."
        );
    }

    @Test
    @DisplayName("verify object template can be loaded")
    void verifyObjectTemplateCanBeLoaded() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("test").getObjectTemplate("goblin");
        assertNotNull(
                objectTemplate,
                "Object template test:goblin failed to load."
        );
    }


}
