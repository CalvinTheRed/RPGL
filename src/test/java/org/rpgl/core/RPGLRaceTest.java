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

public class RPGLRaceTest {

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
        RPGLResource resource = RPGLFactory.newResource("std:common/action/01");
        String resourceUuid = resource.getUuid();
        object.addResource(resource);

        assertEquals(1, object.getResources().size(),
                "Dummy should have resource"
        );

        new RPGLRace().revokeLostResources(object, new JsonObject() {{
            this.putJsonArray("resources", new JsonArray() {{
                this.addString("std:common/action/01");
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
        object.getEvents().addString("std:test");

        assertEquals(1, object.getEvents().size(),
                "Dummy should have event"
        );

        new RPGLRace().revokeLostEvents(object, new JsonObject() {{
            this.putJsonArray("events", new JsonArray() {{
                this.addString("std:test");
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
        RPGLEffect effect = RPGLFactory.newEffect("std:common/dodge");
        String effectUuid = effect.getUuid();
        object.addEffect(effect);

        assertEquals(1, object.getEffects().size(),
                "Dummy should have effect"
        );

        new RPGLRace().revokeLostEffects(object, new JsonObject() {{
            this.putJsonArray("effects", new JsonArray() {{
                this.addString("std:common/dodge");
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
        new RPGLRace().grantGainedResources(object, new JsonObject() {{
            this.putJsonArray("resources", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("resource", "std:common/action/01");
                    this.putInteger("count", 1);
                }});
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
        new RPGLRace().grantGainedEvents(object, new JsonObject() {{
            this.putJsonArray("events", new JsonArray() {{
                this.addString("std:object/dragon/red/young/breath");
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
                this.addString("std:common/damage/immunity/fire");
                this.addString("std:common/damage/resistance/cold");
            }});
        }};
        new RPGLRace().grantGainedEffects(object, features, new JsonObject());

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
                        this.addString("std:common/dodge");
                        this.addString("std:common/damage/resistance/cold");
                        this.addString("std:common/damage/immunity/fire");
                        this.addString("std:item/ring/ring_of_protection");
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
        new RPGLRace().grantGainedEffects(object, features, choices);

        List<RPGLEffect> effects = object.getEffectObjects();
        assertEquals(2, effects.size(),
                "dummy should have 2 effects"
        );
        assertEquals("std:common/damage/resistance/cold", effects.get(0).getId(),
                "first effect should be std:common/damage/resistance/cold"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(1).getId(),
                "second effect should be std:common/damage/immunity/fire"
        );
    }

    @Test
    @DisplayName("levelUpRPGLObject grants correct features on level up")
    void levelUpRPGLObject_grantsCorrectFeaturesOnLevelUp() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");

        RPGLFactory.getRace("std:human").levelUpRPGLObject(object, new JsonObject(), 1);

        assertEquals(1, object.getEffects().size(),
                "object should have 1 effect from leveling up at level 1 with race std:human"
        );
    }

}
