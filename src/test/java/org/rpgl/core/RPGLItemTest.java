package org.rpgl.core;

import org.jsonutils.JsonFormatException;
import org.junit.jupiter.api.*;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPGLItemTest {

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
    @DisplayName("verify item template contents are loaded correctly")
    void verifyItemTemplateContentsAreLoadedCorrectly() throws JsonFormatException {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("test").getItemTemplate("scimitar");
        // if this much is loaded, then it is safe to assume the whole file is loaded
        assertEquals("Calvin Withun", itemTemplate.seek("metadata.author"),
                "Item template test:scimitar did not load the correct content."
        );
    }

}
