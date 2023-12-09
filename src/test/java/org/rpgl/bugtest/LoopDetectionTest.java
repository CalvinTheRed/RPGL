package org.rpgl.bugtest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class is dedicated to testing RPGL's behavior when it encounters an infinite recursive loop while attempting to
 * evaluate Conditions. Such loops should be safely exited, and the outermost participating Condition should evaluate to
 * false.
 *
 * @author Calvin Withun
 */
public class LoopDetectionTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
        );
        RPGLCore.initializeTesting();
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("Detect loop from single self-calling condition")
    void test_singleton() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_singleton"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(0, dummy.getAbilityModifierFromAbilityName("str", context),
                "Str mod should not change as a result of hitting a condition loop"
        );
    }

    @Test
    @DisplayName("Detect loop from condition chain")
    void test_chain() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_1"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_2"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_3"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(0, dummy.getAbilityModifierFromAbilityName("str", context),
                "Str mod should not change as a result of hitting a condition loop"
        );
    }

    @Test
    @DisplayName("Detect loop from condition chain with independent first condition")
    void test_chain_independentFirstCondition() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_0"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_1"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_2"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_3"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(0, dummy.getAbilityModifierFromAbilityName("cha", context),
                "Cha mod should not change as a result of hitting a condition loop"
        );
    }

    @Test
    @DisplayName("Detect loop from condition chain with inverted independent first condition")
    void test_chain_independentFirstCondition_inverted() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_0_inverted"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_1"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_2"));
        dummy.addEffect(RPGLFactory.newEffect("debug:loop_detection_chain_3"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(4, dummy.getAbilityModifierFromAbilityName("cha", context),
                "Cha mod should change as a result of hitting a condition loop due to independent inversion"
        );
    }

    // note: no test exists for false positives concerning identifying a loop. community testing would be appreciated,
    // as it is not clear if such false positives are possible in the current state of the code.

}
