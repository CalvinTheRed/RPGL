package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculateProficiencyBonusTest {

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

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        CalculateProficiencyBonus calculateProficiencyBonus = new CalculateProficiencyBonus();
        calculateProficiencyBonus.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> calculateProficiencyBonus.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("prepare sets base proficiency bonus from source json")
    void prepare_setsBaseProficiencyBonusFromSourceJson() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateProficiencyBonus calculateProficiencyBonus = new CalculateProficiencyBonus();
        calculateProficiencyBonus.setSource(source);
        calculateProficiencyBonus.prepare(context);

        assertEquals(4, calculateProficiencyBonus.getBase(),
                "prepare() should assign the proficiency bonus from the source json to the subevent base value"
        );
    }

}
