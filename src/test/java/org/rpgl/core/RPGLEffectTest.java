package org.rpgl.core;

import org.jsonutils.JsonFormatException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPGLEffectTest {

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
    @DisplayName("verify effect template contents are loaded correctly")
    void verifyEffectTemplateContentsAreLoadedCorrectly() throws JsonFormatException {
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("test").getEffectTemplate("dodging");
        // if this much is loaded, then it is safe to assume the whole file is loaded
        assertEquals(
                "Calvin Withun",
                effectTemplate.seek("metadata.author"),
                "Effect template test:dodging did not load the correct content."
        );
    }

}
