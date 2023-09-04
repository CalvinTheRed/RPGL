package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AttackRoll;
import org.rpgl.subevent.DealDamage;
import org.rpgl.subevent.SavingThrow;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.ApplyVampirism class.
 *
 * @author Calvin Withun
 */
public class ApplyVampirismTest {

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
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new ApplyVampirism();
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
    @DisplayName("execute applies vampirism (attack roll)")
    void execute_appliesVampirism_attackRoll() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();

        ApplyVampirism applyVampirism = new ApplyVampirism();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "apply_vampirism",
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                }
            }*/
            this.putString("function", "apply_vampirism");
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
        }};

        applyVampirism.execute(null, attackRoll, functionJson, context);

        String expected = """
                {"subevent":"attack_roll","tags":[],"vampirism":{"damage_type":"necrotic","denominator":2,"numerator":1,"round_up":false}}""";
        assertEquals(expected, attackRoll.toString(),
                "vampirism should be applied to attack roll"
        );
    }

    @Test
    @DisplayName("execute applies vampirism (deal damage)")
    void execute_appliesVampirism_dealDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DealDamage dealDamage = new DealDamage();

        ApplyVampirism applyVampirism = new ApplyVampirism();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "apply_vampirism",
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                }
            }*/
            this.putString("function", "apply_vampirism");
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
        }};

        applyVampirism.execute(null, dealDamage, functionJson, context);

        String expected = """
                {"subevent":"deal_damage","tags":[],"vampirism":{"damage_type":"necrotic","denominator":2,"numerator":1,"round_up":false}}""";
        assertEquals(expected, dealDamage.toString(),
                "vampirism should be applied to subevent"
        );
    }

    @Test
    @DisplayName("execute applies vampirism (saving throw)")
    void execute_appliesVampirism_savingThrow() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();

        ApplyVampirism applyVampirism = new ApplyVampirism();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "apply_vampirism",
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                }
            }*/
            this.putString("function", "apply_vampirism");
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
        }};

        applyVampirism.execute(null, savingThrow, functionJson, context);

        String expected = """
                {"subevent":"saving_throw","tags":[],"vampirism":{"damage_type":"necrotic","denominator":2,"numerator":1,"round_up":false}}""";
        assertEquals(expected, savingThrow.toString(),
                "vampirism should be applied at attack roll"
        );
    }

}
