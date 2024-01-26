package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.InvokeSubevent class.
 *
 * @author Calvin Withun
 */
public class InvokeSubeventTest {

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new InvokeSubevent().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("invokes subevent")
    void invokesSubevent() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);

        new InvokeSubevent().execute(effect, new DummySubevent(), new JsonObject() {{
            /*{
                "function": "invoke_subevent",
                "subevent": {
                  "subevent": "dummy_subevent"
                },
                "source": {
                  "from": "effect",
                  "object": "source"
                },
                "targets": [
                  {
                    "from": "effect",
                    "object": "source"
                  }
                ]
            }*/
            this.putString("function", "invoke_subevent");
            this.putJsonObject("subevent", new JsonObject() {{
                this.putString("subevent", "dummy_subevent");
            }});
            this.putJsonObject("source", new JsonObject() {{
                this.putString("from", "effect");
                this.putString("object", "source");
            }});
            this.putJsonArray("targets", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("from", "effect");
                    this.putString("object", "source");
                }});
            }});
        }}, new DummyContext());

        assertEquals(1, DummySubevent.counter,
                "execute should invoke dummy subevent"
        );
    }

}
