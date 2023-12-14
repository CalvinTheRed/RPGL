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
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.SetMinimum class.
 *
 * @author Calvin Withun
 */
public class SetMinimumTest {

    private Calculation calculation;

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
        Function function = new SetMinimum();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context, List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute sets calculation minimum to new value (number)")
    void execute_setsCalculationMinimumToNewValue_number() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context, List.of());
        calculation.setTarget(target);

        SetMinimum setMinimum = new SetMinimum();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 13
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 13);
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(13, calculation.getMinimum(),
                "execute should set calculation minimum to 13"
        );
    }

    @Test
    @DisplayName("execute sets calculation minimum to new value (modifier)")
    void execute_setsCalculationMinimumToNewValue_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        calculation.setSource(source);
        calculation.prepare(context, List.of());
        calculation.setTarget(target);

        SetMinimum setMinimum = new SetMinimum();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "modifier",
                    "ability": "dex",
                    "object": {
                        "from": "effect",
                        "object": "source"
                    }
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "modifier");
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

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(5, calculation.getMinimum(),
                "execute should set calculation minimum to source's dex modifier (+5)"
        );
    }

    @Test
    @DisplayName("execute sets calculation minimum to new value (ability)")
    void execute_setsCalculationMinimumToNewValue_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        calculation.setSource(source);
        calculation.prepare(context, List.of());
        calculation.setTarget(target);

        SetMinimum setMinimum = new SetMinimum();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "ability",
                    "ability": "dex",
                    "object": {
                        "from": "effect",
                        "object": "source"
                    }
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "ability");
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

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(20, calculation.getMinimum(),
                "execute should set calculation minimum to source's dex score (20)"
        );
    }

    @Test
    @DisplayName("execute sets calculation minimum to new value (proficiency)")
    void execute_setsCalculationMinimumToNewValue_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context, List.of());
        calculation.setTarget(target);

        SetMinimum setMinimum = new SetMinimum();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "proficiency",
                    "object": {
                        "from": "effect",
                        "object": "source"
                    }
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "proficiency");
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

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(2, calculation.getMinimum(),
                "execute should set calculation minimum to source's proficiency bonus (+2)"
        );
    }

    @Test
    @DisplayName("execute sets calculation minimum to new value only if new minimum is larger")
    void execute_setsCalculationMinimumToNewValueOnlyIfNewMinimumIsLarger() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        calculation.setSource(source);
        calculation.prepare(context, List.of());
        calculation.setTarget(target);

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        SetMinimum setMinimum = new SetMinimum();
        JsonObject functionJson;

        // first set minimum to 10

        functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 10
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 10);
            }});
        }};

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(10, calculation.getMinimum(),
                "execute should set calculation minimum to 10"
        );

        // second set the minimum to 15

        functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 15
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 15);
            }});
        }};

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(15, calculation.getMinimum(),
                "execute should set calculation minimum to 15 (15 > 10)"
        );

        // third set the minimum to 5 (should not work)

        functionJson = new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 5
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 5);
            }});
        }};

        setMinimum.execute(effect, calculation, functionJson, context, List.of());

        assertEquals(15, calculation.getMinimum(),
                "execute should not change calculation minimum (5 < 15>)"
        );
    }

}
