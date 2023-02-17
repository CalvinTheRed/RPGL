package org.rpgl.subevent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.BaseDamageRoll class.
 *
 * @author Calvin Withun
 */
public class BaseDamageRollTest {

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        BaseDamageRoll baseDamageRoll = new BaseDamageRoll();
        baseDamageRoll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> baseDamageRoll.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

}
