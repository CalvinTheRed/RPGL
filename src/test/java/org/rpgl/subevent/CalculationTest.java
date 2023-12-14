package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        // create an anonymous class for Calculation for the purpose of running tests on it
        calculation = new Calculation("calculation") {

            @Override
            public Subevent clone() {
                return this;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
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
    @DisplayName("addBonus should be able to go negative and should be additive")
    void addBonus_canGoNegativeAndIsAdditive() {
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", -5);
            this.putJsonArray("dice", new JsonArray());
        }});
        assertEquals(-5, calculation.getBonus(),
                "bonus should be able to no below 0"
        );
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 10);
            this.putJsonArray("dice", new JsonArray());
        }});
        assertEquals(5, calculation.getBonus(),
                "bonus values should be additive"
        );
    }

    @Test
    @DisplayName("setBase should be the most recent value")
    void setBase_mostRecentValue() {
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
    @DisplayName("get returns base + bonus when set is null")
    void get_notSet() {
        calculation.setBase(10);
        calculation.addBonus(new JsonObject() {{
            this.putInteger("bonus", 5);
            this.putJsonArray("dice", new JsonArray());
        }});
        assertEquals(15, calculation.get(),
                "get should return base + bonus (10+5) when set is null"
        );
    }

    @Test
    @DisplayName("scale should calculate half rounded down (default behavior)")
    void scale_shouldCalculateHalfRoundedDown_defaultBehavior() {
        int value = 11;
        int scaledValue = Calculation.scale(value, new JsonObject());

        assertEquals(5, scaledValue,
                "scaled value should be half (of 11) rounded down"
        );
    }

    @Test
    @DisplayName("scale should calculate according to specified ratio and rounding")
    void scale_shouldCalculateAccordingToSpecifiedRatioAndRounding() {
        int value = 11;
        int scaledValue = Calculation.scale(value, new JsonObject() {{
            /*{
                "numerator": 1,
                "denominator": 3,
                "round_up": true
            }*/
            this.putInteger("numerator", 1);
            this.putInteger("denominator", 3);
            this.putBoolean("round_up", true);
        }});

        assertEquals(4, scaledValue,
                "scaled value should be one third (of 11) rounded up"
        );
    }

    @Test
    @DisplayName("processBonusJson generates correct bonus (range)")
    void processBonusJson_generatesCorrectBonus_range() throws Exception {
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
    @DisplayName("processBonusJson generates correct bonus (modifier)")
    void processBonusJson_generatesCorrectBonus_modifier() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.getAbilityScores().putInteger("str", 20);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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
        assertEquals(expected, Calculation.processBonusJson(null, dummySubevent, formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processBonusJson generates correct bonus (ability)")
    void processBonusJson_generatesCorrectBonus_ability() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.getAbilityScores().putInteger("str", 20);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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
        assertEquals(expected, Calculation.processBonusJson(null, dummySubevent, formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processBonusJson generates correct bonus (proficiency)")
    void processBonusJson_generatesCorrectBonus_proficiency() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.setProficiencyBonus(5);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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
        assertEquals(expected, Calculation.processBonusJson(null, dummySubevent, formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processBonusJson generates correct bonus (level)")
    void processBonusJson_generatesCorrectBonus_level() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.levelUp("debug:blank", new JsonObject());

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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
        assertEquals(expected, Calculation.processBonusJson(null, dummySubevent, formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processBonusJson generates correct bonus (level) (no class specified)")
    void processBonusJson_generatesCorrectBonus_level_noClassSpecified() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.levelUp("debug:blank", new JsonObject());

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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
        assertEquals(expected, Calculation.processBonusJson(null, dummySubevent, formulaJson, new DummyContext()).toString(),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processSetJson generates correct set value (number)")
    void processSetJson_generatesCorrectSetValue_number() throws Exception {
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
    @DisplayName("processSetJson generates correct set value (modifier)")
    void processSetJson_generatesCorrectSetValue_modifier() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.getAbilityScores().putInteger("str", 20);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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

        assertEquals(5, Calculation.processSetJson(null, dummySubevent, formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processSetJson generates correct set value (ability)")
    void processSetJson_generatesCorrectSetValue_ability() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.getAbilityScores().putInteger("str", 20);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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

        assertEquals(20, Calculation.processSetJson(null, dummySubevent, formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processSetJson generates correct set value (proficiency)")
    void processSetJson_generatesCorrectSetValue_proficiency() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.setProficiencyBonus(5);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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

        assertEquals(5, Calculation.processSetJson(null, dummySubevent, formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processSetJson generates correct set value (level)")
    void processSetJson_generatesCorrectSetValue_level() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.levelUp("debug:blank", new JsonObject());

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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

        assertEquals(1, Calculation.processSetJson(null, dummySubevent, formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

    @Test
    @DisplayName("processSetJson generates correct set value (level) (no class specified)")
    void processSetJson_generatesCorrectSetValue_level_noClassSpecified() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        dummy.levelUp("debug:blank", new JsonObject());

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dummy);

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

        assertEquals(1, Calculation.processSetJson(null, dummySubevent, formulaJson, new DummyContext()),
                "bonus object failed to evaluate correctly"
        );
    }

}
