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
import java.util.ArrayList;
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
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
        String crossbowUuid = knight.getInventory().getString(0);
        knight.equipItem(crossbowUuid, "mainhand");

        assertEquals(crossbowUuid, knight.getEquippedItems().getString("mainhand"),
                "heavy crossbow should be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("equipItem item not equipped (mainhand, item absent from inventory)")
    void equipItem_itemNotEquipped_mainhandItemAbsentFromInventory() {
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
        String daggerUuid = RPGLFactory.newItem("std:weapon/melee/simple/dagger").getUuid();
        knight.equipItem(daggerUuid, "mainhand");

        assertNotEquals(daggerUuid, knight.getEquippedItems().getString("mainhand"),
                "dagger should not be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("giveItem item added to inventory (item not already present)")
    void giveItem_itemAddedToInventory_itemNotAlreadyPresent() {
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
        String daggerUuid = RPGLFactory.newItem("std:weapon/melee/simple/dagger").getUuid();
        knight.giveItem(daggerUuid);

        assertTrue(knight.getInventory().asList().contains(daggerUuid),
                "dagger should be present in the knight's inventory"
        );
    }

    @Test
    @DisplayName("giveItem item added to inventory (item already present)")
    void giveItem_itemAddedToInventory_itemAlreadyPresent() {
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
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
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(knight);

        assertEquals(20, knight.getBaseArmorClass(context),
                "std:humanoid/knight should have 20 AC"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (no temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_noTemporaryHitPoints() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        youngRedDragon.reduceHitPoints(10, context);

        assertEquals(168, youngRedDragon.getHealthData().getInteger("current"),
                "std:dragon/red/young should lose 10 hit points (178-10=168)"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (few temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_fewTemporaryHitPoints() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        youngRedDragon.getHealthData().putInteger("temporary", 10);
        youngRedDragon.reduceHitPoints(20, context);

        assertEquals(168, youngRedDragon.getHealthData().getInteger("current"),
                "std:dragon/red/young should net lose 10 hit points (178+10-20=168)"
        );
    }

    @Test
    @DisplayName("reduceHitPoints deducts correct number of hit points (many temporary hit points)")
    void reduceHitPoints_deductsCorrectNumberOfHitPoints_ManyTemporaryHitPoints() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        youngRedDragon.getHealthData().putInteger("temporary", 20);
        youngRedDragon.reduceHitPoints(10, context);

        assertEquals(178, youngRedDragon.getHealthData().getInteger("current"),
                "std:dragon/red/young should lose no hit points (178)"
        );
        assertEquals(10, youngRedDragon.getHealthData().getInteger("temporary"),
                "std:dragon/red/young should lose 10 temporary hit points (20-10=10)"
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
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
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
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(knight);

        assertEquals(2, knight.getEffectiveProficiencyBonus(context),
                "std:humanoid/knight should have proficiency bonus of 2"
        );
    }

    @Test
    @DisplayName("getEvents returns an array of the correct events")
    void getEvents_returnsArrayOfCorrectEvents() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);

        List<RPGLEvent> events = youngRedDragon.getEventObjects(context);

        assertEquals(4, events.size(),
                "std:dragon/red/young should have 1 RPGLEvent"
        );
        assertEquals("std:object/dragon/red/young/breath", events.get(0).getId(),
                "std:dragon/red/young should have the std:object/dragon/red/young/breath event"
        );
        assertEquals("std:object/dragon/red/young/claw", events.get(1).getId(),
                "std:dragon/red/young should have the std:object/dragon/red/young/claw event"
        );
        assertEquals("std:object/dragon/red/young/bite", events.get(2).getId(),
                "std:dragon/red/young should have the std:object/dragon/red/young/bite event"
        );
        assertEquals("std:object/dragon/red/young/multiattack", events.get(3).getId(),
                "std:dragon/red/young should have the std:object/dragon/red/young/multiattack event"
        );
    }

    @Test
    @DisplayName("getEffects returns an array of the correct effects")
    void getEffects_returnsArrayOfCorrectEffects() {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);

        List<RPGLEffect> effects = youngRedDragon.getEffectObjects();

        assertEquals(9, effects.size(),
                "std:dragon/red/young should have 9 RPGLEffects"
        );
        assertEquals("std:resource/take/claw_attack", effects.get(0).getId(),
                "std:dragon/red/young should have the std:common/proficiency/save/dexterity effect"
        );
        assertEquals("std:resource/take/bite_attack", effects.get(1).getId(),
                "std:dragon/red/young should have the std:common/proficiency/save/constitution effect"
        );
        assertEquals("std:common/proficiency/save/dexterity", effects.get(2).getId(),
                "std:dragon/red/young should have the std:common/proficiency/save/dexterity effect"
        );
        assertEquals("std:common/proficiency/save/constitution", effects.get(3).getId(),
                "std:dragon/red/young should have the std:common/proficiency/save/constitution effect"
        );
        assertEquals("std:common/proficiency/save/wisdom", effects.get(4).getId(),
                "std:dragon/red/young should have the std:common/proficiency/save/wisdom effect"
        );
        assertEquals("std:common/proficiency/save/charisma", effects.get(5).getId(),
                "std:dragon/red/young should have the std:common/proficiency/save/charisma effect"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(6).getId(),
                "std:dragon/red/young should have the std:common/damage/immunity/fire effect"
        );
        assertEquals("std:common/proficiency/skill/perception", effects.get(7).getId(),
                "std:dragon/red/young should have the std:common/proficiency/skill/perception effect"
        );
        assertEquals("std:common/proficiency/skill/stealth", effects.get(8).getId(),
                "std:dragon/red/young should have the std:common/proficiency/skill/stealth effect"
        );
    }

    @Test
    @DisplayName("addRemoveEffect effects can be added and removed")
    void addRemoveEffect_effectsCanBeAddedAndRemoved() {
        RPGLObject knight = RPGLFactory.newObject("debug:dummy");
        DummyContext context = new DummyContext();
        context.add(knight);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("std:common/damage/immunity/fire");
        List<RPGLEffect> effects;

        knight.addEffect(fireImmunity);
        effects = knight.getEffectObjects();
        assertEquals(1, effects.size(),
                "std:humanoid/knight should have 1 effect"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(0).getId(),
                "std:humanoid/knight should have the std:common/damage/immunity/fire effect"
        );

        assertTrue(knight.removeEffect(fireImmunity.getUuid()),
                "the provided UUID should correspond to an effect assigned to the knight"
        );
        effects = knight.getEffectObjects();
        assertEquals(0, effects.size(),
                "std:humanoid/knight should have 0 effects"
        );
    }

    @Test
    @DisplayName("invokeEvent event behaves properly")
    void invokeEvent_eventBehavesProperly() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young");
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(youngRedDragon);
        context.add(knight);

        youngRedDragon.invokeEvent(
                new RPGLObject[] { knight },
                RPGLFactory.newEvent("std:object/dragon/red/young/breath"),
                new ArrayList<>() {{
                    this.add(youngRedDragon.getResourcesWithTag("action").get(0));
                    this.add(youngRedDragon.getResourcesWithTag("breath_attack").get(0));
                }},
                context
        );

        assertEquals(4, knight.getHealthData().getInteger("current"),
                "std:humanoid/knight should have 4 health left after failing a save against std:dragon/red/young's breath attack"
        );
        assertTrue(youngRedDragon.getResourcesWithTag("action").get(0).getExhausted(),
                "resource should be exhausted"
        );
        assertTrue(youngRedDragon.getResourcesWithTag("breath_attack").get(0).getExhausted(),
                "resource should be exhausted"
        );
    }

    @Test
    @DisplayName("receiveHealing missing hit points are restored")
    void receiveHealing_missingHitPointsAreRestored() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young");
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young");
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
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young");
        RPGLObject target = RPGLFactory.newObject("std:dragon/red/young");
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
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLResource resource = RPGLFactory.newResource("std:common/action/01");
        source.addResource(resource);

        resource.exhaust();
        source.invokeInfoSubevent(context, "start_turn");

        assertFalse(resource.getExhausted(),
                "resource should not be exhausted after turn start"
        );
    }

    @Test
    @DisplayName("getResourceObjects returns resources provided by equipped items")
    void getResourceObjects_returnsResourcesProvidedByEquippedItems() {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        RPGLItem wand = RPGLFactory.newItem("std:wand/wand_of_fireballs");
        object.giveItem(wand.getUuid());
        object.equipItem(wand.getUuid(), "mainhand");

        List<RPGLResource> resources = object.getResourceObjects();

        assertEquals(3, resources.size(),
                "commoner should have 3 resources from the wand"
        );
        for (RPGLResource resource : resources) {
            assertEquals("std:item/wand/wand_of_fireballs_charge", resource.getId(),
                    "resource should be a std:item/wand/wand_of_fireballs_charge"
            );
        }
    }

    @Test
    @DisplayName("getLevel returns correct level (with class parameter)")
    void getLevel_returnsCorrectLevel_withClassParameter() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "debug:test",
                    "name": "TEST CLASS",
                    "level": 5,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:test");
                this.putString("name", "TEST CLASS");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});

        assertEquals(5, object.getLevel("debug:test"),
                "object should have 5 levels in class debug:test"
        );
    }

    @Test
    @DisplayName("getLevel returns correct level (no parameter, no nested classes)")
    void getLevel_returnsCorrectLevel_noParameterNoNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 5,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});

        assertEquals(5, object.getLevel(),
                "object should have 5 levels overall"
        );
    }

    @Test
    @DisplayName("getLevel returns correct level (no parameter, with nested classes)")
    void getLevel_returnsCorrectLevel_noParameterWithNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "debug:test",
                    "name": "TEST CLASS",
                    "level": 5,
                    "additional_nested_classes": { }
                },
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 5,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:test");
                this.putString("name", "TEST CLASS");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});

        assertEquals(5, object.getLevel(),
                "object should have 5 levels overall (ignore the levels of the nested class)"
        );
    }

    @Test
    @DisplayName("getLevel returns correct level (no parameter, with additional nested classes)")
    void getLevel_returnsCorrectLevel_noParameterWithAdditionalNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 5,
                    "additional_nested_classes": {
                        "std:fighter/champion": {
                            "scale": 1,
                            "round_up": false
                        }
                    }
                },
                {
                    "id": "std:common/base",
                    "name": "Base",
                    "level": 5,
                    "additional_nested_classes": { }
                },
                {
                    "id": "std:fighter/champion",
                    "name": "Champion",
                    "level": 5,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject() {{
                    this.putJsonObject("std:fighter/champion", new JsonObject() {{
                        this.putInteger("scale", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:common/base");
                this.putString("name", "Base");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter/champion");
                this.putString("name", "Champion");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});

        assertEquals(5, object.getLevel(),
                "object should have 5 levels overall (ignore the levels of the nested class and the additional nested class)"
        );
    }

    @Test
    @DisplayName("calculateLevelForNestedClass calculates correct level (no additional nested classes)")
    void calculateLevelForNestedClass_calculatesCorrectLevel_noAdditionalNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 5,
                    "additional_nested_classes": { }
                },
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 3,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject() );
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 3);
                this.putJsonObject("additional_nested_classes", new JsonObject() );
            }});
        }});

        assertEquals(5+3, object.calculateLevelForNestedClass("std:common/base"),
                "object should expect to have 8 levels in std:common/base"
        );
    }

    @Test
    @DisplayName("calculateLevelForNestedClass calculates correct level (with additional nested classes)")
    void calculateLevelForNestedClass_calculatesCorrectLevel_withAdditionalNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 5,
                    "additional_nested_classes": { }
                },
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 3,
                    "additional_nested_classes": {
                        "std:common/base": {
                            "scale": 1,
                            "round_up": false
                        }
                    }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject() );
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 3);
                this.putJsonObject("additional_nested_classes", new JsonObject() {{
                    this.putJsonObject("std:common/base", new JsonObject() {{
                        this.putInteger("scale", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});

        assertEquals(5+3, object.calculateLevelForNestedClass("std:common/base"),
                "object should expect to have 8 levels in std:common/base"
        );
    }

    @Test
    @DisplayName("calculateLevelForNestedClass calculates correct level (with additional nested classes and partial scaling rounded down)")
    void calculateLevelForNestedClass_calculatesCorrectLevel_withAdditionalNestedClassesAndPartialScalingRoundedDown() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 5,
                    "additional_nested_classes": { }
                },
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 3,
                    "additional_nested_classes": {
                        "std:common/base": {
                            "scale": 2,
                            "round_up": false
                        }
                    }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject() );
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 3);
                this.putJsonObject("additional_nested_classes", new JsonObject() {{
                    this.putJsonObject("std:common/base", new JsonObject() {{
                        this.putInteger("scale", 2);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});

        assertEquals(5+1, object.calculateLevelForNestedClass("std:common/base"),
                "object should expect to have 6 levels in std:common/base"
        );
    }

    @Test
    @DisplayName("calculateLevelForNestedClass calculates correct level (with additional nested classes and partial scaling rounded up)")
    void calculateLevelForNestedClass_calculatesCorrectLevel_withAdditionalNestedClassesAndPartialScalingRoundedUp() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 5,
                    "additional_nested_classes": { }
                },
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 3,
                    "additional_nested_classes": {
                        "std:common/base": {
                            "scale": 2,
                            "round_up": true
                        }
                    }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject() );
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 3);
                this.putJsonObject("additional_nested_classes", new JsonObject() {{
                    this.putJsonObject("std:common/base", new JsonObject() {{
                        this.putInteger("scale", 2);
                        this.putBoolean("round_up", true);
                    }});
                }});
            }});
        }});

        assertEquals(5+2, object.calculateLevelForNestedClass("std:common/base"),
                "object should expect to have 7 levels in std:common/base"
        );
    }

    @Test
    @DisplayName("getNestedClassIds returns correct nested classes")
    void getNestedClassIds_returnsCorrectNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 5,
                    "additional_nested_classes": {
                        "debug:blank": {
                            "scale": 1,
                            "round_up": false
                        }
                    }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 5);
                this.putJsonObject("additional_nested_classes", new JsonObject(){{
                    this.putJsonObject("debug:blank", new JsonObject() {{
                        this.putInteger("scale", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});

        List<String> nestedClassIds = object.getNestedClassIds("std:fighter");
        assertEquals(3, nestedClassIds.size(),
                "std:fighter should have 3 nested classes for this object"
        );
        assertEquals("std:common/hit_die/d10", nestedClassIds.get(0),
                "std:fighter should have std:common/hit_die/d10 as a nested class"
        );
        assertEquals("std:common/base", nestedClassIds.get(1),
                "std:fighter should have std:common/base as a nested class"
        );
        assertEquals("debug:blank", nestedClassIds.get(2),
                "std:fighter should have debug:blank as a nested class"
        );
    }

    @Test
    @DisplayName("levelUpNestedClasses levels up nested classes correctly")
    void levelUpNestedClasses_levelsUpNestedClassesCorrectly() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 2,
                    "additional_nested_classes": {
                        "debug:blank": {
                            "scale": 1,
                            "round_up": false
                        }
                    }
                },
                {
                    "id": "std:common/base",
                    "name": "Base",
                    "level": 1,
                    "additional_nested_classes": { }
                },
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 1,
                    "additional_nested_classes": { }
                },
                {
                    "id": "std:common/spellcaster",
                    "name": "Spellcaster",
                    "level": 1,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 2);
                this.putJsonObject("additional_nested_classes", new JsonObject(){{
                    this.putJsonObject("debug:blank", new JsonObject() {{
                        this.putInteger("scale", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:common/base");
                this.putString("name", "Base");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:common/spellcaster");
                this.putString("name", "Spellcaster");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});

        object.levelUpNestedClasses("std:fighter", new JsonObject());

        assertEquals(2, object.getLevel("std:common/base"),
                "object should gain a 2nd level in class std:common/base"
        );
        assertEquals(2, object.getLevel("debug:blank"),
                "object should gain a 2nd level in class debug:blank"
        );
        assertEquals(1, object.getLevel("std:common/spellcaster"),
                "object should not gain a 2nd level in class std:common/spellcaster"
        );
    }

    @Test
    @DisplayName("levelUp levels up all classes correctly (no new classes)")
    void levelUp_levelsUpAllClassesCorrectly_noNewClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        object.setClasses(new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "name": "Fighter",
                    "level": 1,
                    "additional_nested_classes": {
                        "debug:blank": {
                            "scale": 1,
                            "round_up": false
                        }
                    }
                },
                {
                    "id": "std:common/base",
                    "name": "Base",
                    "level": 1,
                    "additional_nested_classes": { }
                },
                {
                    "id": "debug:blank",
                    "name": "BLANK CLASS",
                    "level": 1,
                    "additional_nested_classes": { }
                },
                {
                    "id": "std:common/spellcaster",
                    "name": "Spellcaster",
                    "level": 1,
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putString("name", "Fighter");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject(){{
                    this.putJsonObject("debug:blank", new JsonObject() {{
                        this.putInteger("scale", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:common/base");
                this.putString("name", "Base");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putString("name", "BLANK CLASS");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:common/spellcaster");
                this.putString("name", "Spellcaster");
                this.putInteger("level", 1);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});

        object.levelUp("std:fighter", new JsonObject());

        assertEquals(2, object.getLevel("std:fighter"),
                "object should gain a 2nd level in class std:fighter"
        );
        assertEquals(2, object.getLevel("std:common/base"),
                "object should gain a 2nd level in class std:common/base"
        );
        assertEquals(2, object.getLevel("debug:blank"),
                "object should gain a 2nd level in class debug:blank"
        );
        assertEquals(1, object.getLevel("std:common/spellcaster"),
                "object should not gain a 2nd level in class std:common/spellcaster"
        );
    }

    @Test
    @DisplayName("levelUp levels up all classes correctly (first class level)")
    void levelUp_levelsUpAllClassesCorrectly_firstClassLevel() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");

        object.levelUp("std:fighter", new JsonObject() {{
            this.putJsonArray("Skill Proficiencies", new JsonArray() {{
                this.addInteger(0);
                this.addInteger(1);
            }});
            this.putJsonArray("Fighting Style", new JsonArray() {{
                this.addInteger(0);
            }});
        }});

        assertEquals(1, object.getLevel("std:fighter"),
                "object should gain 1 level in class std:fighter"
        );
        assertEquals(1, object.getLevel("std:common/base"),
                "object should gain 1 level in class std:common/base"
        );
    }

    @Test
    @DisplayName("abilityCheck evaluates correctly")
    void abilityCheck_evaluatesCorrectly() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLContext context = new DummyContext();
        context.add(object);

        object.getAbilityScores().putInteger("str", 20);
        object.setProficiencyBonus(5);

        RPGLEffect athleticsProficiency = RPGLFactory.newEffect("std:common/proficiency/skill/athletics");
        athleticsProficiency.setSource(object);
        athleticsProficiency.setTarget(object);
        object.addEffect(athleticsProficiency);

        assertEquals(10+5+5, object.abilityCheck("str", "athletics", context),
                "ability check should return a 20 (10+5+5)"
        );
    }

}
