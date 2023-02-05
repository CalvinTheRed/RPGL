package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RPGLObjectTemplateTest {

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
    @DisplayName("processHealthData hit dice enumeration")
    void processHealthData_hitDiceAreEnumerated() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("young_red_dragon");
        RPGLObject object = new RPGLObject();
        object.join(objectTemplate);

        RPGLObjectTemplate.processHealthData(object);

        JsonArray hitDiceArray = object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getJsonArray("hit_dice");
        assertEquals(17, hitDiceArray.size(),
                "compact hit dice view should be unpacked"
        );
        for (int i = 0; i < hitDiceArray.size(); i++) {
            JsonObject hitDie = hitDiceArray.getJsonObject(i);
            assertFalse(hitDie.getBoolean("spent"),
                    "hit die (index " + i + ") should be unspent"
            );
            assertEquals(10, hitDie.getInteger("size"),
                    "hit die (index " + i + ") has the wrong size"
            );
            assertEquals(5, hitDie.getInteger("determined"),
                    "hit die (index " + i + ") has the wrong determined value"
            );
        }
    }

}
