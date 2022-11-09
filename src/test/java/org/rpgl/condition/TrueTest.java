package org.rpgl.condition;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.ConditionMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class TrueTest {

    @Test
    @DisplayName("True Condition throws ConditionMismatchException when condition type doesn't match")
    void test0() throws JsonFormatException {
        Condition condition = new True();
        String conditionJsonString = """
                {
                    "condition": "not_a_condition"
                }
                """;
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson),
                "True Condition should throw a ConditionMismatchException if the specified condition doesn't match."
        );
    }

    @Test
    @DisplayName("True Condition should always evaluate true")
    void test1() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new True();
        String conditionJsonString = """
                {
                    "condition": "true"
                }
                """;
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertTrue(result,
                "True Condition should always evaluate to true."
        );
    }

}
