package org.rpgl.subevent;

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
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new AbilityCheck();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("getAbility returns correct ability")
    void getAbility_returnsCorrectAbility() {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", "str");
        }});

        assertEquals("str", abilityCheck.getAbility(new DummyContext()),
                "getAbility should return the correct ability"
        );
    }

    @Test
    @DisplayName("getSkill returns correct skill")
    void getSkill_returnsCorrectSkill() {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("skill", "athletics");
        }});

        assertEquals("athletics", abilityCheck.getSkill(),
                "getSkill should return the correct skill"
        );
    }

    @Test
    @DisplayName("giveHalfProficiency gives only half proficiency")
    void giveHalfProficiency_givesOnlyHalfProficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(context);

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
        assertEquals(1, abilityCheck.getProficiencyBonus(),
                "proficiency bonus should be 1 for ability check"
        );
    }

    @Test
    @DisplayName("giveProficiency gives proficiency and overrides half proficiency")
    void giveProficiency_givesProficiencyAndOverridesHalfProficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(context);

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
        assertEquals(2, abilityCheck.getProficiencyBonus(),
                "proficiency bonus should be 2 for ability check"
        );
    }

    @Test
    @DisplayName("giveProficiency gives expertise and overrides half proficiency and proficiency")
    void giveExpertise_givesExpertiseAndOverridesHalfProficiencyAndProficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(context);

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
        assertEquals(4, abilityCheck.getProficiencyBonus(),
                "proficiency bonus should be 4 for ability check"
        );
    }

    @Test
    @DisplayName("getProficiencyBonus returns zero when no proficiency is given")
    void getProficiencyBonus_returnsZeroWhenNoProficiencyIsGiven() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(context);

        assertFalse(abilityCheck.hasHalfProficiency(),
                "should not have half proficiency"
        );
        assertFalse(abilityCheck.hasProficiency(),
                "should not have proficiency"
        );
        assertFalse(abilityCheck.hasExpertise(),
                "should have expertise"
        );
        assertEquals(0, abilityCheck.getProficiencyBonus(),
                "proficiency bonus should be 0 for ability check"
        );
    }

    @Test
    @DisplayName("invoke rolls die correctly")
    void invoke_rollsDieCorrectly() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

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
        abilityCheck.prepare(context);
        abilityCheck.setTarget(object);
        abilityCheck.invoke(context);

        assertEquals(10, abilityCheck.get(),
                "abilityCheck should roll 10 (10+0)"
        );
    }

    @Test
    @DisplayName("invoke adds ability bonus")
    void invoke_addsAbilityBonus() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

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
        abilityCheck.prepare(context);
        abilityCheck.setTarget(object);
        abilityCheck.invoke(context);

        assertEquals(10+5, abilityCheck.get(),
                "abilityCheck should roll 15 (10+5)"
        );
    }

    @Test
    @DisplayName("invoke adds proficiency (is proficient)")
    void invoke_addsProficiency_isProficient() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        RPGLEffect athleticsProficiency = RPGLFactory.newEffect("std:common/proficiency/skill/athletics");
        athleticsProficiency.setSource(object);
        athleticsProficiency.setTarget(object);
        object.addEffect(athleticsProficiency);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "str",
                "skill": "athletics",
                "determined": [ 10 ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "str");
            this.putString("skill", "athletics");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});

        abilityCheck.setSource(object);
        abilityCheck.prepare(context);
        abilityCheck.setTarget(object);
        abilityCheck.invoke(context);

        assertEquals(10+2, abilityCheck.get(),
                "abilityCheck should roll 12 (10+2)"
        );
    }

}
