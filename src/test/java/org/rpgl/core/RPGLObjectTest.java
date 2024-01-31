package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.function.DummyFunction;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingDelivery;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        DummyFunction.resetCounter();
    }

    @Test
    @DisplayName("equips item (item in inventory)")
    void equipsItem_itemInInventory() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        assertEquals(item.getUuid(), object.getEquippedItems().getString("mainhand"),
                "object should have item equipped in mainhand"
        );
    }

    @Test
    @DisplayName("refuses to equip item (item not in inventory)")
    void refusesToEquipItem_itemNotInInventory() {
        RPGLObject knight = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        String daggerUuid = RPGLFactory.newItem("std:weapon/melee/simple/dagger").getUuid();
        knight.equipItem(daggerUuid, "mainhand");

        assertNotEquals(daggerUuid, knight.getEquippedItems().getString("mainhand"),
                "dagger should not be equipped in the mainhand slot"
        );
    }

    @Test
    @DisplayName("adds item to inventory")
    void addsItemToInventory() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        String daggerUuid = RPGLFactory.newItem("std:weapon/melee/simple/dagger").getUuid();
        object.giveItem(daggerUuid);

        assertTrue(object.getInventory().asList().contains(daggerUuid),
                "object should have item in inventory"
        );
    }

    @Test
    @DisplayName("does not add duplicate items to inventory")
    void doesNotAddDuplicateItemsToInventory() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/simple/dagger");
        object.giveItem(item.getUuid());
        object.giveItem(item.getUuid());

        assertEquals(1, object.getInventory().size(),
                "object should not add a duplicate item to inventory"
        );
    }

    @Test
    @DisplayName("calculates base armor class")
    void calculatesBaseArmorClass() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(knight);

        assertEquals(20, knight.getBaseArmorClass(context),
                "std:humanoid/knight should have 20 AC"
        );
    }

    @Test
    @DisplayName("loses hit points")
    void losesHitPoints() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.reduceHitPoints(10, new DummyContext());

        assertEquals(1000 /*base*/ -10 /*lost hit points*/, object.getHealthData().getInteger("current"),
                "object should lose 10 hit points"
        );
    }

    @Test
    @DisplayName("deducts temporary hit points before hit points")
    void deductsTemporaryHitPointsBeforeHitPoints() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getHealthData().putInteger("temporary", 10);

        object.reduceHitPoints(5, new DummyContext());
        assertEquals(10 /*base*/ -5 /*damage*/, object.getHealthData().getInteger("temporary"),
                "object should lose 5 temporary hit points"
        );

        object.reduceHitPoints(10, new DummyContext());
        assertEquals(0, object.getHealthData().getInteger("temporary"),
                "object should lose 5 temporary hit points"
        );
        assertEquals(1000 /*base*/ -5 /*overflow damage*/, object.getHealthData().getInteger("current"),
                "object should lose 5 hit points"
        );
    }

    @Test
    @DisplayName("gets ability score modifier from ability score")
    void getsAbilityScoreModifierFromAbilityScore() {
        assertEquals(-2, RPGLObject.getAbilityModifierFromAbilityScore( 7));
        assertEquals(-1, RPGLObject.getAbilityModifierFromAbilityScore( 8));
        assertEquals(-1, RPGLObject.getAbilityModifierFromAbilityScore( 9));
        assertEquals( 0, RPGLObject.getAbilityModifierFromAbilityScore(10));
        assertEquals( 0, RPGLObject.getAbilityModifierFromAbilityScore(11));
        assertEquals( 1, RPGLObject.getAbilityModifierFromAbilityScore(12));
        assertEquals( 1, RPGLObject.getAbilityModifierFromAbilityScore(13));
        assertEquals( 2, RPGLObject.getAbilityModifierFromAbilityScore(14));
    }

    @Test
    @DisplayName("gets ability score modifier from ability name")
    void getsAbilityScoreModifierFromAbilityName() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        assertEquals(5, object.getAbilityModifierFromAbilityName("str", new DummyContext()),
                "object should have str modifier of +5"
        );
    }

    @Test
    @DisplayName("gets proficiency bonus")
    void getsProficiencyBonus() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.setProficiencyBonus(5);

        assertEquals(5, object.getEffectiveProficiencyBonus(new DummyContext()),
                "object should have proficiency bonus of +5"
        );
    }

    @Test
    @DisplayName("gets events")
    void getsEvents() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.giveEvent("std:spell/fire_bolt");
        object.giveEvent("std:common/dodge");

        List<RPGLEvent> events = object.getEventObjects(new DummyContext());

        assertEquals(2, events.size(),
                "object should have 4 events"
        );
        assertEquals("std:spell/fire_bolt", events.get(0).getId(),
                "object should have the std:spell/fire_bolt event"
        );
        assertEquals("std:common/dodge", events.get(1).getId(),
                "object should have the std:common/dodge event"
        );
    }

    @Test
    @DisplayName("gets effects")
    void getsEffects() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.addEffect(RPGLFactory.newEffect("std:common/damage/immunity/fire"));
        object.addEffect(RPGLFactory.newEffect("std:common/damage/immunity/poison"));

        List<RPGLEffect> effects = object.getEffectObjects();

        assertEquals(2, effects.size(),
                "object should have 2 effects"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(0).getId(),
                "object should have the std:common/damage/immunity/fire effect"
        );
        assertEquals("std:common/damage/immunity/poison", effects.get(1).getId(),
                "object should have the std:common/damage/immunity/poison effect"
        );
    }

    @Test
    @DisplayName("adds and removes effects")
    void addsAndRemovesEffects() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("std:common/damage/immunity/fire");
        List<RPGLEffect> effects;

        object.addEffect(fireImmunity);
        effects = object.getEffectObjects();
        assertEquals(1, effects.size(),
                "object should have 1 effect"
        );
        assertEquals("std:common/damage/immunity/fire", effects.get(0).getId(),
                "object should have the std:common/damage/immunity/fire effect"
        );

        assertTrue(object.removeEffect(fireImmunity.getUuid()),
                "the provided UUID should correspond to an effect assigned to the object"
        );
        effects = object.getEffectObjects();
        assertEquals(0, effects.size(),
                "object should have 0 effects"
        );
    }

    @Test
    @DisplayName("invokes events")
    void invokesEvents() throws Exception {
        RPGLObject youngRedDragon = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        youngRedDragon.invokeEvent(
                new RPGLObject[] { target },
                RPGLFactory.newEvent("std:object/dragon/red/young/breath"),
                new ArrayList<>() {{
                    this.add(youngRedDragon.getResourcesWithTag("action").get(0));
                    this.add(youngRedDragon.getResourcesWithTag("breath_attack").get(0));
                }},
                new DummyContext()
        );

        assertEquals(1000 /*base*/ -(16*3) /*damage*/, target.getHealthData().getInteger("current"),
                "target should take 48 (16d6) damage from breath attack"
        );
        assertTrue(youngRedDragon.getResourcesWithTag("action").get(0).getExhausted(),
                "resource should be exhausted"
        );
        assertTrue(youngRedDragon.getResourcesWithTag("breath_attack").get(0).getExhausted(),
                "resource should be exhausted"
        );
    }

    @Test
    @DisplayName("heals")
    void heals() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getHealthData().putInteger("current", 100);

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

        healingDelivery.setSource(object);
        healingDelivery.prepare(new DummyContext());
        healingDelivery.setTarget(object);
        healingDelivery.invoke(new DummyContext());

        object.receiveHealing(healingDelivery, new DummyContext());

        assertEquals(100 /*base*/ +10 /*healing*/, object.getHealthData().getInteger("current"),
                "target should recover 10 hit points"
        );
    }

    @Test
    @DisplayName("does not heal beyond hit point maximum")
    void doesNotHealBeyondHitPointMaximum() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getHealthData().putInteger("current", 995);

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

        healingDelivery.setSource(object);
        healingDelivery.prepare(new DummyContext());
        healingDelivery.setTarget(object);
        healingDelivery.invoke(new DummyContext());

        object.receiveHealing(healingDelivery, new DummyContext());

        assertEquals(1000, object.getHealthData().getInteger("current"),
                "target should only recover its missing hit points"
        );
    }

    @Test
    @DisplayName("refreshes resources")
    void refreshesResources() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:test", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(object);

        RPGLResource resource = RPGLFactory.newResource("std:common/action/01");
        resource.exhaust();

        object.addResource(resource);
        object.invokeInfoSubevent(context, "start_turn");

        assertFalse(resource.getExhausted(),
                "resource should not be exhausted after turn start"
        );
    }

    @Test
    @DisplayName("has resources granted by equipped items")
    void hasResourcesGrantedByEquippedItems() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLItem item = RPGLFactory.newItem("std:wand/wand_of_fireballs");
        object.giveItem(item.getUuid());
        object.equipItem(item.getUuid(), "mainhand");

        List<RPGLResource> resources = object.getResourceObjects();

        assertEquals(3, resources.size(),
                "object should have 3 resources from item"
        );
        for (RPGLResource resource : resources) {
            assertEquals("std:item/wand/wand_of_fireballs_charge", resource.getId(),
                    "resource should be a std:item/wand/wand_of_fireballs_charge"
            );
        }
    }

    @Test
    @DisplayName("does not have proxy resources by default")
    void doesNotHaveProxyResourcesByDefault() {
        RPGLObject originObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.setOriginObject(originObject.getUuid());
        object.setProxy(false);

        originObject.addResource(RPGLFactory.newResource("std:common/spell_slot/01"));

        assertEquals(0, object.getResourceObjects().size(),
                "object should not have access to origin object's resources"
        );
    }

    @Test
    @DisplayName("has proxy resources")
    void hasProxyResources() {
        RPGLObject originObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.setOriginObject(originObject.getUuid());
        object.setProxy(true);

        originObject.addResource(RPGLFactory.newResource("std:common/spell_slot/01"));

        assertEquals(1, object.getResourceObjects().size(),
                "object should have access to origin object's resources"
        );
    }

    @Test
    @DisplayName("invokes event as proxy object")
    void invokesEventAsProxyObject() throws Exception {
        RPGLObject originObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject proxyObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(originObject);
        context.add(proxyObject);

        // add effect to origin object to detect when it invokes an InfoSubevent
        RPGLEffect detectorEffect = RPGLFactory.newEffect("debug:detect_target_invokes_info_subevent");
        detectorEffect.setSource(originObject);
        detectorEffect.setTarget(originObject);
        originObject.addEffect(detectorEffect);

        // set proxy object data
        proxyObject.setOriginObject(originObject.getUuid());
        proxyObject.setProxy(true);

        proxyObject.invokeEvent(
                new RPGLObject[] { proxyObject },
                RPGLFactory.newEvent("debug:test_info_subevent"),
                List.of(),
                context
        );

        assertEquals(1, DummyFunction.counter,
                "DummyFunction counter should be incremented due to origin object being the source of the proxy event"
        );
    }

    @Test
    @DisplayName("does not invoke event as proxy object by default")
    void doesNotInvokeEventAsProxyObjectByDefault() throws Exception {
        RPGLObject originObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject proxyObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(originObject);
        context.add(proxyObject);

        // add effect to origin object to detect when it invokes an InfoSubevent
        RPGLEffect detectorEffect = RPGLFactory.newEffect("debug:detect_target_invokes_info_subevent");
        detectorEffect.setSource(originObject);
        detectorEffect.setTarget(originObject);
        originObject.addEffect(detectorEffect);

        // set proxy object data (proxy defaults to false)
        proxyObject.setOriginObject(originObject.getUuid());

        proxyObject.invokeEvent(
                new RPGLObject[] { proxyObject },
                RPGLFactory.newEvent("debug:test_info_subevent"),
                List.of(),
                context
        );

        assertEquals(0, DummyFunction.counter,
                "DummyFunction counter should not be incremented when proxy is false"
        );
    }

    @Test
    @DisplayName("gets class level")
    void getsClassLevel() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("gets total level")
    void getsTotalLevel() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
                "object should be level 5"
        );
    }

    @Test
    @DisplayName("does not include nested class levels in total level")
    void doesNotIncludeNestedClassLevelsInTotalLevel() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
                "object should be level 5 (ignore the levels of the nested class and the additional nested class)"
        );
    }

    @Test
    @DisplayName("calculates nested class level")
    void calculatesNestedClassLevel() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
                "object should have 8 levels in std:common/base between both Fighter classes"
        );
    }

    @Test
    @DisplayName("calculates level of class which is nested and additionally nested")
    void calculatesLevelOfClassWhichIsNestedAndAdditionallyNested() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
                "object should have 8 levels in std:common/base"
        );
    }

    @Test
    @DisplayName("calculates nested class level (with level scaling) (rounded down)")
    void calculatesNestedClassLevel_withLevelScaling_roundedDown() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
                "object should have 6 levels in std:common/base"
        );
    }

    @Test
    @DisplayName("calculates nested class level (with level scaling) (rounded up)")
    void calculatesNestedClassLevel_withLevelScaling_roundedUp() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("gets nested class IDs")
    void getsNestedClassIds() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("levels up nested classes")
    void levelsUpNestedClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("levels up existing classes")
    void levelsUpExistingClasses() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
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
    @DisplayName("levels up new class")
    void levelsUpNewClass() {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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
    @DisplayName("makes ability checks")
    void makesAbilityChecks() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);
        RPGLContext context = new DummyContext();
        context.add(object);

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
