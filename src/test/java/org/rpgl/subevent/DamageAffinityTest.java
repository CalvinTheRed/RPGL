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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
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
    @DisplayName("indicates normal damage")
    void indicatesNormalDamage() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("fire");

        assertFalse(damageAffinity.isImmune("fire"),
                "damageAffinity should not report immune by default"
        );
        assertFalse(damageAffinity.isResistant("fire"),
                "damageAffinity should not report resistant by default"
        );
        assertFalse(damageAffinity.isVulnerable("fire"),
                "damageAffinity should not report vulnerable by default"
        );
    }

    @Test
    @DisplayName("indicates and revokes resistance")
    void indicatesAndRevokesResistance() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("fire");
        damageAffinity.grantResistance("fire");

        assertFalse(damageAffinity.isImmune("fire"),
                "damageAffinity should not report immune to fire"
        );
        assertTrue(damageAffinity.isResistant("fire"),
                "damageAffinity should report resistant to fire"
        );
        assertFalse(damageAffinity.isVulnerable("fire"),
                "damageAffinity should not report vulnerable to fire"
        );

        damageAffinity.revokeResistance("fire");
        assertFalse(damageAffinity.isResistant("fire"),
                "damageAffinity should not report resistant to fire"
        );
    }

    @Test
    @DisplayName("indicates and revokes immunity")
    void indicatesAndRevokesImmunity() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("fire");
        damageAffinity.grantImmunity("fire");

        assertTrue(damageAffinity.isImmune("fire"),
                "damageAffinity should report immune to fire"
        );
        assertFalse(damageAffinity.isResistant("fire"),
                "damageAffinity should not report resistant to fire"
        );
        assertFalse(damageAffinity.isVulnerable("fire"),
                "damageAffinity should not report vulnerable to fire"
        );

        damageAffinity.revokeImmunity("fire");
        assertFalse(damageAffinity.isImmune("fire"),
                "damageAffinity should not report immune to fire"
        );
    }

    @Test
    @DisplayName("indicates and revokes vulnerability")
    void indicatesAndRevokesVulnerability() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("fire");
        damageAffinity.grantVulnerability("fire");

        assertFalse(damageAffinity.isImmune("fire"),
                "damageAffinity should not report immune to fire"
        );
        assertFalse(damageAffinity.isResistant("fire"),
                "damageAffinity should not report resistant to fire"
        );
        assertTrue(damageAffinity.isVulnerable("fire"),
                "damageAffinity should report vulnerable to fire"
        );

        damageAffinity.revokeVulnerability("fire");
        assertFalse(damageAffinity.isVulnerable("fire"),
                "damageAffinity should not report vulnerable to fire"
        );
    }

    @Test
    @DisplayName("recognizes present damage type")
    void recognizesPresentDamageType() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("fire");

        assertTrue(damageAffinity.includesDamageType("fire"),
                "should return true when damage type is included"
        );
    }

    @Test
    @DisplayName("recognizes absent damage type")
    void recognizesAbsentDamageType() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("cold");

        assertFalse(damageAffinity.includesDamageType("fire"),
                "should return false when damage type is not included"
        );
    }

}
