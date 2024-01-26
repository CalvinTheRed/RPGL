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
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.subevent.VampiricSubevent class.
 *
 * @author Calvin Withun
 */
public class VampiricSubeventTest {

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
    @DisplayName("heals partial damage (specific damage type)")
    void healsPartialDamage_specificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 1);

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

        VampiricSubevent.handleVampirism(dummySubevent, new JsonObject() {{
            this.putInteger("necrotic", 10);
            this.putInteger("radiant", 10);
        }}, new DummyContext());

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half necrotic damage via vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should not be healed via vampirism"
        );
    }

    @Test
    @DisplayName("heals partial damage (all damage types)")
    void healsPartialDamage_allDamageTypes() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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

        VampiricSubevent.handleVampirism(dummySubevent, damageByType, new DummyContext());

        assertEquals(11, source.getHealthData().getInteger("current"),
                "source should be healed for half total damage via vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should not be healed via vampirism"
        );
    }

}
