package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddBonus class.
 *
 * @author Calvin Withun
 */
public class AddBonusTest {

    private Calculation calculation;

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

    @BeforeEach
    void beforeEach() {
        calculation = new Calculation("calculation") {
            @Override
            public Subevent clone() {
                return null;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return null;
            }
        };
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new AddBonus();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to calculation (range)")
    void execute_addsCorrectBonusToCalculation_range() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        AddBonus addBonus = new AddBonus();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_bonus",
                "bonus": [
                    {
                        "bonus_formula": "range",
                        "bonus": 2,
                        "dice": [ ]
                    }
                ]
            }*/
            this.putString("function", "add_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("bonus_formula", "range");
                    this.putInteger("bonus", 2);
                    this.putJsonArray("dice", new JsonArray());
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        addBonus.execute(effect, calculation, functionJson, context);

        assertEquals(2, calculation.getBonus(),
                "bonus of 2 should be applied to the calculation following execution"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to calculation (modifier)")
    void execute_addsCorrectBonusToCalculation_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        AddBonus addBonus = new AddBonus();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_bonus",
                "bonus": [
                    {
                        "bonus_formula": "modifier",
                        "ability": "dex",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putString("function", "add_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("bonus_formula", "modifier");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);
        effect.setName("TEST");

        addBonus.execute(effect, calculation, functionJson, context);

        assertEquals(5, calculation.getBonus(),
                "source's dex modifier should be added as bonus to calculation (+5)"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to calculation (ability)")
    void execute_addsCorrectBonusToCalculation_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        AddBonus addBonus = new AddBonus();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_bonus",
                "bonus": [
                    {
                        "bonus_formula": "ability",
                        "ability": "dex",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putString("function", "add_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("bonus_formula", "ability");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);
        effect.setName("TEST");

        addBonus.execute(effect, calculation, functionJson, context);

        assertEquals(20, calculation.getBonus(),
                "source's dex score should be added as bonus to calculation (20)"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to calculation (proficiency)")
    void execute_addsCorrectBonusToCalculation_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        AddBonus addBonus = new AddBonus();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_bonus",
                "bonus": [
                    ]
                        "bonus_formula": "proficiency",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putString("function", "add_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("bonus_formula", "proficiency");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);
        effect.setName("TEST");

        addBonus.execute(effect, calculation, functionJson, context);

        assertEquals(2, calculation.getBonus(),
                "source's proficiency bonus should be added as bonus to calculation (+2)"
        );
    }

}
