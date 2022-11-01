package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class GiveEffectTest {

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
    @DisplayName("GiveEffect Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GiveEffect();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "GiveEffect Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("GiveEffect Subevent defaults to not being canceled")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GiveEffect();
        String subeventJsonString = "{" +
                "\"subevent\": \"give_effect\"," +
                "\"effect\":\"dummy:dummy\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GiveEffect giveEffect = (GiveEffect) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        giveEffect.setSource(object);
        giveEffect.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse((Boolean) giveEffect.subeventJson.get("cancel"),
                "GiveEffect Subevent should default to not being canceled."
        );
    }

    @Test
    @DisplayName("GiveEffect Subevent can be canceled")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GiveEffect();
        String subeventJsonString = "{" +
                "\"subevent\": \"give_effect\"," +
                "\"effect\":\"dummy:dummy\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GiveEffect giveEffect = (GiveEffect) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        giveEffect.setSource(object);
        giveEffect.prepare(context);
        giveEffect.cancel();

        /*
         * Verify subevent behaves as expected
         */
        assertTrue((Boolean) giveEffect.subeventJson.get("cancel"),
                "GiveEffect Subevent did not cancel correctly."
        );
    }

    @Test
    @DisplayName("GiveEffect Subevent gives Effect to target when not canceled")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GiveEffect();
        String subeventJsonString = "{" +
                "\"subevent\": \"give_effect\"," +
                "\"effect\":\"dummy:dummy\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GiveEffect giveEffect = (GiveEffect) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        giveEffect.setSource(object);
        giveEffect.prepare(context);
        giveEffect.setTarget(object);
        giveEffect.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, object.getEffects().length,
                "GiveEffect Subevent did not apply Effect correctly."
        );
    }

    @Test
    @DisplayName("GiveEffect Subevent does not give Effect to target when canceled")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GiveEffect();
        String subeventJsonString = "{" +
                "\"subevent\": \"give_effect\"," +
                "\"effect\":\"dummy:dummy\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GiveEffect giveEffect = (GiveEffect) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        giveEffect.setSource(object);
        giveEffect.prepare(context);
        giveEffect.setTarget(object);
        giveEffect.cancel();
        giveEffect.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(0, object.getEffects().length,
                "GiveEffect Subevent did not apply Effect correctly."
        );
    }

}
