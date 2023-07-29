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
 * Testing class for the org.rpgl.function.SetBase class.
 *
 * @author Calvin Withun
 */
public class SetBaseTest {

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
        Function function = new SetBase();
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
    @DisplayName("execute sets calculation base to new value (number)")
    void execute_setsCalculationBaseToNewValue_number() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        SetBase setBase = new SetBase();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_base",
                "base": {
                    "base_formula": "number",
                    "value": 13
                }
            }*/
            this.putString("function", "set_base");
            this.putJsonObject("base", new JsonObject() {{
                this.putString("base_formula", "number");
                this.putInteger("value", 13);
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        setBase.execute(effect, calculation, functionJson, context);

        assertEquals(13, calculation.getBase().getInteger("value"),
                "execute should set calculation base to 13"
        );
    }

    @Test
    @DisplayName("execute sets calculation base to new value (modifier)")
    void execute_setsCalculationBaseToNewValue_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        SetBase setBase = new SetBase();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_base",
                "base": {
                    "base_formula": "modifier",
                    "ability": "dex",
                    "object": {
                        "from": "effect",
                        "object": "source"
                    }
                }
            }*/
            this.putString("function", "set_base");
            this.putJsonObject("base", new JsonObject() {{
                this.putString("base_formula", "modifier");
                this.putString("ability", "dex");
                this.putJsonObject("object", new JsonObject() {{
                    this.putString("from", "effect");
                    this.putString("object", "source");
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);
        effect.setName("TEST");

        setBase.execute(effect, calculation, functionJson, context);

        assertEquals(5, calculation.getBase().getInteger("value"),
                "execute should set calculation base to source's dex modifier (+5)"
        );
    }

    @Test
    @DisplayName("execute sets calculation base to new value (ability)")
    void execute_setsCalculationBaseToNewValue_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        SetBase setBase = new SetBase();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_base",
                "base": {
                    "base_formula": "ability",
                    "ability": "dex",
                    "object": {
                        "from": "effect",
                        "object": "source"
                    }
                }
            }*/
            this.putString("function", "set_base");
            this.putJsonObject("base", new JsonObject() {{
                this.putString("base_formula", "ability");
                this.putString("ability", "dex");
                this.putJsonObject("object", new JsonObject() {{
                    this.putString("from", "effect");
                    this.putString("object", "source");
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);
        effect.setName("TEST");

        setBase.execute(effect, calculation, functionJson, context);

        assertEquals(20, calculation.getBase().getInteger("value"),
                "execute should set calculation base to source's dex score (20)"
        );
    }

    @Test
    @DisplayName("execute sets calculation base to new value (proficiency)")
    void execute_setsCalculationBaseToNewValue_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context);
        calculation.setTarget(target);

        SetBase setBase = new SetBase();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_base",
                "base": {
                    "base_formula": "proficiency",
                    "object": {
                        "from": "effect",
                        "object": "source"
                    }
                }
            }*/
            this.putString("function", "set_base");
            this.putJsonObject("base", new JsonObject() {{
                this.putString("base_formula", "proficiency");
                this.putJsonObject("object", new JsonObject() {{
                    this.putString("from", "effect");
                    this.putString("object", "source");
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);
        effect.setName("TEST");

        setBase.execute(effect, calculation, functionJson, context);

        assertEquals(2, calculation.getBase().getInteger("value"),
                "execute should set calculation base to source's proficiency bonus (+2)"
        );
    }

}
