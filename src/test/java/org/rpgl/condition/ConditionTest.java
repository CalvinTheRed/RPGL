package org.rpgl.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.Condition class.
 *
 * @author Calvin Withun
 */
public class ConditionTest {

    @Test
    @DisplayName("compares values")
    void comparesValues() throws Exception {
        assertTrue(Condition.compareValues(5, 10, "<"),
                "5 is less than 10"
        );
        assertFalse(Condition.compareValues(10, 5, "<"),
                "10 is not less than 5"
        );
        assertTrue(Condition.compareValues(5, 10, "<="),
                "10 is less than or equal to 5"
        );
        assertTrue(Condition.compareValues(5, 5, "<="),
                "5 is less than or equal to 5"
        );
        assertFalse(Condition.compareValues(10, 5, "<="),
                "10 is not less than or equal to 5"
        );
        assertTrue(Condition.compareValues(5, 5, "="),
                "5 is equal to 5"
        );
        assertFalse(Condition.compareValues(10, 5, "="),
                "5 is not equal to 5"
        );
        assertFalse(Condition.compareValues(5, 10, ">="),
                "5 is not greater than or equal to 10"
        );
        assertTrue(Condition.compareValues(5, 5, ">="),
                "5 is greater than or equal to 5"
        );
        assertTrue(Condition.compareValues(10, 5, ">="),
                "10 is greater than or equal to 5"
        );
        assertFalse(Condition.compareValues(5, 10, ">"),
                "5 is not greater than 10"
        );
        assertTrue(Condition.compareValues(10, 5, ">"),
                "10 is greater than 5"
        );
    }
}
