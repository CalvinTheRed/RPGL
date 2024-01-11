package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.CalculateBaseArmorClass class.
 *
 * @author Calvin Withun
 */
public class CalculateBaseArmorClassTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
        );
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new CalculateBaseArmorClass();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("gets shield bonus")
    void getsShieldBonus() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();
        calculateBaseArmorClass.setSource(object);

        assertEquals(0, calculateBaseArmorClass.getShieldBonus(),
                "prepareUnarmored should return 0 when shield is not wielded"
        );

        RPGLItem shield = RPGLFactory.newItem("std:armor/shield/metal");
        object.giveItem(shield.getUuid());
        object.equipItem(shield.getUuid(), "offhand");

        assertEquals(2, calculateBaseArmorClass.getShieldBonus(),
                "prepareUnarmored should return >0 when shield is wielded"
        );
    }

    @Test
    @DisplayName("prepares base armor")
    void preparesBaseArmor() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("dex", 16);
        CalculateBaseArmorClass calculateBaseArmorClass;

        calculateBaseArmorClass = new CalculateBaseArmorClass();
        calculateBaseArmorClass.setSource(object);
        assertEquals(13, calculateBaseArmorClass.prepareUnarmored(new DummyContext()),
                "should have 13 AC when unarmored"
        );
        assertTrue(calculateBaseArmorClass.hasTag("unarmored"),
                "should have unarmored tag"
        );
        assertFalse(calculateBaseArmorClass.hasTag("armored"),
                "should not have armored tag"
        );

        calculateBaseArmorClass = new CalculateBaseArmorClass();
        calculateBaseArmorClass.setSource(object);
        assertEquals(11 /*base*/ +3 /*dex bonus*/,
                calculateBaseArmorClass.prepareArmored(
                        RPGLFactory.newItem("std:armor/light/leather"),
                        new DummyContext()
                ),
                "should have 14 AC when armored with leather armor"
        );
        assertFalse(calculateBaseArmorClass.hasTag("unarmored"),
                "should not have unarmored tag"
        );
        assertTrue(calculateBaseArmorClass.hasTag("armored"),
                "should have armored tag"
        );

        calculateBaseArmorClass = new CalculateBaseArmorClass();
        calculateBaseArmorClass.setSource(object);
        assertEquals(14 /*base*/ +2 /*limited dex bonus*/,
                calculateBaseArmorClass.prepareArmored(
                        RPGLFactory.newItem("std:armor/medium/breastplate"),
                        new DummyContext()
                ),
                "should have 16 AC when armored with breastplate armor"
        );
        assertFalse(calculateBaseArmorClass.hasTag("unarmored"),
                "should not have unarmored tag"
        );
        assertTrue(calculateBaseArmorClass.hasTag("armored"),
                "should have armored tag"
        );

        calculateBaseArmorClass = new CalculateBaseArmorClass();
        calculateBaseArmorClass.setSource(object);
        assertEquals(18 /*base*/,
                calculateBaseArmorClass.prepareArmored(
                        RPGLFactory.newItem("std:armor/heavy/plate"),
                        new DummyContext()
                ),
                "should have 18 AC when armored with plate armor"
        );
        assertFalse(calculateBaseArmorClass.hasTag("unarmored"),
                "should not have unarmored tag"
        );
        assertTrue(calculateBaseArmorClass.hasTag("armored"),
                "should have armored tag"
        );
    }

    @Test
    @DisplayName("adds shield bonus to AC")
    void addsShieldBonusToAC() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem shield = RPGLFactory.newItem("std:armor/shield/metal");
        source.giveItem(shield.getUuid());
        source.equipItem(shield.getUuid(), "offhand");

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);
        calculateBaseArmorClass.prepare(new DummyContext(), List.of());

        assertEquals(10 /*base*/ +2 /*shield*/, calculateBaseArmorClass.get(),
                "shield should raise AC to 12"
        );
    }

}
