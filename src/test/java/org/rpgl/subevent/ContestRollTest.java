package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContestRollTest {

    private ContestRoll contestRoll;

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @BeforeEach
    void beforeEach() {
        // create an anonymous class for Calculation for the purpose of running tests on it
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
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 10);
            this.putInteger("determined_second", 15);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.roll();
        assertEquals(10, contestRoll.get(),
                "default roll behavior should return the first value (10)"
        );
    }

    @Test
    @DisplayName("roll default behavior rolls the first value (second roll lower)")
    void roll_defaultBehavior_secondRollLower() {
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 10);
            this.putInteger("determined_second", 5);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.roll();
        assertEquals(10, contestRoll.get(),
                "default roll behavior should return the first value (10)"
        );
    }

    @Test
    @DisplayName("roll advantage behavior rolls the higher value (second roll higher)")
    void roll_withAdvantage_secondRollHigher() {
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 5);
            this.putInteger("determined_second", 15);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.grantAdvantage();
        contestRoll.roll();
        assertEquals(15, contestRoll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("roll advantage behavior rolls the higher value (second roll lower)")
    void roll_withAdvantage_secondRollLower() {
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 15);
            this.putInteger("determined_second", 5);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.grantAdvantage();
        contestRoll.roll();
        assertEquals(15, contestRoll.get(),
                "advantage roll behavior should return the higher value (15)"
        );
    }

    @Test
    @DisplayName("roll disadvantage behavior rolls the higher value (first roll higher)")
    void roll_withDisadvantage_firstRollHigher() {
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 15);
            this.putInteger("determined_second", 5);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    @Test
    @DisplayName("roll disadvantage behavior rolls the higher value (first roll lower)")
    void roll_withDisadvantage_firstRollLower() {
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 5);
            this.putInteger("determined_second", 15);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "disadvantage roll behavior should return the lower value (5)"
        );
    }

    @Test
    @DisplayName("roll advantage and disadvantage behavior rolls the determined value (second roll higher)")
    void roll_withAdvantageAndDisadvantage_firstRollHigher() {
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 15);
            this.putInteger("determined_second", 5);
        }};
        contestRoll.joinSubeventData(subeventJson);
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
        JsonObject subeventJson = new JsonObject() {{
            this.putString("subevent", "contest_roll");
            this.putInteger("determined", 5);
            this.putInteger("determined_second", 15);
        }};
        contestRoll.joinSubeventData(subeventJson);
        contestRoll.grantAdvantage();
        contestRoll.grantDisadvantage();
        contestRoll.roll();
        assertEquals(5, contestRoll.get(),
                "advantage and disadvantage roll behavior should return the first value (5)"
        );
    }

    // TODO make a CheckForReroll test

}
