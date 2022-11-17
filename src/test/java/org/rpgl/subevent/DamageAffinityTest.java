package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for subevent.DamageAffinity class.
 *
 * @author Calvin Withun
 */
public class DamageAffinityTest {

    @Test
    @DisplayName("DamageAffinity Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "not_a_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "DamageAffinity Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent defaults to normal damage")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should default to normal damage."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent can grant immunity")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantImmunity();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when immunity is granted."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent can revoke granted immunity")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantImmunity();
        damageAffinity.revokeImmunity();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when immunity is granted and revoked."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent can grant resistance")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantResistance();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("resistance", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report resistance when resistance is granted."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent can revoke granted resistance")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantResistance();
        damageAffinity.revokeResistance();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when resistance is granted and revoked."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent can grant vulnerability")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("vulnerability", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report vulnerability when vulnerability is granted."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent can revoke granted vulnerability")
    void test7() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantVulnerability();
        damageAffinity.revokeVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when vulnerability is granted and revoked."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: immunity and resistance")
    void test8() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantImmunity();
        damageAffinity.grantResistance();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when it has immunity and resistance."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: immunity and vulnerability")
    void test9() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantImmunity();
        damageAffinity.grantVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when it has immunity and vulnerability."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: resistance and vulnerability")
    void test10() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantResistance();
        damageAffinity.grantVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when it has resistance and vulnerability."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: immunity and resistance and vulnerability")
    void test11() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = """
                {
                    "subevent": "damage_affinity",
                    "type": "fire"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.grantImmunity();
        damageAffinity.grantResistance();
        damageAffinity.grantVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when it has immunity and resistance and vulnerability."
        );
    }

}
