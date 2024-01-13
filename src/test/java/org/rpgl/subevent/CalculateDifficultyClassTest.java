package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
 * Testing class for the org.rpgl.subevent.CalculateDifficultyClass class.
 *
 * @author Calvin Withun
 */
public class CalculateDifficultyClassTest {

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
        Subevent subevent = new CalculateDifficultyClass();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("calculates save DC")
    void calculatesSaveDC() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);

        CalculateDifficultyClass calculateDifficultyClass = new CalculateDifficultyClass();
        calculateDifficultyClass.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", "con");
        }});

        calculateDifficultyClass.setSource(source);
        calculateDifficultyClass.prepare(new DummyContext(), List.of());

        assertEquals(8 /*base*/ +5 /*ability*/ +4 /*proficiency*/, calculateDifficultyClass.get(),
                "young red dragon save DC calculated from Constitution should be 17"
        );
    }

}
