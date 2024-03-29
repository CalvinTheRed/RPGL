package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AttackRoll;
import org.rpgl.subevent.DealDamage;
import org.rpgl.subevent.SavingThrow;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.ApplyVampirism class.
 *
 * @author Calvin Withun
 */
public class ApplyVampirismTest {

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
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new ApplyVampirism().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("applies vampirism (attack roll)")
    void appliesVampirism_attackRoll() throws Exception {
        AttackRoll attackRoll = new AttackRoll();

        new ApplyVampirism().execute(null, attackRoll, new JsonObject() {{
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
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"subevent":"attack_roll","tags":["attack_roll"],"vampirism":{"damage_type":"necrotic","denominator":2,"numerator":1,"round_up":false}}""";
        assertEquals(expected, attackRoll.toString(),
                "vampirism should be applied to attack roll"
        );
    }

    @Test
    @DisplayName("applies vampirism (deal damage)")
    void appliesVampirism_dealDamage() throws Exception {
        DealDamage dealDamage = new DealDamage();

        new ApplyVampirism().execute(null, dealDamage, new JsonObject() {{
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
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"subevent":"deal_damage","tags":["deal_damage"],"vampirism":{"damage_type":"necrotic","denominator":2,"numerator":1,"round_up":false}}""";
        assertEquals(expected, dealDamage.toString(),
                "vampirism should be applied to subevent"
        );
    }

    @Test
    @DisplayName("applies vampirism (saving throw)")
    void appliesVampirism_savingThrow() throws Exception {
        SavingThrow savingThrow = new SavingThrow();

        new ApplyVampirism().execute(null, savingThrow, new JsonObject() {{
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
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"subevent":"saving_throw","tags":["saving_throw"],"vampirism":{"damage_type":"necrotic","denominator":2,"numerator":1,"round_up":false}}""";
        assertEquals(expected, savingThrow.toString(),
                "vampirism should be applied at attack roll"
        );
    }

}
