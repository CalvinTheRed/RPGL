package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvokeSubeventTest {

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new InvokeSubevent();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("InvokeSubevent works using example effect (motivational speech effect passive and active)")
    void invokeSubeventWorksUsingExampleEffect_motivationalSpeechEffectPassiveAndActive() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        RPGLEffect motivationalSpeechEffectPassive = RPGLFactory.newEffect("demo:motivational_speech_effect_passive");
        motivationalSpeechEffectPassive.setSource(target);
        motivationalSpeechEffectPassive.setTarget(target);
        target.addEffect(motivationalSpeechEffectPassive);

        assertEquals(1, target.getEffects().size(),
                "verify target has 1 effect (motivational speech passive) applied before InvokeSubevent is precipitated"
        );

        source.invokeEvent(
                new RPGLObject[] { target },
                RPGLFactory.newEvent("demo:weapon_attack_mainhand_melee"),
                context
        );

        System.out.println(UUIDTable.getEffect(target.getEffects().getString(0)));

        assertEquals(2, target.getEffects().size(),
                "verify target has a second effect following the invocation of InvokeSubevent"
        );
    }

}
