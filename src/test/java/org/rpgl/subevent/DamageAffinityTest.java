package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DamageAffinityTest {

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> damageAffinity.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("getAffinity reports not immune, not resistant, not vulnerable (default behavior)")
    void getAffinity_notImmuneNotResistantNotVulnerable_defaultBehavior() {
        DamageAffinity damageAffinity = new DamageAffinity();

        assertFalse(damageAffinity.isImmune(),
                "damageAffinity should not report immune by default"
        );
        assertFalse(damageAffinity.isResistant(),
                "damageAffinity should not report resistant by default"
        );
        assertFalse(damageAffinity.isVulnerable(),
                "damageAffinity should not report vulnerable by default"
        );
    }

    @Test
    @DisplayName("getAffinity reports not immune, not resistant, not vulnerable (after prepare())")
    void getAffinity_notImmuneNotResistantNotVulnerable_afterPrepare() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.prepare(new RPGLContext());

        assertFalse(damageAffinity.isImmune(),
                "damageAffinity should not report immune after prepare()"
        );
        assertFalse(damageAffinity.isResistant(),
                "damageAffinity should not report resistant after prepare()"
        );
        assertFalse(damageAffinity.isVulnerable(),
                "damageAffinity should not report vulnerable after prepare()"
        );
    }

    @Test
    @DisplayName("getAffinity reports immune (grant immunity)")
    void getAffinity_immune_grantImmunity() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.grantImmunity();

        assertTrue(damageAffinity.isImmune(),
                "damageAffinity should report immune after granting immunity"
        );
    }

    @Test
    @DisplayName("getAffinity reports not immune (grant and revoke immunity)")
    void getAffinity_notImmune_grantImmunityRevokeImmunity() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.grantImmunity();
        damageAffinity.revokeImmunity();

        assertFalse(damageAffinity.isImmune(),
                "damageAffinity should not report immune after granting and revoking immunity"
        );
    }

    @Test
    @DisplayName("getAffinity reports resistant (grant resistance)")
    void getAffinity_resistant_grantResistance() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.grantResistance();

        assertTrue(damageAffinity.isResistant(),
                "damageAffinity should report resistant after granting immunity"
        );
    }

    @Test
    @DisplayName("getAffinity reports not immune (grant and revoke immunity)")
    void getAffinity_notResistant_grantResistanceRevokeResistance() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.grantResistance();
        damageAffinity.revokeResistance();

        assertFalse(damageAffinity.isResistant(),
                "damageAffinity should not report resistant after granting and revoking resistance"
        );
    }

    @Test
    @DisplayName("getAffinity reports vulnerable (grant vulnerability)")
    void getAffinity_vulnerable_grantVulnerability() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.grantVulnerability();

        assertTrue(damageAffinity.isVulnerable(),
                "damageAffinity should report vulnerable after granting vulnerability"
        );
    }

    @Test
    @DisplayName("getAffinity reports not vulnerable (grant and revoke vulnerability)")
    void getAffinity_notVulnerable_grantVulnerabilityRevokeVulnerability() {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.grantVulnerability();
        damageAffinity.revokeVulnerability();

        assertFalse(damageAffinity.isVulnerable(),
                "damageAffinity should not report vulnerable after granting and revoking vulnerability"
        );
    }

}
