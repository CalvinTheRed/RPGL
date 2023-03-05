package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingDelivery;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.core.RPGLObject class.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTest {

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
    @DisplayName("equipItem item successfully equipped (mainhand, item in inventory)")
    void equipItem_itemSuccessfullyEquipped_mainhandItemInInventory() {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        String crossbowUuid = knight.getInventory().getString(0);
        knight.equipItem(crossbowUuid, "mainhand");

        assertEquals(crossbowUuid, knight.getEquippedItems().getString("mainhand"),
                "heavy crossbow should be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("equipItem item not equipped (mainhand, item absent from inventory)")
    void equipItem_itemNotEquipped_mainhandItemAbsentFromInventory() {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        String daggerUuid = RPGLFactory.newItem("demo:dagger").getUuid();
        knight.equipItem(daggerUuid, "mainhand");

        assertNotEquals(daggerUuid, knight.getEquippedItems().getString("mainhand"),
                "dagger should not be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("giveItem item added to inventory (item not already present)")
    void giveItem_itemAddedToInventory_itemNotAlreadyPresent() {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        String daggerUuid = RPGLFactory.newItem("demo:dagger").getUuid();
        knight.giveItem(daggerUuid);

        assertTrue(knight.getInventory().asList().contains(daggerUuid),
                "dagger should be present in the knight's inventory"
        );
    }

    @Test
    @DisplayName("giveItem item added to inventory (item already present)")
    void giveItem_itemAddedToInventory_itemAlreadyPresent() {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        JsonArray inventory = knight.getInventory();
        String alreadyHeldItemUuid = inventory.getString(0);
        knight.giveItem(alreadyHeldItemUuid);

        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            String itemUuid = inventory.getString(i);
            if (alreadyHeldItemUuid.equals(itemUuid)) {
                count++;
            }
        }
        assertEquals(1, count,
                "item should not be added if it is already present"
        );
    }

    @Test
    @DisplayName("getBaseArmorClass calculates 20")
    void getBaseArmorClass_calculatesTwenty() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(knight);

        assertEquals(20, knight.getBaseArmorClass(context),
                "demo:knight should have 20 AC"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (no temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_noTemporaryHitPoints() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        youngRedDragon.reduceHitPoints(10);

        assertEquals(168, youngRedDragon.getHealthData().getInteger("current"),
                "demo:young_red_dragon should lose 10 hit points (178-10=168)"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (few temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_fewTemporaryHitPoints() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        youngRedDragon.getHealthData().putInteger("temporary", 10);
        youngRedDragon.reduceHitPoints(20);

        assertEquals(168, youngRedDragon.getHealthData().getInteger("current"),
                "demo:young_red_dragon should net lose 10 hit points (178+10-20=168)"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (many temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_ManyTemporaryHitPoints() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        youngRedDragon.getHealthData().putInteger("temporary", 20);
        youngRedDragon.reduceHitPoints(10);

        assertEquals(178, youngRedDragon.getHealthData().getInteger("current"),
                "demo:young_red_dragon should lose no hit points (178)"
        );
        assertEquals(10, youngRedDragon.getHealthData().getInteger("temporary"),
                "demo:young_red_dragon should lose 10 temporary hit points (20-10=10)"
        );
    }

    // TODO tests for taking damage

    // TODO tests for getting weapon proficiency

    // TODO tests for getting saving throw proficiency

    @Test
    @DisplayName("getAbilityModifierFromAbilityScore returns correct modifier")
    void getAbilityModifierFromAbilityScore_returnsCorrectModifier() {
        assertEquals(-2, RPGLObject.getAbilityModifierFromAbilityScore( 7));
        assertEquals(-1, RPGLObject.getAbilityModifierFromAbilityScore( 8));
        assertEquals(-1, RPGLObject.getAbilityModifierFromAbilityScore( 9));
        assertEquals( 0, RPGLObject.getAbilityModifierFromAbilityScore(10));
        assertEquals( 0, RPGLObject.getAbilityModifierFromAbilityScore(11));
        assertEquals( 1, RPGLObject.getAbilityModifierFromAbilityScore(12));
        assertEquals( 1, RPGLObject.getAbilityModifierFromAbilityScore(13));
    }

    @Test
    @DisplayName("getAbilityModifierFromAbilityName returns correct modifier")
    void getAbilityModifierFromAbilityName_returnsCorrectModifier() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(knight);

        assertEquals(3, knight.getAbilityModifierFromAbilityName("str", context),
                "demo:knight str of 16 should have modifier of +3"
        );
        assertEquals(0, knight.getAbilityModifierFromAbilityName("dex", context),
                "demo:knight dex of 11 should have modifier of +0"
        );
        assertEquals(2, knight.getAbilityModifierFromAbilityName("con", context),
                "demo:knight con of 14 should have modifier of +2"
        );
        assertEquals(0, knight.getAbilityModifierFromAbilityName("int", context),
                "demo:knight int of 11 should have modifier of +0"
        );
        assertEquals(0, knight.getAbilityModifierFromAbilityName("wis", context),
                "demo:knight wis of 11 should have modifier of +0"
        );
        assertEquals(2, knight.getAbilityModifierFromAbilityName("cha", context),
                "demo:knight cha of 15 should have modifier of +2"
        );
    }

    @Test
    @DisplayName("getProficiencyBonus returns correct bonus")
    void getProficiencyBonus_returnsCorrectValue() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(knight);

        assertEquals(2, knight.getEffectiveProficiencyBonus(context),
                "demo:knight should have proficiency bonus of 2"
        );
    }

    @Test
    @DisplayName("getEvents returns an array of the correct events")
    void getEvents_returnsArrayOfCorrectEvents() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(youngRedDragon);

        List<RPGLEvent> events = youngRedDragon.getEventObjects();

        assertEquals(3, events.size(),
                "demo:young_red_dragon should have 3 RPGLEvents"
        );
        assertEquals("demo:young_red_dragon_bite_attack", events.get(0).getId(),
                "demo:young_red_dragon should have the demo:young_red_dragon_bite_attack event"
        );
        assertEquals("demo:young_red_dragon_claw_attack", events.get(1).getId(),
                "demo:young_red_dragon should have the demo:young_red_dragon_claw_attack event"
        );
        assertEquals("demo:young_red_dragon_fire_breath", events.get(2).getId(),
                "demo:young_red_dragon should have the demo:young_red_dragon_fire_breath event"
        );
    }

    @Test
    @DisplayName("getEffects returns an array of the correct effects")
    void getEffects_returnsArrayOfCorrectEffects() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(youngRedDragon);

        List<RPGLEffect> effects = youngRedDragon.getEffectObjects();

        assertEquals(1, effects.size(),
                "demo:young_red_dragon should have 1 RPGLEffect"
        );
        assertEquals("demo:fire_immunity", effects.get(0).getId(),
                "demo:young_red_dragon should have the demo:fire_immunity effect"
        );
    }

    @Test
    @DisplayName("addRemoveEffect effects can be added and removed")
    void addRemoveEffect_effectsCanBeAddedAndRemoved() {
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(knight);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("demo:fire_immunity");
        List<RPGLEffect> effects;

        knight.addEffect(fireImmunity);
        effects = knight.getEffectObjects();
        assertEquals(1, effects.size(),
                "demo:knight should have 1 effect"
        );
        assertEquals("demo:fire_immunity", effects.get(0).getId(),
                "demo:knight should have the demo:fire_immunity effect"
        );

        assertTrue(knight.removeEffect(fireImmunity.getUuid()),
                "the provided UUID should correspond to an effect assigned to the knight"
        );
        effects = knight.getEffectObjects();
        assertEquals(0, effects.size(),
                "demo:knight should have 0 effects"
        );
    }

    @Test
    @DisplayName("invokeEvent event behaves properly")
    void invokeEvent_eventBehavesProperly() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject knight = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(youngRedDragon);
        context.add(knight);

        youngRedDragon.invokeEvent(
                new RPGLObject[] { knight },
                RPGLFactory.newEvent("demo:young_red_dragon_fire_breath"),
                context
        );

        assertEquals(4, knight.getHealthData().getInteger("current"),
                "demo:knight should have 4 health left after failing a save against demo:young_red_dragon's breath attack"
        );
    }

    @Test
    @DisplayName("receiveHealing missing hit points are restored")
    void receiveHealing_missingHitPointsAreRestored() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_delivery",
                "healing": 10
            }*/
            this.putString("subevent", "healing_delivery");
            this.putInteger("healing", 10);
        }});

        healingDelivery.setSource(source);
        healingDelivery.prepare(context);
        healingDelivery.setTarget(target);
        healingDelivery.invoke(context);

        target.receiveHealing(healingDelivery, context);

        assertEquals(20, target.getHealthData().getInteger("current"),
                "target should recover 10 hit points (10+10=20)"
        );
    }

    @Test
    @DisplayName("receiveHealing hit point maximum is not exceeded")
    void receiveHealing_mitPointMaximumIsNotExceeded() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 177);

        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_delivery",
                "healing": 10
            }*/
            this.putString("subevent", "healing_delivery");
            this.putInteger("healing", 10);
        }});

        healingDelivery.setSource(source);
        healingDelivery.prepare(context);
        healingDelivery.setTarget(target);
        healingDelivery.invoke(context);

        target.receiveHealing(healingDelivery, context);

        assertEquals(178, target.getHealthData().getInteger("current"),
                "target should only recover its one missing hit point when healed for 10 (177+10=187, max 178: 187 -> 178)"
        );
    }

}
