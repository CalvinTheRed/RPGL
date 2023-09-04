package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.subevent.VampiricSubevent class.
 *
 * @author Calvin Withun
 */
public class VampiricSubeventTest {

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
    @DisplayName("calculateVampiricHealing should calculate half rounded down (default behavior)")
    void calculateVampiricHealing_shouldCalculateHalfRoundedDown_defaultBehavior() {
        int vampiricDamage = 11;
        int vampiricHealing = VampiricSubevent.calculateVampiricHealing(vampiricDamage, new JsonObject());

        assertEquals(5, vampiricHealing,
                "vampiric healing should be half (of 11) rounded down"
        );
    }

    @Test
    @DisplayName("calculateVampiricHealing should calculate according to specified ratio and rounding")
    void calculateVampiricHealing_shouldCalculateAccordingToSpecifiedRatioAndRounding() {
        int vampiricDamage = 11;
        int vampiricHealing = VampiricSubevent.calculateVampiricHealing(vampiricDamage, new JsonObject() {{
            /*{
                "numerator": 1,
                "denominator": 3,
                "round_up": true
            }*/
            this.putInteger("numerator", 1);
            this.putInteger("denominator", 3);
            this.putBoolean("round_up", true);
        }});

        assertEquals(4, vampiricHealing,
                "vampiric healing should be one third (of 11) rounded up"
        );
    }

    @Test
    @DisplayName("handleVampirism heals source for half damage (specific damage type)")
    void handleVampirism_healsSourceForHalfDamage_specificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 1);

        JsonObject damageByType = new JsonObject() {{
            this.putInteger("necrotic", 10);
            this.putInteger("radiant", 10);
        }};

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.joinSubeventData(new JsonObject() {{
            /*{
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                }
            }*/
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
        }});
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        VampiricSubevent.handleVampirism(dummySubevent, damageByType, context);

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half necrotic damage via vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should not be healed via vampirism"
        );
    }

    @Test
    @DisplayName("handleVampirism heals source for half damage (no specific damage type)")
    void handleVampirism_healsSourceForHalfDamage_noSpecificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 1);

        JsonObject damageByType = new JsonObject() {{
            this.putInteger("necrotic", 10);
            this.putInteger("radiant", 10);
        }};

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.joinSubeventData(new JsonObject() {{
            /*{
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false
                }
            }*/
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
            }});
        }});
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        VampiricSubevent.handleVampirism(dummySubevent, damageByType, context);

        assertEquals(11, source.getHealthData().getInteger("current"),
                "source should be healed for half total damage via vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should not be healed via vampirism"
        );
    }

}
