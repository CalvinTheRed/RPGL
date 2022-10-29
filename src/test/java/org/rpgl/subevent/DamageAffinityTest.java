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

public class DamageAffinityTest {

    @Test
    @DisplayName("DamageAffinity Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
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
    @DisplayName("DamageAffinity Subevent reports immunity correctly")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
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
        damageAffinity.giveImmunity();
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when not revoked."
        );
        damageAffinity.revokeImmunity();
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when revoked."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports resistance correctly")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
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
        damageAffinity.giveResistance();
        assertEquals("resistance", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report resistance when not revoked."
        );
        damageAffinity.revokeResistance();
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when revoked."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports vulnerability correctly")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
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
        damageAffinity.giveVulnerability();
        assertEquals("vulnerability", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report vulnerability when not revoked."
        );
        damageAffinity.revokeVulnerability();
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when revoked."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: immunity and resistance")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.giveImmunity();
        damageAffinity.giveResistance();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when it has immunity and resistance."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: immunity and vulnerability")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.giveImmunity();
        damageAffinity.giveVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when it has immunity and vulnerability."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: resistance and vulnerability")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.giveResistance();
        damageAffinity.giveVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("normal", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report normal when it has resistance and vulnerability."
        );
    }

    @Test
    @DisplayName("DamageAffinity Subevent reports affinity correctly: immunity and resistance and vulnerability")
    void test7() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DamageAffinity();
        String subeventJsonString = "{" +
                "\"subevent\": \"damage_affinity\"," +
                "\"type\": \"fire\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DamageAffinity damageAffinity = (DamageAffinity) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent method
         */
        damageAffinity.prepare(context);
        damageAffinity.giveImmunity();
        damageAffinity.giveResistance();
        damageAffinity.giveVulnerability();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals("immunity", damageAffinity.getAffinity(),
                "DamageAffinity Subevent should report immunity when it has immunity and resistance and vulnerability."
        );
    }

}
