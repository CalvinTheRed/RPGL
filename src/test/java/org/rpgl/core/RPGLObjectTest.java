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

public class RPGLObjectTest {

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
    @DisplayName("verify object template contents are loaded correctly")
    void verifyObjectTemplateContentsAreLoadedCorrectly() throws JsonFormatException {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("test").getObjectTemplate("goblin");
        // if this much is loaded, then it is safe to assume the whole file is loaded
        assertEquals("Calvin Withun", objectTemplate.seek("metadata.author"),
                "Object template test:goblin did not load the correct content."
        );
    }

}
