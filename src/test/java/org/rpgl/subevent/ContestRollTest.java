package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for abstract class ContestRoll.
 *
 * @author Calvin Withun
 */
public class ContestRollTest {

    private ContestRoll contestRoll;

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @BeforeEach
    void beforeEach() {
        // create an anonymous class for ContestRoll for the purpose of running tests on it
        contestRoll = new ContestRoll("contest_roll") {

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

    @Test
    @DisplayName("default behavior should yield only a normal roll")
    void default_yieldOnlyNormalRoll() {
        assertFalse(contestRoll.isAdvantageRoll(),
                "grantAdvantage should not yield an advantage roll"
        );
        assertFalse(contestRoll.isDisadvantageRoll(),
                "grantAdvantage should not yield a disadvantage roll"
        );
        assertTrue(contestRoll.isNormalRoll(),
                "grantAdvantage should yield a normal roll"
        );
    }

    @Test
    @DisplayName("grantAdvantage should yield only an advantage roll")
    void grantAdvantage_yieldOnlyAdvantageRoll() {
        contestRoll.grantAdvantage();
        assertTrue(contestRoll.isAdvantageRoll(),
                "grantAdvantage should yield an advantage roll"
        );
        assertFalse(contestRoll.isDisadvantageRoll(),
                "grantAdvantage should not yield a disadvantage roll"
        );
        assertFalse(contestRoll.isNormalRoll(),
                "grantAdvantage should not yield a normal roll"
        );
    }

    @Test
    @DisplayName("grantDisadvantage should yield only a disadvantage roll")
    void grantDisadvantage_yieldOnlyDisadvantageRoll() {
        contestRoll.grantDisadvantage();
        assertFalse(contestRoll.isAdvantageRoll(),
                "grantAdvantage should not yield an advantage roll"
        );
        assertTrue(contestRoll.isDisadvantageRoll(),
                "grantAdvantage should yield a disadvantage roll"
        );
        assertFalse(contestRoll.isNormalRoll(),
                "grantAdvantage should not yield a normal roll"
        );
    }

    @Test
    @DisplayName("grantAdvantage and grantDisadvantage should yield only a normal roll")
    void grantAdvantageGrantDisadvantage_yieldOnlyNormalRoll() {
        contestRoll.grantAdvantage();
        contestRoll.grantDisadvantage();
        assertFalse(contestRoll.isAdvantageRoll(),
                "grantAdvantage should not yield an advantage roll"
        );
        assertFalse(contestRoll.isDisadvantageRoll(),
                "grantAdvantage should not yield a disadvantage roll"
        );
        assertTrue(contestRoll.isNormalRoll(),
                "grantAdvantage should yield a normal roll"
        );
    }

    @Test
    @DisplayName("roll default behavior rolls the first value (second roll higher)")
    void roll_defaultBehavior_secondRollHigher() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "default roll behavior should return the first value (5)"
        );
    }

    @Test
    @DisplayName("roll default behavior rolls the first value (second roll lower)")
    void roll_defaultBehavior_secondRollLower() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        contestRoll.roll();
        assertEquals(15, contestRoll.get(),
                "default roll behavior should return the first value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage behavior rolls the higher value (second roll higher)")
    void roll_withAdvantage_secondRollHigher() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        contestRoll.grantAdvantage();
        contestRoll.roll();
        assertEquals(15, contestRoll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage behavior rolls the higher value (second roll lower)")
    void roll_withAdvantage_secondRollLower() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        contestRoll.grantAdvantage();
        contestRoll.roll();
        assertEquals(15, contestRoll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("roll disadvantage behavior rolls the higher value (first roll higher)")
    void roll_withDisadvantage_firstRollHigher() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    @Test
    @DisplayName("roll disadvantage behavior rolls the higher value (first roll lower)")
    void roll_withDisadvantage_firstRollLower() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    @Test
    @DisplayName("roll advantage and disadvantage behavior rolls the determined value (second roll higher)")
    void roll_withAdvantageAndDisadvantage_firstRollHigher() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(15);
                this.addInteger(5);
            }});
        }});
        contestRoll.grantAdvantage();
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(15, contestRoll.get(),
                "advantage and disadvantage roll behavior should return the first value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage and disadvantage behavior rolls the determined value (first roll lower)")
    void roll_withAdvantageAndDisadvantage_firstRollLower() {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        contestRoll.grantAdvantage();
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "advantage and disadvantage roll behavior should return the first value (5)"
        );
    }

    @Test
    @DisplayName("checkForReroll no reroll was requested")
    void checkForReroll_noRerollRequested() throws Exception {
        contestRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(5);
                this.addInteger(15);
            }});
        }});
        contestRoll.roll();
        contestRoll.checkForReroll(new RPGLContext());
        assertEquals(5, contestRoll.get(),
                "original roll should be preserved when no reroll is requested"
        );
    }

    // TODO additional unit tests needed for when re-rolls are requested... requires RPGLEffect functionality

}
