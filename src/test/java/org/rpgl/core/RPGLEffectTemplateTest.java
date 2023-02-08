package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPGLEffectTemplateTest {

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
    @DisplayName("newInstance comprehensive test using demo:fire_immunity template")
    void newInstance_fireImmunity() {
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("demo").getEffectTemplate("fire_immunity");
        RPGLEffect effect = effectTemplate.newInstance();
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, effect.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Immunity", effect.getString(DatapackContentTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("Creatures with this effect take 0 fire damage.", effect.getString(DatapackContentTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        expected = """
                {"damage_affinity":{"conditions":[],"functions":[]}}""";
        assertEquals(expected, effect.getJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS).toString(),
                "incorrect field value: " + RPGLEffectTO.SUBEVENT_FILTERS_ALIAS
        );
    }

}
