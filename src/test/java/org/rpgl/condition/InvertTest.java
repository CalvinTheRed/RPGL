package org.rpgl.condition;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.ConditionMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class InvertTest {

    @Test
    @DisplayName("Invert Condition throws ConditionMismatchException when condition type doesn't match")
    void test0() throws JsonFormatException {
        Condition condition = new Invert();
        String conditionJsonString = """
                {
                    "condition": "not_a_condition"
                }
                """;
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson),
                "False Condition should throw a ConditionMismatchException if the specified condition doesn't match."
        );
    }

    @Test
    @DisplayName("Invert Condition should evaluate true when nested condition evaluates to false")
    void test1() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new Invert();
        String conditionJsonString = """
                {
                    "condition": "invert",
                    "invert": {
                        "condition": "false"
                    }
                }
                """;
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertTrue(result,
                "Invert Condition should evaluate to true when the nested condition evaluates to false."
        );
    }

    @Test
    @DisplayName("Invert Condition should evaluate false when nested condition evaluates to true")
    void test2() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new Invert();
        String conditionJsonString = """
                {
                    "condition": "invert",
                    "invert": {
                        "condition": "true"
                    }
                }
                """;
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertFalse(result,
                "Invert Condition should evaluate to false when the nested condition evaluates to true."
        );
    }

}
