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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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

    @BeforeEach
    void beforeEach() {
        // create an anonymous class for Roll for the purpose of running tests on it
        roll = new Roll("roll") {

            @Override
            public Subevent clone() {
                return this;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return this;
            }

        };
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("default behavior should yield only a normal roll")
    void default_yieldOnlyNormalRoll() {
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
    @DisplayName("grantAdvantage should yield only an advantage roll")
    void grantAdvantage_yieldOnlyAdvantageRoll() {
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
    @DisplayName("grantDisadvantage should yield only a disadvantage roll")
    void grantDisadvantage_yieldOnlyDisadvantageRoll() {
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
    @DisplayName("grantAdvantage and grantDisadvantage should yield only a normal roll")
    void grantAdvantageGrantDisadvantage_yieldOnlyNormalRoll() {
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
    @DisplayName("roll default behavior rolls the first value (second roll higher)")
    void roll_defaultBehavior_secondRollHigher() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 5, 15 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        roll.roll();

        assertEquals(5, roll.get(),
                "default roll behavior should return the first value (5)"
        );
    }

    @Test
    @DisplayName("roll default behavior rolls the first value (second roll lower)")
    void roll_defaultBehavior_secondRollLower() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 15, 5 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        roll.roll();
        assertEquals(15, roll.get(),
                "default roll behavior should return the first value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage behavior rolls the higher value (second roll higher)")
    void roll_withAdvantage_secondRollHigher() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 5, 15 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        roll.grantAdvantage();
        roll.roll();
        assertEquals(15, roll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage behavior rolls the higher value (second roll lower)")
    void roll_withAdvantage_secondRollLower() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 15, 5 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        roll.grantAdvantage();
        roll.roll();
        assertEquals(15, roll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("roll disadvantage behavior rolls the higher value (first roll higher)")
    void roll_withDisadvantage_firstRollHigher() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 15, 5 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        roll.grantDisadvantage();
        roll.roll();
        assertEquals(5, roll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    @Test
    @DisplayName("roll disadvantage behavior rolls the higher value (first roll lower)")
    void roll_withDisadvantage_firstRollLower() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 5, 15 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        roll.grantDisadvantage();
        roll.roll();
        assertEquals(5, roll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    @Test
    @DisplayName("roll advantage and disadvantage behavior rolls the determined value (second roll higher)")
    void roll_withAdvantageAndDisadvantage_firstRollHigher() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 15, 5 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        roll.grantAdvantage();
        roll.grantDisadvantage();
        roll.roll();
        assertEquals(15, roll.get(),
                "advantage and disadvantage roll behavior should return the first value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage and disadvantage behavior rolls the determined value (first roll lower)")
    void roll_withAdvantageAndDisadvantage_firstRollLower() {
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 5, 15 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        roll.grantAdvantage();
        roll.grantDisadvantage();
        roll.roll();
        assertEquals(5, roll.get(),
                "advantage and disadvantage roll behavior should return the first value (5)"
        );
    }

    @Test
    @DisplayName("checkForReroll no reroll was requested")
    void checkForReroll_noRerollRequested() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        roll.setSource(source);
        roll.setTarget(target);
        roll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest_roll",
                "determined": [ 5, 15 ]
            }*/
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        roll.roll();
        roll.checkForReroll(new RPGLContext());
        assertEquals(5, roll.get(),
                "original roll should be preserved when no reroll is requested"
        );
    }

    // TODO additional unit tests needed for when re-rolls are requested... requires RPGLEffect functionality

}
