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

public class RPGLItemTest {

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
    @DisplayName("verify item template contents are loaded correctly")
    void verifyItemTemplateContentsAreLoadedCorrectly() throws JsonFormatException {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("test").getItemTemplate("scimitar");
        // if this much is loaded, then it is safe to assume the whole file is loaded
        assertEquals(
                "Calvin Withun",
                itemTemplate.seek("metadata.author"),
                "Item template test:scimitar did not load the correct content."
        );
    }

}
