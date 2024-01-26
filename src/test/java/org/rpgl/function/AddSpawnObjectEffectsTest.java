package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.SpawnObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddSpawnObjectEffects class.
 *
 * @author Calvin Withun
 */
public class AddSpawnObjectEffectsTest {

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
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new AddSpawnObjectEffects().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("adds effects to spawned object")
    void addsEffectsToSpawnedObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.setSource(summoner);
        spawnObject.prepare(new DummyContext());

        new AddSpawnObjectEffects().execute(null, spawnObject, new JsonObject() {{
            /*{
                "function": "add_spawn_object_effects",
                "effects": [
                    "std:common/damage/immunity/fire",
                    "std:common/damage/immunity/poison"
                ]
            }*/
            this.putString("function", "add_spawn_object_effects");
            this.putJsonArray("effects", new JsonArray() {{
                this.addString("std:common/damage/immunity/fire");
                this.addString("std:common/damage/immunity/poison");
            }});
        }}, new DummyContext());

        String expected = """
                ["std:common/damage/immunity/fire","std:common/damage/immunity/poison"]""";
        assertEquals(expected, spawnObject.json.getJsonArray("extra_effects").toString(),
                "execute should add the correct object effects to the object's spawning instructions"
        );
    }

}
