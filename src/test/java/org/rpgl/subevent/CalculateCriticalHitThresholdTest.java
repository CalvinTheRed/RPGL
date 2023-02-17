package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.CalculateCriticalHitThreshold class.
 *
 * @author Calvin Withun
 */
public class CalculateCriticalHitThresholdTest {

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold();
        calculateCriticalHitThreshold.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> calculateCriticalHitThreshold.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("prepare sets the base of the calculation to 20")
    void prepare_setsBaseToTwenty() throws Exception {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold();
        calculateCriticalHitThreshold.prepare(new RPGLContext());

        assertEquals(20, calculateCriticalHitThreshold.get(),
                "calculateCriticalHitThreshold should have a base of 20 after prepare()"
        );
    }

}
