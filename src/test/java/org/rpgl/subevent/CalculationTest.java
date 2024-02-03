package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.subevent.Calculation class.
 *
 * @author Calvin Withun
 */
public class CalculationTest {

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
                return this;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return this;
            }

            @Override
            public Calculation run(RPGLContext context, JsonArray originPoint) {
                return this;
            }
        };

        calculation.joinSubeventData(new JsonObject() {{
            /*{
                "bonuses": [ ]
                "minimum": {
                    "value": Integer.MIN_VALUE
                }
            }*/
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", Integer.MIN_VALUE);
            }});
        }});
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("bonuses can scale")
    void bonusesCanScale() {
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 5);
            this.putJsonArray("dice", new JsonArray());
            this.putJsonObject("scale", new JsonObject() {{
                this.putInteger("numerator", -1);
                this.putInteger("denominator", 1);
                this.putBoolean("round_up", false);
            }});
        }});

        assertEquals(-5, calculation.getBonus(),
                "bonus should scale"
        );
    }

    @Test
    @DisplayName("bonuses are additive")
    void bonusesAreAdditive() {
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 5);
            this.putJsonArray("dice", new JsonArray());
            this.putJsonObject("scale", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 1);
                this.putBoolean("round_up", false);
            }});
        }});
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 5);
            this.putJsonArray("dice", new JsonArray());
            this.putJsonObject("scale", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 1);
                this.putBoolean("round_up", false);
            }});
        }});

        assertEquals(5+5, calculation.getBonus(),
                "bonuses should be cumulative"
        );
    }

    @Test
    @DisplayName("base gets overridden with latest")
    void baseGetsOverriddenWithLatest() {
        calculation.setBase(1);
        assertEquals(1, calculation.getBase(),
                "base should be most recent value (1)"
        );

        calculation.setBase(-5);
        assertEquals(-5, calculation.getBase(),
                "base should be most recent value (-5)"
        );

        calculation.setBase(5);
        assertEquals(5, calculation.getBase(),
                "base should be most recent value (5)"
        );
    }

    @Test
    @DisplayName("calculates without set")
    void calculatesWithoutSet() {
        calculation.setBase(10);
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 5);
            this.putJsonArray("dice", new JsonArray());
            this.putJsonObject("scale", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 1);
                this.putBoolean("round_up", false);
            }});
        }});

        assertEquals(10 /*base*/ +5 /*bonus*/, calculation.get(),
                "get should return base + bonus when set is null"
        );
    }

    @Test
    @DisplayName("scale rounds down by default")
    void scaleRoundsDownByDefault() {
        int value = 11;
        int scaledValue = Calculation.scale(value, new JsonObject());

        assertEquals(5, scaledValue,
                "scaled value should be half (of 11) rounded down"
        );
    }

    @Test
    @DisplayName("scale rounds up")
    void scaleRoundsUp() {
        int scaledValue = Calculation.scale(11, new JsonObject() {{
            this.putBoolean("round_up", true);
        }});

        assertEquals(6, scaledValue,
                "scaled value should be half (of 11) rounded up"
        );
    }

    @Test
    @DisplayName("scale matches specified ratio")
    void scaleMatchesSpecifiedRatio() {
        int scaledValue = Calculation.scale(9, new JsonObject() {{
            /*{
                "numerator": 1,
                "denominator": 3,
                "round_up": true
            }*/
            this.putInteger("numerator", 1);
            this.putInteger("denominator", 3);
            this.putBoolean("round_up", true);
        }});

        assertEquals(3, scaledValue,
                "scaled value should be one third (of 9)"
        );
    }

    @Test
    @DisplayName("processes bonus (range)")
    void processesBonus_range() throws Exception {
        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "range",
                "bonus": 2,
                "dice": [
                    { "count": 2, "size": 6, "determined": [ 3 ] }
                ]
            }*/
            this.putString("formula", "range");
            this.putInteger("bonus", 2);
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("count", 2);
                    this.putInteger("size", 6);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(3);
                    }});
                }});
            }});
        }};

        String expected = """
                {"bonus":2,"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}""";
        assertEquals(expected, Calculation.processBonusJson(null, null, formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes bonus (modifier)")
    void processesBonus_modifier() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "modifier",
                "ability": "str",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_origin": false
                }
            }*/
            this.putString("formula", "modifier");
            this.putString("ability", "str");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        String expected = """
                {"bonus":5,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}""";
        assertEquals(expected, Calculation.processBonusJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes bonus (ability)")
    void processesBonus_ability() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "ability",
                "ability": "str",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_origin": false
                }
            }*/
            this.putString("formula", "ability");
            this.putString("ability", "str");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        String expected = """
                {"bonus":20,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}""";
        assertEquals(expected, Calculation.processBonusJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes bonus (proficiency)")
    void processesBonus_proficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(5);

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "proficiency",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_origin": false
                }
            }*/
            this.putString("formula", "proficiency");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        String expected = """
                {"bonus":5,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}""";
        assertEquals(expected, Calculation.processBonusJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes bonus (level) (class specified)")
    void processesBonus_level_classSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .levelUp("debug:blank", new JsonObject());

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "level",
                "class": "debug:blank",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_origin": false
                }
            }*/
            this.putString("formula", "level");
            this.putString("class", "debug:blank");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        String expected = """
                {"bonus":1,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}""";
        assertEquals(expected, Calculation.processBonusJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes bonus (level) (no class specified)")
    void processesBonus_level_noClassSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .levelUp("debug:blank", new JsonObject());

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "level",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_origin": false
                }
            }*/
            this.putString("formula", "level");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        String expected = """
                {"bonus":1,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}""";
        assertEquals(expected, Calculation.processBonusJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes set (number)")
    void processesSet_number() throws Exception {
        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "number",
                "number": 5
            }*/
            this.putString("formula", "number");
            this.putInteger("number", 5);
        }};

        assertEquals(5, Calculation.processSetJson(null, null, formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes set (modifier)")
    void processesSet_modifier() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "modifier",
                "ability": "str",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_source": false
                }
            }*/
            this.putString("formula", "modifier");
            this.putString("ability", "str");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        assertEquals(5, Calculation.processSetJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes set (ability)")
    void processesSet_ability() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "ability",
                "ability": "str",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_source": false
                }
            }*/
            this.putString("formula", "ability");
            this.putString("ability", "str");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        assertEquals(20, Calculation.processSetJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes set (proficiency)")
    void processesSet_proficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(5);

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "proficiency",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_source": false
                }
            }*/
            this.putString("formula", "proficiency");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        assertEquals(5, Calculation.processSetJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes set (level) (class specified)")
    void processesSet_level_classSpecified() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .levelUp("debug:blank", new JsonObject());

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "level",
                "class": "debug:blank",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_source": false
                }
            }*/
            this.putString("formula", "level");
            this.putString("class", "debug:blank");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        assertEquals(1, Calculation.processSetJson(null, new DummySubevent().setSource(dummy), formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processes set (level) (no class specified)")
    void processesSet_level_noClassSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .levelUp("debug:blank", new JsonObject());

        JsonObject formulaJson = new JsonObject() {{
            /*{
                "formula": "level",
                "object": {
                    "from": "subevent",
                    "object": "source",
                    "as_source": false
                }
            }*/
            this.putString("formula", "level");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
                this.putBoolean("as_origin", false);
            }});
        }};

        assertEquals(1, Calculation.processSetJson(null, new DummySubevent().setSource(object), formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

}
