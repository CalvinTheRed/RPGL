package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.GetProficiency class.
 *
 * @author Calvin Withun
 */
public class GetProficiencyTest {

    private GetProficiency getProficiency;

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @BeforeEach
    void beforeEach() {
        // create an anonymous class for GetProficiency for the purpose of running tests on it
        getProficiency = new GetProficiency("get_proficiency") {

            @Override
            public Subevent clone() {
                return this;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return this;
            }

        };

    }

    @Test
    @DisplayName("comprehensive unit test")
    void comprehensiveUnitTest() {
        assertFalse(getProficiency.isProficient(),
                "GetProficiency should default to not granting proficiency"
        );
        getProficiency.grantProficiency();
        assertTrue(getProficiency.isProficient(),
                "GrantProficiency should report proficiency has been granted after it was granted"
        );
    }

}
