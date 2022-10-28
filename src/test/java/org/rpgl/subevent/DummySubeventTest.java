package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DummySubeventTest {

    @AfterEach
    void afterEach() {
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("DummySubevent Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        Subevent subevent = new DummySubevent();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "DummySubevent Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

}
