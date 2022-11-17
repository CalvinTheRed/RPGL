package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for subevent.DamageDelivery class.
 *
 * @author Calvin Withun
 */
public class DamageDeliveryTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
        UUIDTable.clear();
    }

    @Test
    @DisplayName("DamageDelivery Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageDelivery();
        String subeventJsonString = """
                {
                    "subevent": "not_a_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "DamageDelivery Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("DamageDelivery Subevent can change targets")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageDelivery();
        String subeventJsonString = """
                {
                    "subevent": "damage_delivery",
                    "damage": {
                        "fire": 1
                    }
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageDelivery damageDelivery = (DamageDelivery) subevent.clone(subeventJson);
        RPGLObject originalObject = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLObject newObject = RPGLFactory.newObject("dummy:dummy_hollow");
        assert originalObject != null;
        assert newObject != null;

        /*
         * Invoke subevent method
         */
        damageDelivery.setSource(originalObject);
        damageDelivery.setTarget(newObject);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(newObject, damageDelivery.getTarget(),
                "DamageDelivery should have a new target after it is reassigned."
        );
    }

}
