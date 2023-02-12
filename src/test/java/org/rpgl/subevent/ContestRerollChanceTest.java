package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for class ContestRerollChance.
 *
 * @author Calvin Withun
 */
public class ContestRerollChanceTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        ContestRerollChance contestRerollChance = new ContestRerollChance();
        contestRerollChance.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> contestRerollChance.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("prepare no reroll is requested by default")
    void default_noRerollRequested() {
        ContestRerollChance contestRerollChance = new ContestRerollChance();

        assertFalse(contestRerollChance.wasRerollRequested(),
                "a reroll should not be requested by default preceding prepare() call"
        );
    }

    @Test
    @DisplayName("prepare no reroll is requested by default")
    void prepare_noRerollRequestedByDefault() {
        ContestRerollChance contestRerollChance = new ContestRerollChance();
        contestRerollChance.prepare(new RPGLContext());

        assertFalse(contestRerollChance.wasRerollRequested(),
                "a reroll should not be requested by default following prepare() call"
        );
    }

    @Test
    @DisplayName("requestReroll sets reroll request to true and stores reroll mode")
    void requestReroll_setsRerollRequestedToTrueAndStoresRerollMode() {
        ContestRerollChance contestRerollChance = new ContestRerollChance();
        contestRerollChance.requestReroll(ContestRerollChance.USE_HIGHEST);

        assertTrue(contestRerollChance.wasRerollRequested(),
                "a reroll should be requested following requestReroll() call"
        );
        assertEquals(ContestRerollChance.USE_HIGHEST, contestRerollChance.getRerollMode(),
                "reroll mode should reflect the passed value"
        );
    }
}
