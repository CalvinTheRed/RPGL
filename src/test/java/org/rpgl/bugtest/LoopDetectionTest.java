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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopDetectionTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
        RPGLObject dummy = RPGLFactory.newObject("bugtest:dummy");
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_singleton"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(0, dummy.getAbilityModifierFromAbilityName("str", context),
                "Str mod should not change as a result of hitting a condition loop"
        );
    }

    @Test
    @DisplayName("Detect loop from condition chain")
    void test_chain() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("bugtest:dummy");
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_1"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_2"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_3"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(0, dummy.getAbilityModifierFromAbilityName("str", context),
                "Str mod should not change as a result of hitting a condition loop"
        );
    }

    @Test
    @DisplayName("Detect loop from condition chain with independent first condition")
    void test_chain_independentFirstCondition() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("bugtest:dummy");
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_0"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_1"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_2"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_3"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(0, dummy.getAbilityModifierFromAbilityName("cha", context),
                "Cha mod should not change as a result of hitting a condition loop"
        );
    }

    @Test
    @DisplayName("Detect loop from condition chain with inverted independent first condition")
    void test_chain_independentFirstCondition_inverted() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("bugtest:dummy");
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_0_inverted"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_1"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_2"));
        dummy.addEffect(RPGLFactory.newEffect("bugtest:loop_detection_chain_3"));

        DummyContext context = new DummyContext();
        context.add(dummy);

        assertEquals(4, dummy.getAbilityModifierFromAbilityName("cha", context),
                "Cha mod should change as a result of hitting a condition loop due to independent inversion"
        );
    }

    // note: no test exists for false positives concerning identifying a loop. community testing would be appreciated,
    // as it is not clear if such false positives are possible in the current state of the code.

}
