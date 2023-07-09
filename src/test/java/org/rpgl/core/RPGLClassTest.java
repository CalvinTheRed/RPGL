package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RPGLClassTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        RPGLCore.initializeTesting();
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
    @DisplayName("revokeLostResources revokes and unregisters resources")
    void revokeLostResources_revokesAndUnregistersResource() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLResource resource = RPGLFactory.newResource("std:action");
        String resourceUuid = resource.getUuid();
        object.addResource(resource);

        assertEquals(1, object.getResources().size(),
                "Dummy should have resource"
        );

        new RPGLClass().revokeLostResources(object, new JsonObject() {{
            this.putJsonArray("resources", new JsonArray() {{
                this.addString("std:action");
            }});
        }});

        assertTrue(object.getResources().asList().isEmpty(),
                "object should have no more resource after its resource is lost"
        );
        assertNull(UUIDTable.getResource(resourceUuid),
                "resource should be un-registered from UUIDTable after being revoked"
        );
    }

    @Test
    @DisplayName("revokeLostEvents revokes events")
    void revokeLostEvents_revokesEvents() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.getEvents().addString("std:young_red_dragon_fire_breath");

        assertEquals(1, object.getEvents().size(),
                "Dummy should have event"
        );

        new RPGLClass().revokeLostEvents(object, new JsonObject() {{
            this.putJsonArray("events", new JsonArray() {{
                this.addString("std:young_red_dragon_fire_breath");
            }});
        }});

        assertTrue(object.getEvents().asList().isEmpty(),
                "object should have no more events after its event is lost"
        );
    }

    @Test
    @DisplayName("revokeLostEffects revokes and unregisters effects")
    void revokeLostEffects_revokesAndUnregistersEffects() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLEffect effect = RPGLFactory.newEffect("std:dodge");
        String effectUuid = effect.getUuid();
        object.addEffect(effect);

        assertEquals(1, object.getEffects().size(),
                "Dummy should have effect"
        );

        new RPGLClass().revokeLostEffects(object, new JsonObject() {{
            this.putJsonArray("effects", new JsonArray() {{
                this.addString("std:dodge");
            }});
        }});

        assertTrue(object.getEffects().asList().isEmpty(),
                "object should have no more effect after its effect is lost"
        );
        assertNull(UUIDTable.getEffect(effectUuid),
                "effect should be un-registered from UUIDTable after being revoked"
        );
    }

    @Test
    @DisplayName("grantGainedResources grants resources")
    void grantGainedResources_grantsResources() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        new RPGLClass().grantGainedResources(object, new JsonObject() {{
            this.putJsonArray("resources", new JsonArray() {{
                this.addString("std:action");
            }});
        }});

        assertEquals(1, object.getResources().size(),
                "dummy should have 1 resource"
        );
    }

    @Test
    @DisplayName("grantGainedEvents grants events")
    void grantGainedEvents_grantsEvents() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        new RPGLClass().grantGainedEvents(object, new JsonObject() {{
            this.putJsonArray("events", new JsonArray() {{
                this.addString("std:young_red_dragon_fire_breath");
            }});
        }});

        assertEquals(1, object.getEvents().size(),
                "dummy should have 1 event"
        );
    }

    @Test
    @DisplayName("grantGainedEffects grants effects (no choices needed)")
    void grantGainedEffects_grantsEffects_noChoicesNeeded() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        JsonObject features = new JsonObject() {{
            this.putJsonArray("effects", new JsonArray() {{
                this.addString("std:fire_immunity");
                this.addString("std:cold_resistance");
            }});
        }};
        new RPGLClass().grantGainedEffects(object, features, new JsonObject());

        assertEquals(2, object.getEffects().size(),
                "dummy should have 2 effects"
        );
    }

    @Test
    @DisplayName("grantGainedEffects grants effects (choices needed)")
    void grantGainedEffects_grantsEffects_choicesNeeded() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        JsonObject features = new JsonObject() {{
            this.putJsonArray("effects", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("name", "testname");
                    this.putInteger("count", 2);
                    this.putJsonArray("options", new JsonArray() {{
                        this.addString("std:dodge");
                        this.addString("std:cold_resistance");
                        this.addString("std:fire_immunity");
                        this.addString("std:ring_of_protection");
                    }});
                }});
            }});
        }};
        JsonObject choices = new JsonObject() {{
           this.putJsonArray("testname", new JsonArray() {{
               this.addInteger(1);
               this.addInteger(2);
           }});
        }};
        new RPGLClass().grantGainedEffects(object, features, choices);

        List<RPGLEffect> effects = object.getEffectObjects();
        assertEquals(2, effects.size(),
                "dummy should have 2 effects"
        );
        assertEquals("std:cold_resistance", effects.get(0).getId(),
                "first effect should be std:cold_resistance"
        );
        assertEquals("std:fire_immunity", effects.get(1).getId(),
                "second effect should be std:fire_immunity"
        );
    }

    @Test
    @DisplayName("incrementRPGLObjectLevel increments level correctly (already has levels in class)")
    void incrementRPGLObjectLevel_incrementsLevelCorrectly_alreadyHasLevelsInClass() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.join(new JsonObject() {{
            this.putJsonArray("classes", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("name", "Test Class");
                    this.putString("id", "test:test");
                    this.putInteger("level", 1);
                }});
            }});
        }});

        RPGLClass rpglClass = new RPGLClass();
        rpglClass.setName("Test Class");
        rpglClass.setId("test:test");

        rpglClass.incrementRPGLObjectLevel(object);

        assertEquals(2, object.getLevel("test:test"),
                "object should be second level"
        );
    }

    @Test
    @DisplayName("incrementRPGLObjectLevel increments level correctly (first level in class)")
    void incrementRPGLObjectLevel_incrementsLevelCorrectly_firstLevelInClass() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");

        RPGLClass rpglClass = new RPGLClass();
        rpglClass.setName("Test Class");
        rpglClass.setId("test:test");

        rpglClass.incrementRPGLObjectLevel(object);

        assertEquals(1, object.getLevel("test:test"),
                "object should be first level"
        );
    }

    @Test
    @DisplayName("levelUpRPGLObject levels up correctly (with features)")
    void levelUpRPGLObject_levelsUpCorrectly_withFeatures() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.addEffect(RPGLFactory.newEffect("std:fire_immunity"));
        object.getEvents().addString("std:young_red_dragon_fire_breath");
        object.addResource(RPGLFactory.newResource("std:bonus_action"));

        // ensure object is set up properly
        assertEquals("std:fire_immunity", object.getEffectObjects().get(0).getId(),
                "dummy should have std:fire_immunity effect at start of test"
        );
        assertEquals("std:young_red_dragon_fire_breath", object.getEvents().getString(0),
                "dummy should have std:young_red_dragon_fire_breath event at start of test"
        );
        assertEquals("std:bonus_action", object.getResourceObjects().get(0).getId(),
                "dummy should have std:bonus_action resource at start of test"
        );

        RPGLClass rpglClass = new RPGLClass();
        rpglClass.setName("Test Class");
        rpglClass.setId("test:test");
        rpglClass.setClassFeatures(new JsonObject() {{
            /*{
                "1": {
                    "gain": {
                        "effects": [
                            {
                                "name": "Test Effect Choice",
                                "count": 1,
                                "options": [
                                    "std:fire_resistance",
                                    "std:cold_resistance"
                                ]
                            }
                        ],
                        "events": [
                            "std:fire_bolt"
                        ],
                        "resources": [
                            "std:action"
                        ]
                    },
                    "lose": {
                        "effects": [
                            "std:fire_immunity"
                        ],
                        "events": [
                            "std:young_red_dragon_fire_breath"
                        ],
                        "resources": [
                            "std:bonus_action"
                        ]
                    }
                }
            }*/
            this.putJsonObject("1", new JsonObject() {{
                this.putJsonObject("gain", new JsonObject() {{
                    this.putJsonArray("effects", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("name", "Test Effect Choice");
                            this.putInteger("count", 1);
                            this.putJsonArray("options", new JsonArray() {{
                                this.addString("std:fire_resistance");
                                this.addString("std:cold_resistance");
                            }});
                        }});
                    }});
                    this.putJsonArray("events", new JsonArray() {{
                        this.addString("std:fire_bolt");
                    }});
                    this.putJsonArray("resources", new JsonArray() {{
                        this.addString("std:action");
                    }});
                }});
                this.putJsonObject("lose", new JsonObject() {{
                    this.putJsonArray("effects", new JsonArray() {{
                        this.addString("std:fire_immunity");
                    }});
                    this.putJsonArray("events", new JsonArray() {{
                        this.addString("std:young_red_dragon_fire_breath");
                    }});
                    this.putJsonArray("resources", new JsonArray() {{
                        this.addString("std:bonus_action");
                    }});
                }});
            }});
        }});

        JsonObject choices = new JsonObject() {{
           this.putJsonArray("Test Effect Choice", new JsonArray() {{
               this.addInteger(1);
           }});
        }};

        rpglClass.levelUpRPGLObject(object, choices);

        assertEquals(1, object.getLevel("test:test"),
                "object should have 1 level in class test:test after level up"
        );

        List<RPGLEffect> effects = object.getEffectObjects();
        assertEquals(1, effects.size(),
                "dummy should have 1 effect after level up"
        );
        assertEquals("std:cold_resistance", effects.get(0).getId(),
                "dummy should have std:cold_resistance effect after level up"
        );

        JsonArray events = object.getEvents();
        assertEquals(1, events.size(),
                "dummy should have 1 event after level up"
        );
        assertEquals("std:fire_bolt", events.getString(0),
                "dummy should have std:fire_bolt event after level up"
        );

        List<RPGLResource> resources = object.getResourceObjects();
        assertEquals(1, resources.size(),
                "dummy should have 1 resource after level up"
        );
        assertEquals("std:action", resources.get(0).getId(),
                "dummy should have std:action resource after level up"
        );
    }

    @Test
    @DisplayName("setBaseClass sets base features correctly")
    void setBaseClass_setsBaseFeaturesCorrectly() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLClass rpglClass = RPGLFactory.getClass("std:fighter");

        JsonObject choices = new JsonObject() {{
            this.putJsonArray("Skill Proficiencies", new JsonArray() {{
                this.addInteger(2);
                this.addInteger(4);
            }});
            this.putJsonArray("Fighting Style", new JsonArray() {{
                this.addInteger(0);
            }});
        }};

        rpglClass.setBaseClass(object, choices);

        List<RPGLEffect> effects = object.getEffectObjects();
        assertEquals(13, effects.size(),
                "dummy should have 13 effects after setting base class to std:fighter"
        );
        assertEquals("std:save_proficiency_str", effects.get(0).getId());
        assertEquals("std:save_proficiency_con", effects.get(1).getId());
        assertEquals("std:armor_proficiency_heavy", effects.get(2).getId());
        assertEquals("std:skill_proficiency_athletics", effects.get(3).getId());
        assertEquals("std:skill_proficiency_insight", effects.get(4).getId());
        assertEquals("std:armor_proficiency_light", effects.get(5).getId());
        assertEquals("std:armor_proficiency_medium", effects.get(6).getId());
        assertEquals("std:shield_proficiency", effects.get(7).getId());
        assertEquals("std:weapon_proficiency_simple_melee", effects.get(8).getId());
        assertEquals("std:weapon_proficiency_simple_ranged", effects.get(9).getId());
        assertEquals("std:weapon_proficiency_martial_melee", effects.get(10).getId());
        assertEquals("std:weapon_proficiency_martial_ranged", effects.get(11).getId());
        assertEquals("std:fighting_style_archery", effects.get(12).getId());

        JsonArray events = object.getEvents();
        assertEquals(1, events.size(),
                "dummy should have 1 event after setting base class to std:fighter"
        );
        assertEquals("std:second_wind", events.getString(0));

        List<RPGLResource> resources = object.getResourceObjects();
        assertEquals(1, resources.size(),
                "dummy should have 1 resource after setting base class to std:fighter"
        );
        assertEquals("std:second_wind_charge", resources.get(0).getId());

        assertEquals(1, object.getLevel("std:fighter"),
                "dummy should have 1 level in fighter after setting base class to std:fighter"
        );
    }

}
