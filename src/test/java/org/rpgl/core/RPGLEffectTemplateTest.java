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
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.core.RPGLEffectTemplate class.
 *
 * @author Calvin Withun
 */
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
    @DisplayName("newInstance comprehensive test using std:common/damage/immunity/fire template")
    void newInstance_fireImmunity() {
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("std")
                .getEffectTemplate("common/damage/immunity/fire");
        RPGLEffect effect = effectTemplate.newInstance();
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, effect.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Immunity", effect.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("Creatures with this effect take 0 fire damage.", effect.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );
        assertEquals("std:common/damage/immunity/fire", effect.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected = """
                {"damage_affinity":[{"conditions":[{"condition":"objects_match","effect":"target","subevent":"target"}],"functions":[{"damage_type":"fire","function":"grant_immunity"}]}]}""";
        assertEquals(expected, effect.getSubeventFilters().toString(),
                "incorrect field value: " + RPGLEffectTO.SUBEVENT_FILTERS_ALIAS
        );
    }

    @Test
    @DisplayName("processScale scales effect data according to resource potency")
    void processScale_scalesEffectDataAccordingToResourcePotency() {
        RPGLEffect effect = new RPGLEffect();
        effect.join(new JsonObject() {{
            /*{
                "scale": [
                    {
                        "resource_tags": [ "spell_slot" ],
                        "minimum_potency": 1,
                        "scale": [
                            {
                                "field": "field_1",
                                "magnitude": 1
                            },
                            {
                                "field": "field_2",
                                "magnitude": 2
                            }
                        ]
                    }
                ],
                "field_1": 1,
                "field_2": 2
            }*/
            this.putJsonArray("scale", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("resource_tags", new JsonArray() {{
                        this.addString("spell_slot");
                    }});
                    this.putInteger("minimum_potency", 1);
                    this.putJsonArray("scale", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("field", "field_1");
                            this.putInteger("magnitude", 1);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putString("field", "field_2");
                            this.putInteger("magnitude", 2);
                        }});
                    }});
                }});
            }});
            this.putInteger("field_1", 1);
            this.putInteger("field_2", 2);
        }});

        RPGLEffectTemplate.processScale(effect, List.of(RPGLFactory.newResource("std:common/spell_slot/02")));

        assertEquals(2, effect.getInteger("field_1"),
                "field 1 should be increased by 1 from resource scaling"
        );
        assertEquals(4, effect.getInteger("field_2"),
                "field 2 should be increased by 2 from resource scaling"
        );
    }

}
