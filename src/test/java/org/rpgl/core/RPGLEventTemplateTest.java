package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLEventTO;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPGLEventTemplateTest {

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
    @DisplayName("processSubeventDamage damage dice are unpacked")
    void processSubeventDamage_damageDiceAreUnpacked() {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("demo").getEventTemplate("young_red_dragon_fire_breath");
        RPGLEvent event = new RPGLEvent();
        event.join(eventTemplate);

        RPGLEventTemplate.processSubeventDamage(event);

        JsonArray damageDiceArray = event.getJsonArray(RPGLEventTO.SUBEVENTS_ALIAS).getJsonObject(0).getJsonArray("damage").getJsonObject(0).getJsonArray("dice");
        assertEquals(16, damageDiceArray.size(),
                "compact hit dice view should be unpacked into 16 objects"
        );
        for (int i = 0; i < damageDiceArray.size(); i++) {
            JsonObject damageDie = damageDiceArray.getJsonObject(i);
            assertEquals(6, damageDie.getInteger("size"),
                    "damage die (index " + i + ") has the wrong size"
            );
            assertEquals(3, damageDie.getInteger("determined"),
                    "damage die (index " + i + ") has the wrong determined value"
            );
        }
    }

    @Test
    @DisplayName("newInstance comprehensive test using demo:young_red_dragon_fire_breath template")
    void newInstance_youngRedDragonFireBreathTemplate() {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("demo").getEventTemplate("young_red_dragon_fire_breath");
        RPGLEvent event = eventTemplate.newInstance();

        assertEquals("{\"author\":\"Calvin Withun\"}", event.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Breath", event.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("The dragon breathes fire.", event.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        assertEquals("{}", event.getJsonObject(RPGLEventTO.AREA_OF_EFFECT_ALIAS).toString(),
                "incorrect field value: " + RPGLEventTO.AREA_OF_EFFECT_ALIAS
        );
        assertEquals("[{\"damage\":[{\"bonus\":0,\"dice\":[{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6},{\"determined\":3,\"size\":6}],\"type\":\"fire\"}],\"damage_on_pass\":\"half\",\"determined\":1,\"difficulty_class_ability\":\"con\",\"save_ability\":\"dex\",\"subevent\":\"saving_throw\"}]", event.getJsonArray(RPGLEventTO.SUBEVENTS_ALIAS).toString(),
                "incorrect field value: " + RPGLEventTO.SUBEVENTS_ALIAS
        );
    }

}