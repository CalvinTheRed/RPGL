package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        calculation.joinSubeventData(new JsonObject() {{
            /*{
                "bonuses": [ ]
                "minimum": {
                    "value": Integer.MIN_VALUE
                }
            }*/
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", Integer.MIN_VALUE);
            }});
        }});
    }

    @Test
    @DisplayName("addBonus should be able to go negative and should be additive")
    void addBonus_canGoNegativeAndIsAdditive() {
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", -5);
            this.putJsonArray("dice", new JsonArray());
        }});
        assertEquals(-5, calculation.getBonus(),
                "bonus should be able to no below 0"
        );
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 10);
            this.putJsonArray("dice", new JsonArray());
        }});
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
    @DisplayName("get returns base + bonus when set is null")
    void get_notSet() {
        calculation.setBase(10);
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 5);
            this.putJsonArray("dice", new JsonArray());
        }});
        assertEquals(15, calculation.get(),
                "get should return base + bonus (10+5) when set is null"
        );
    }

}
