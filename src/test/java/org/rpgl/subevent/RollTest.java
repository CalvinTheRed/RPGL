package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.Roll class.
 *
 * @author Calvin Withun
 */
public class RollTest {

    private Roll roll;

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

    @BeforeEach
    void beforeEach() {
        roll = new Roll("roll") {

            @Override
            public Subevent clone() {
                return this;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return this;
            }

            @Override
            public Roll run(RPGLContext context, JsonArray originPoint) {
                return this;
            }

            @Override
            public String getAbility(RPGLContext context) {
                return null;
            }

        };
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("defaults to flat roll")
    void defaultsToFlatRoll() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.setSource(object);
        roll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertFalse(roll.isAdvantageRoll(),
                "grantAdvantage should not yield an advantage roll"
        );
        assertFalse(roll.isDisadvantageRoll(),
                "grantAdvantage should not yield a disadvantage roll"
        );
        assertTrue(roll.isNormalRoll(),
                "grantAdvantage should yield a normal roll"
        );
    }

    @Test
    @DisplayName("gains advantage")
    void gainsAdvantage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.setSource(source);
        roll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        roll.grantAdvantage();

        assertTrue(roll.isAdvantageRoll(),
                "grantAdvantage should yield an advantage roll"
        );
        assertFalse(roll.isDisadvantageRoll(),
                "grantAdvantage should not yield a disadvantage roll"
        );
        assertFalse(roll.isNormalRoll(),
                "grantAdvantage should not yield a normal roll"
        );
    }

    @Test
    @DisplayName("gains disadvantage")
    void gainsDisadvantage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.setSource(source);
        roll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        roll.grantDisadvantage();

        assertFalse(roll.isAdvantageRoll(),
                "grantAdvantage should not yield an advantage roll"
        );
        assertTrue(roll.isDisadvantageRoll(),
                "grantAdvantage should yield a disadvantage roll"
        );
        assertFalse(roll.isNormalRoll(),
                "grantAdvantage should not yield a normal roll"
        );
    }

    @Test
    @DisplayName("cancels out advantage and disadvantage")
    void cancelsOutAdvantageAndDisadvantage() {
        roll.grantAdvantage();
        roll.grantDisadvantage();

        assertFalse(roll.isAdvantageRoll(),
                "grantAdvantage should not yield an advantage roll"
        );
        assertFalse(roll.isDisadvantageRoll(),
                "grantAdvantage should not yield a disadvantage roll"
        );
        assertTrue(roll.isNormalRoll(),
                "grantAdvantage should yield a normal roll"
        );
    }

    @Test
    @DisplayName("rolls normally")
    void rollsNormally() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(5);
            this.addInteger(15);
        }});

        roll.setSource(object);
        roll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        roll.roll();

        assertEquals(5, roll.get(),
                "default roll behavior should return the first value (5)"
        );

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(15);
            this.addInteger(5);
        }});

        roll.roll();

        assertEquals(15, roll.get(),
                "default roll behavior should return the first value (15)"
        );

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(15);
            this.addInteger(5);
        }});

        roll.grantAdvantage();
        roll.grantDisadvantage();
        roll.roll();

        assertEquals(15, roll.get(),
                "default roll behavior should return the first value (15)"
        );

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(5);
            this.addInteger(15);
        }});

        roll.roll();

        assertEquals(5, roll.get(),
                "default roll behavior should return the first value (5)"
        );
    }

    @Test
    @DisplayName("rolls with advantage")
    void rollsWithAdvantage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(5);
            this.addInteger(15);
        }});

        roll.setSource(source);
        roll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        roll.grantAdvantage();
        roll.roll();

        assertEquals(15, roll.get(),
                "advantage roll behavior should return the higher value (15)"
        );

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(15);
            this.addInteger(5);
        }});

        roll.roll();

        assertEquals(15, roll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("rolls with disadvantage")
    void roll_withDisadvantage_firstRollHigher() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(15);
            this.addInteger(5);
        }});

        roll.setSource(object);
        roll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        roll.grantDisadvantage();
        roll.roll();

        assertEquals(5, roll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );

        roll.json.insertJsonArray("determined", new JsonArray() {{
            this.addInteger(5);
            this.addInteger(15);
        }});

        roll.roll();

        assertEquals(5, roll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    // TODO additional unit tests needed for when re-rolls are requested... requires RPGLEffect functionality

}
