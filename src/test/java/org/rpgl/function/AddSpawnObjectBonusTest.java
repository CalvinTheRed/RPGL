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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddSpawnObjectBonus class.
 *
 * @author Calvin Withun
 */
public class AddSpawnObjectBonusTest {

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
                () -> new AddSpawnObjectBonus().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("adds bonus to spawned object")
    void addsBonusToSpawnedObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.setSource(summoner);
        spawnObject.prepare(new DummyContext(), List.of());

        new AddSpawnObjectBonus().execute(null, spawnObject, new JsonObject() {{
            /*{
                "function": "add_spawn_object_bonus",
                "bonus": [
                    {
                        "field": "health_data.base",
                        "bonus": 10
                    },
                    {
                        "field": "health_data.current",
                        "bonus": 10
                    }
                ]
            }*/
            this.putString("function", "add_spawn_object_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("field", "health_data.base");
                    this.putInteger("bonus", 10);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("field", "health_data.current");
                    this.putInteger("bonus", 10);
                }});
            }});
        }}, new DummyContext(), List.of());

        String expected = """
                [{"bonus":10,"field":"health_data.base"},{"bonus":10,"field":"health_data.current"}]""";
        assertEquals(expected, spawnObject.json.getJsonArray("object_bonuses").toString(),
                "execute should add the correct object bonuses to the object's spawning instructions"
        );
    }

}