package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AbilityCheck class.
 *
 * @author Calvin Withun
 */
public class AbilityCheckTest {
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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new AbilityCheck();
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
    @DisplayName("gets ability")
    void getsAbility() {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", "str");
        }});

        assertEquals("str", abilityCheck.getAbility(new DummyContext()),
                "getAbility should return the correct ability"
        );
    }

    @Test
    @DisplayName("gets skill")
    void getsSkill() {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("skill", "athletics");
        }});

        assertEquals("athletics", abilityCheck.getSkill(),
                "getSkill should return the correct skill"
        );
    }

    @Test
    @DisplayName("gives half proficiency")
    void givesHalfProficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.setProficiencyBonus(2);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(new DummyContext(), List.of());

        abilityCheck.giveHalfProficiency();

        assertTrue(abilityCheck.hasHalfProficiency(),
                "should have half proficiency"
        );
        assertFalse(abilityCheck.hasProficiency(),
                "should not have proficiency"
        );
        assertFalse(abilityCheck.hasExpertise(),
                "should not have expertise"
        );
    }

    @Test
    @DisplayName("gives proficiency")
    void givesProficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.setProficiencyBonus(2);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(new DummyContext(), List.of());

        abilityCheck.giveHalfProficiency();
        abilityCheck.giveProficiency();

        assertFalse(abilityCheck.hasHalfProficiency(),
                "should not have half proficiency"
        );
        assertTrue(abilityCheck.hasProficiency(),
                "should have proficiency"
        );
        assertFalse(abilityCheck.hasExpertise(),
                "should not have expertise"
        );
    }

    @Test
    @DisplayName("gives expertise")
    void givesExpertise() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.setProficiencyBonus(2);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(new DummyContext(), List.of());

        abilityCheck.giveHalfProficiency();
        abilityCheck.giveProficiency();
        abilityCheck.giveExpertise();

        assertFalse(abilityCheck.hasHalfProficiency(),
                "should not have half proficiency"
        );
        assertFalse(abilityCheck.hasProficiency(),
                "should not have proficiency"
        );
        assertTrue(abilityCheck.hasExpertise(),
                "should have expertise"
        );
    }

    @Test
    @DisplayName("defaults to proficiency bonus of 0")
    void defaultsToProficiencyBonusOfZero() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(new DummyContext(), List.of());

        assertFalse(abilityCheck.hasHalfProficiency(),
                "should not have half proficiency"
        );
        assertFalse(abilityCheck.hasProficiency(),
                "should not have proficiency"
        );
        assertFalse(abilityCheck.hasExpertise(),
                "should have expertise"
        );
    }

    @Test
    @DisplayName("rolls base die")
    void rollsBaseDie() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "str",
                "determined": [ 10 ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "str");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});

        abilityCheck.setSource(object);
        abilityCheck.prepare(new DummyContext(), List.of());
        abilityCheck.setTarget(object);
        abilityCheck.invoke(new DummyContext(), List.of());

        assertEquals(10, abilityCheck.get(),
                "abilityCheck should roll 10 (10+0)"
        );
    }

    @Test
    @DisplayName("adds ability bonus")
    void addsAbilityBonus() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "str",
                "determined": [ 10 ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "str");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});

        abilityCheck.setSource(object);
        abilityCheck.prepare(new DummyContext(), List.of());
        abilityCheck.setTarget(object);
        abilityCheck.invoke(new DummyContext(), List.of());

        assertEquals(10 /*base*/ +5 /*ability bonus*/, abilityCheck.get(),
                "abilityCheck should total 15"
        );
    }

}
