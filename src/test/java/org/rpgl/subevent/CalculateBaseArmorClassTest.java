package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.CalculateBaseArmorClass class.
 *
 * @author Calvin Withun
 */
public class CalculateBaseArmorClassTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new CalculateBaseArmorClass();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("getShieldBonus returns 0 (commoner not wielding a shield)")
    void getShieldBonus_returnsZero_commoner() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);

        assertEquals(0, calculateBaseArmorClass.getShieldBonus(),
                "prepareUnarmored should return 0 for a commoner wielding no shield"
        );
    }

    @Test
    @DisplayName("getShieldBonus returns 2 (knight wielding a shield)")
    void getShieldBonus_returnsTwo_knight() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);

        assertEquals(2, calculateBaseArmorClass.getShieldBonus(),
                "prepareUnarmored should return 2 for a knight wielding a shield"
        );
    }

    @Test
    @DisplayName("prepareUnarmored returns 10 (commoner)")
    void prepareUnarmored_returnsTen_commoner() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);

        assertEquals(10, calculateBaseArmorClass.prepareUnarmored(context),
                "prepareUnarmored should return 10 for a commoner"
        );
    }

    @Test
    @DisplayName("prepareArmored returns 18 (knight wearing plate armor)")
    void prepareArmored_returnsEighteen_knightPlateArmor() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);

        assertEquals(18, calculateBaseArmorClass.prepareArmored(UUIDTable.getItem(source.getEquippedItems().getString("armor")), context),
                "prepareUnarmored should return 18 for a knight wearing plate armor"
        );
    }

    @Test
    @DisplayName("prepareArmored returns 14 (knight wearing breastplate armor)")
    void prepareArmored_returnsFourteen_knightBreastplateArmor() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        RPGLItem breastplateArmor = RPGLFactory.newItem("demo:breastplate_armor");
        source.giveItem(breastplateArmor.getUuid());
        source.equipItem(breastplateArmor.getUuid(), "armor");
        calculateBaseArmorClass.setSource(source);

        assertEquals(14, calculateBaseArmorClass.prepareArmored(UUIDTable.getItem(source.getEquippedItems().getString("armor")), context),
                "prepareUnarmored should return 18 for a knight wearing breastplate armor"
        );
    }

    @Test
    @DisplayName("prepareArmored returns 11 (knight wearing leather armor)")
    void prepareArmored_returnsEleven_knightLeatherArmor() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        RPGLItem leatherArmor = RPGLFactory.newItem("demo:leather_armor");
        source.giveItem(leatherArmor.getUuid());
        source.equipItem(leatherArmor.getUuid(), "armor");
        calculateBaseArmorClass.setSource(source);

        assertEquals(11, calculateBaseArmorClass.prepareArmored(UUIDTable.getItem(source.getEquippedItems().getString("armor")), context),
                "prepareUnarmored should return 11 for a knight wearing leather armor"
        );
    }

    @Test
    @DisplayName("prepare returns 10 (commoner with no armor or shield)")
    void prepare_returnsTen_commonerNoArmorNoShield() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);
        calculateBaseArmorClass.prepare(context);

        assertEquals(10, calculateBaseArmorClass.get(),
                "commoner with no armor or shield should have a base armor class of 10"
        );
    }

    @Test
    @DisplayName("prepare returns 18 (knight wearing plate armor and shield)")
    void prepare_returnsTwenty_knightPlateArmorShield() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);

        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();

        calculateBaseArmorClass.setSource(source);
        calculateBaseArmorClass.prepare(context);

        assertEquals(20, calculateBaseArmorClass.get(),
                "knight with plate armor and shield should have a base armor class of 20"
        );
    }

}
