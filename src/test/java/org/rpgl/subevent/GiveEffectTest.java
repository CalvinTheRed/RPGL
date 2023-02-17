package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for class GiveEffect.
 *
 * @author Calvin Withun
 */
public class GiveEffectTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        GiveEffect giveEffect = new GiveEffect();
        giveEffect.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> giveEffect.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("isCancelled returns false (by default)")
    void isCancelled_returnsFalse_default() {
        GiveEffect giveEffect = new GiveEffect();

        assertFalse(giveEffect.isCancelled(),
                "giveEffect should not be cancelled by default"
        );
    }

    @Test
    @DisplayName("isCancelled returns true (when cancelled)")
    void isCancelled_returnsTrue_cancelled() {
        GiveEffect giveEffect = new GiveEffect();
        giveEffect.cancel();

        assertTrue(giveEffect.isCancelled(),
                "giveEffect should not be cancelled by default"
        );
    }

    @Test
    @DisplayName("invoke gives effect (not cancelled)")
    void invoke_givesEffect_notCancelled() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object);

        GiveEffect giveEffect = new GiveEffect();
        giveEffect.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "give_effect");
            this.putString("effect", "demo:fire_immunity");
        }});
        giveEffect.setSource(object);
        giveEffect.prepare(context);
        giveEffect.setTarget(object);
        giveEffect.invoke(context);

        List<RPGLEffect> effects = object.getEffectObjects();
        assertEquals(1, effects.size(),
                "commoner should have 1 effect after the subevent is invoked"
        );
        assertEquals("demo:fire_immunity", effects.get(0).getString(DatapackContentTO.ID_ALIAS),
                "the commoner's subevent should match the effect specified in the subevent json"
        );
    }

    @Test
    @DisplayName("invoke does not give effect (cancelled)")
    void invoke_doesNotGiveEffect_cancelled() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object);

        GiveEffect giveEffect = new GiveEffect();
        giveEffect.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "give_effect");
            this.putString("effect", "demo:fire_immunity");
        }});
        giveEffect.setSource(object);
        giveEffect.prepare(context);
        giveEffect.setTarget(object);
        giveEffect.cancel();
        giveEffect.invoke(context);

        List<RPGLEffect> effects = object.getEffectObjects();
        assertEquals(0, effects.size(),
                "commoner should have 0 effects after the subevent is invoked"
        );
    }

    // TODO still need test for not giving an effect already applied to the target

}
