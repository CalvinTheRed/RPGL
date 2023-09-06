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
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.CalculateProficiencyBonus class.
 *
 * @author Calvin Withun
 */
public class CalculateProficiencyBonusTest {

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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new CalculateProficiencyBonus();
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
    @DisplayName("prepare sets base proficiency bonus from source json")
    void prepare_setsBaseProficiencyBonusFromSourceJson() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young");
        DummyContext context = new DummyContext();
        context.add(source);

        CalculateProficiencyBonus calculateProficiencyBonus = new CalculateProficiencyBonus();

        calculateProficiencyBonus.setSource(source);
        calculateProficiencyBonus.prepare(context, List.of());

        assertEquals(4, calculateProficiencyBonus.getBase(),
                "prepare() should assign the proficiency bonus from the source json to the subevent base value"
        );
    }

}
