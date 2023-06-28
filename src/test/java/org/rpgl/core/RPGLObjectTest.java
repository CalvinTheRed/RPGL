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
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        String crossbowUuid = knight.getInventory().getString(0);
        knight.equipItem(crossbowUuid, "mainhand");

        assertEquals(crossbowUuid, knight.getEquippedItems().getString("mainhand"),
                "heavy crossbow should be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("equipItem item not equipped (mainhand, item absent from inventory)")
    void equipItem_itemNotEquipped_mainhandItemAbsentFromInventory() {
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        String daggerUuid = RPGLFactory.newItem("std:dagger").getUuid();
        knight.equipItem(daggerUuid, "mainhand");

        assertNotEquals(daggerUuid, knight.getEquippedItems().getString("mainhand"),
                "dagger should not be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("giveItem item added to inventory (item not already present)")
    void giveItem_itemAddedToInventory_itemNotAlreadyPresent() {
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        String daggerUuid = RPGLFactory.newItem("std:dagger").getUuid();
        knight.giveItem(daggerUuid);

        assertTrue(knight.getInventory().asList().contains(daggerUuid),
                "dagger should be present in the knight's inventory"
        );
    }

    @Test
    @DisplayName("giveItem item added to inventory (item already present)")
    void giveItem_itemAddedToInventory_itemAlreadyPresent() {
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        JsonArray inventory = knight.getInventory();
        String alreadyHeldItemUuid = inventory.getString(0);
        knight.giveItem(alreadyHeldItemUuid);

        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            String itemUuid = inventory.getString(i);
            if (Objects.equals(alreadyHeldItemUuid, itemUuid)) {
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
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(knight);

        assertEquals(20, knight.getBaseArmorClass(context),
                "std:knight should have 20 AC"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (no temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_noTemporaryHitPoints() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        youngRedDragon.reduceHitPoints(10, context);

        assertEquals(168, youngRedDragon.getHealthData().getInteger("current"),
                "std:young_red_dragon should lose 10 hit points (178-10=168)"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (few temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_fewTemporaryHitPoints() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        youngRedDragon.getHealthData().putInteger("temporary", 10);
        youngRedDragon.reduceHitPoints(20, context);

        assertEquals(168, youngRedDragon.getHealthData().getInteger("current"),
                "std:young_red_dragon should net lose 10 hit points (178+10-20=168)"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (many temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_ManyTemporaryHitPoints() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        youngRedDragon.getHealthData().putInteger("temporary", 20);
        youngRedDragon.reduceHitPoints(10, context);

        assertEquals(178, youngRedDragon.getHealthData().getInteger("current"),
                "std:young_red_dragon should lose no hit points (178)"
        );
        assertEquals(10, youngRedDragon.getHealthData().getInteger("temporary"),
                "std:young_red_dragon should lose 10 temporary hit points (20-10=10)"
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
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(knight);

        assertEquals(3, knight.getAbilityModifierFromAbilityName("str", context),
                "std:knight str of 16 should have modifier of +3"
        );
        assertEquals(0, knight.getAbilityModifierFromAbilityName("dex", context),
                "std:knight dex of 11 should have modifier of +0"
        );
        assertEquals(2, knight.getAbilityModifierFromAbilityName("con", context),
                "std:knight con of 14 should have modifier of +2"
        );
        assertEquals(0, knight.getAbilityModifierFromAbilityName("int", context),
                "std:knight int of 11 should have modifier of +0"
        );
        assertEquals(0, knight.getAbilityModifierFromAbilityName("wis", context),
                "std:knight wis of 11 should have modifier of +0"
        );
        assertEquals(2, knight.getAbilityModifierFromAbilityName("cha", context),
                "std:knight cha of 15 should have modifier of +2"
        );
    }

    @Test
    @DisplayName("getProficiencyBonus returns correct bonus")
    void getProficiencyBonus_returnsCorrectValue() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(knight);

        assertEquals(2, knight.getEffectiveProficiencyBonus(context),
                "std:knight should have proficiency bonus of 2"
        );
    }

    @Test
    @DisplayName("getEvents returns an array of the correct events")
    void getEvents_returnsArrayOfCorrectEvents() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);

        List<RPGLEvent> events = youngRedDragon.getEventObjects(context);

        assertEquals(1, events.size(),
                "std:young_red_dragon should have 1 RPGLEvent"
        );
        assertEquals("std:young_red_dragon_fire_breath", events.get(0).getId(),
                "std:young_red_dragon should have the std:young_red_dragon_fire_breath event"
        );
    }

    @Test
    @DisplayName("getEffects returns an array of the correct effects")
    void getEffects_returnsArrayOfCorrectEffects() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);

        List<RPGLEffect> effects = youngRedDragon.getEffectObjects();

        assertEquals(1, effects.size(),
                "std:young_red_dragon should have 1 RPGLEffect"
        );
        assertEquals("std:fire_immunity", effects.get(0).getId(),
                "std:young_red_dragon should have the std:fire_immunity effect"
        );
    }

    @Test
    @DisplayName("addRemoveEffect effects can be added and removed")
    void addRemoveEffect_effectsCanBeAddedAndRemoved() {
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(knight);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("std:fire_immunity");
        List<RPGLEffect> effects;

        knight.addEffect(fireImmunity);
        effects = knight.getEffectObjects();
        assertEquals(1, effects.size(),
                "std:knight should have 1 effect"
        );
        assertEquals("std:fire_immunity", effects.get(0).getId(),
                "std:knight should have the std:fire_immunity effect"
        );

        assertTrue(knight.removeEffect(fireImmunity.getUuid()),
                "the provided UUID should correspond to an effect assigned to the knight"
        );
        effects = knight.getEffectObjects();
        assertEquals(0, effects.size(),
                "std:knight should have 0 effects"
        );
    }

    @Test
    @DisplayName("invokeEvent event behaves properly")
    void invokeEvent_eventBehavesProperly() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:young_red_dragon");
        RPGLObject knight = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        context.add(knight);

        youngRedDragon.invokeEvent(
                new RPGLObject[] { knight },
                RPGLFactory.newEvent("std:young_red_dragon_fire_breath"),
                youngRedDragon.getResourceObjects(),
                context
        );

        assertEquals(4, knight.getHealthData().getInteger("current"),
                "std:knight should have 4 health left after failing a save against std:young_red_dragon's breath attack"
        );
        for (RPGLResource resource : youngRedDragon.getResourceObjects()) {
            assertTrue(resource.getExhausted());
        }
    }

    @Test
    @DisplayName("receiveHealing missing hit points are restored")
    void receiveHealing_missingHitPointsAreRestored() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 10);

        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "bonus": 10,
                        "dice": [ ]
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("bonus", 10);
                    this.putJsonArray("dice", new JsonArray());
                }});
            }});
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
        RPGLObject source = RPGLFactory.newObject("std:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("std:young_red_dragon");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getHealthData().putInteger("current", 177);

        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "bonus": 10,
                        "dice": [ ]
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("bonus", 10);
                    this.putJsonArray("dice", new JsonArray());
                }});
            }});
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

    @Test
    @DisplayName("processSubevent handles resources appropriately")
    void processSubevent_handlesResourcesAppropriately() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLResource resource = RPGLFactory.newResource("std:action");
        source.addResource(resource);

        resource.exhaust();
        source.startTurn(context);

        assertFalse(resource.getExhausted(),
                "resource should not be exhausted after turn start"
        );
    }

}
