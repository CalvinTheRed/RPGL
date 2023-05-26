package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.DamageAffinity class.
 *
 * @author Calvin Withun
 */
public class DamageAffinityTest {

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new DamageAffinity();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("getAffinity reports not immune, not resistant, not vulnerable (default behavior)")
    void getAffinity_notImmuneNotResistantNotVulnerable_defaultBehavior() {
        DamageAffinity damageAffinity = new DamageAffinity();
        String damageType = "fire";
        damageAffinity.addDamageType(damageType);

        assertFalse(damageAffinity.isImmune(damageType),
                "damageAffinity should not report immune by default"
        );
        assertFalse(damageAffinity.isResistant(damageType),
                "damageAffinity should not report resistant by default"
        );
        assertFalse(damageAffinity.isVulnerable(damageType),
                "damageAffinity should not report vulnerable by default"
        );
    }

}
