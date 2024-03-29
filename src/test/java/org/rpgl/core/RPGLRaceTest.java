package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.core.RPGLRace class.
 *
 * @author Calvin Withun
 */
public class RPGLRaceTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("revokes old resources")
    void revokesOldResources() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("revokes old events")
    void revokesOldEvents() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("revokes old effects")
    void revokesOldEffects() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("gains new resources")
    void gainsNewResources() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("gains new events")
    void gainsNewEvents() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("gains new effects")
    void gainsNewEffects() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("gains new effects (choices needed)")
    void gainsNewEffects_choicesNeeded() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("levels up objects")
    void levelsUpObjects() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLFactory.getRace("std:human").levelUpRPGLObject(object, new JsonObject(), 1);

        assertEquals(1, object.getEffects().size(),
                "object should have 1 effect from leveling up at level 1 with race std:human"
        );
    }

}
