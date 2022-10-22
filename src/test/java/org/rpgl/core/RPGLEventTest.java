package org.rpgl.core;

import org.jsonutils.JsonFormatException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPGLEventTest {

    @BeforeAll
    static void setup() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
    }

    @AfterAll
    static void cleanup() {
        DatapackLoader.DATAPACKS.clear();
        UUIDTable.clear();
    }

    @Test
    @DisplayName("verify event template contents are loaded correctly")
    void verifyEventTemplateContentsAreLoadedCorrectly() throws JsonFormatException {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("test").getEventTemplate("dodge");
        // if this much is loaded, then it is safe to assume the whole file is loaded
        assertEquals("Calvin Withun", eventTemplate.seek("metadata.author"),
                "Event template test:dodge did not load the correct content."
        );
    }

}
