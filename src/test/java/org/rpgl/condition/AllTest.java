package org.rpgl.condition;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.ConditionMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class AllTest {

    @Test
    @DisplayName("All Condition throws ConditionMismatchException when condition type doesn't match")
    void test0() throws JsonFormatException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"not_a_condition\"" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson),
                "All Condition should throw a ConditionMismatchException if the specified condition doesn't match."
        );
    }

    @Test
    @DisplayName("All Condition is true with no nested conditions")
    void test1() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"all\"," +
                "\"conditions\": [ ]" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertTrue(result,
                "All Condition should default to evaluating true when no nested Conditions are supplied."
        );
    }

    @Test
    @DisplayName("All Condition is true with single nested true")
    void test2() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"all\"," +
                "\"conditions\": [" +
                "   { \"condition\": \"true\" }" +
                "]" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertTrue(result,
                "All Condition should evaluate true when the only nested Condition evaluates to true."
        );
    }

    @Test
    @DisplayName("All Condition is false with single nested false")
    void test3() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"all\"," +
                "\"conditions\": [" +
                "   { \"condition\": \"false\" }" +
                "]" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertFalse(result,
                "All Condition should evaluate false when the only nested Condition evaluates to false."
        );
    }

    @Test
    @DisplayName("All Condition is true with multiple nested true")
    void test4() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"all\"," +
                "\"conditions\": [" +
                "   { \"condition\": \"true\" }," +
                "   { \"condition\": \"true\" }" +
                "]" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertTrue(result,
                "All Condition should evaluate true when all nested Conditions evaluate to true."
        );
    }

    @Test
    @DisplayName("All Condition is false with nested true and false")
    void test5() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"all\"," +
                "\"conditions\": [" +
                "   { \"condition\": \"true\" }," +
                "   { \"condition\": \"false\" }" +
                "]" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertFalse(result,
                "All Condition should evaluate false when only some nested Conditions evaluates to true."
        );
    }

    @Test
    @DisplayName("All Condition is false with multiple nested false")
    void test6() throws JsonFormatException, ConditionMismatchException {
        Condition condition = new All();
        String conditionJsonString = "{" +
                "\"condition\": \"all\"," +
                "\"conditions\": [" +
                "   { \"condition\": \"false\" }," +
                "   { \"condition\": \"false\" }" +
                "]" +
                "}";
        JsonObject conditionJson = JsonParser.parseObjectString(conditionJsonString);
        boolean result = condition.evaluate(null, null, conditionJson);
        assertFalse(result,
                "All Condition should evaluate false when all nested Conditions evaluate to false."
        );
    }

}
