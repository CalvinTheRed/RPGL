package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Testing class for the org.rpgl.subevent.Calculation class.
 *
 * @author Calvin Withun
 */
public class CalculationTest {

    private Calculation calculation;

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @BeforeEach
    void beforeEach() {
        // create an anonymous class for Calculation for the purpose of running tests on it
        calculation = new Calculation("calculation") {

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
    @DisplayName("default behavior with no methods called")
    void defaultBehavior() {
        assertNull(calculation.getBase(),
                "base should be null prior to being set to a particular value"
        );
        assertNull(calculation.getSet(),
                "set should be null prior to being set to a particular value"
        );
        assertEquals(0, calculation.getBonus(),
                "bonus should be 0 before any additional bonuses are added"
        );
    }

    @Test
    @DisplayName("addBonus should be able to go negative and should be additive")
    void addBonus_canGoNegativeAndIsAdditive() {
        calculation.addBonus(-5);
        assertEquals(-5, calculation.getBonus(),
                "bonus should be able to no below 0"
        );
        calculation.addBonus(10);
        assertEquals(5, calculation.getBonus(),
                "bonus values should be additive"
        );
    }

    @Test
    @DisplayName("setBase should be the most recent value")
    void setBase_mostRecentValue() {
        calculation.setBase(1);
        assertEquals(1, calculation.getBase(),
                "base should be most recent value (1)"
        );
        calculation.setBase(-5);
        assertEquals(-5, calculation.getBase(),
                "base should be most recent value (-5)"
        );
        calculation.setBase(5);
        assertEquals(5, calculation.getBase(),
                "base should be most recent value (5)"
        );
    }

    @Test
    @DisplayName("setSet should be the most recent value")
    void setSet_mostRecentValue() {
        calculation.setSet(1);
        assertEquals(1, calculation.getSet(),
                "set should be most recent value (1)"
        );
        calculation.setSet(-5);
        assertEquals(-5, calculation.getSet(),
                "set should be most recent value (-5)"
        );
        calculation.setSet(5);
        assertEquals(5, calculation.getSet(),
                "set should be most recent value (5)"
        );
    }

    @Test
    @DisplayName("get returns base + bonus when set is null")
    void get_notSet() {
        calculation.setBase(10);
        calculation.addBonus(5);
        assertEquals(15, calculation.get(),
                "get should return base + bonus (10+5) when set is null"
        );
    }

    @Test
    @DisplayName("get returns set when set is not null")
    void get_isSet() {
        calculation.setBase(10);
        calculation.addBonus(5);
        calculation.setSet(12);
        assertEquals(12, calculation.get(),
                "get should return set (12) when set is not null"
        );
    }

}
