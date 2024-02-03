package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.GiveEffect class.
 *
 * @author Calvin Withun
 */
public class GiveEffectTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
        );
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new GiveEffect();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("gives effect")
    void givesEffect() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GiveEffect giveEffect = new GiveEffect();
        giveEffect.joinSubeventData(new JsonObject() {{
            this.putString("effect", "std:common/damage/immunity/fire");
        }});
        giveEffect.setSource(source);
        giveEffect.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        giveEffect.setTarget(target);
        giveEffect.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        List<RPGLEffect> effects = target.getEffectObjects();
        assertEquals(1, effects.size(),
                "commoner should have 1 effect after the subevent is invoked"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(0).getId(),
                "the commoner's subevent should match the effect specified in the subevent json"
        );
    }

}
