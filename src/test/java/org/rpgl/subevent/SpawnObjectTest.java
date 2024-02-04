package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.SpawnObject class.
 *
 * @author Calvin Withun
 */
public class SpawnObjectTest {

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
        Subevent subevent = new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "not_a_subevent"
                    }*/
                    this.putString("subevent", "not_a_subevent");
                }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("spawns new object")
    void spawnsNewObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertEquals(1, context.getContextObjects().size(),
                "There should be 1 new object in context following spawn"
        );

        for (RPGLObject object : context.getContextObjects()) {
            assertNotNull(UUIDTable.getObject(object.getUuid()),
                    "UUIDTable should contain context object " + object.getName()
            );
        }
    }

    @Test
    @DisplayName("defaults to source user id")
    void defaultsToSourceUserId() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", "user-one");
        RPGLObject target = RPGLFactory.newObject("debug:dummy", "user-two");
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                }})
                .setSource(source)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(target)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertEquals(2, UUIDTable.getObjectsByUserId(source.getUserId()).size(),
                "SpawnObject should use source's user id by default"
        );
    }

    @Test
    @DisplayName("uses target user id")
    void usesTargetUserId() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", "user-one");
        RPGLObject target = RPGLFactory.newObject("debug:dummy", "user-two");
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putString("controlled_by", "target");
                }})
                .setSource(source)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(target)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertEquals(2, UUIDTable.getObjectsByUserId(target.getUserId()).size(),
                "SpawnObject should use target's user id when specified"
        );
    }

    @Test
    @DisplayName("adds extra effects")
    void addsExtraEffects() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        RPGLItem originItem = RPGLFactory.newItem("std:weapon/melee/simple/dagger");

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putJsonArray("extra_effects", new JsonArray() {{
                        this.addString("std:common/damage/immunity/fire");
                        this.addString("std:common/damage/immunity/poison");
                    }});
                }})
                .setOriginItem(originItem.getUuid())
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        RPGLObject spawnedObject = context.getContextObjects().get(0);
        RPGLEffect effect;

        assertEquals(2, spawnedObject.getEffects().size(),
                "new object should have 2 effects"
        );

        effect = UUIDTable.getEffect(spawnedObject.getEffects().getString(0));
        assertEquals("std:common/damage/immunity/fire", effect.getId(),
                "new effect has the wrong id"
        );
        assertEquals(summoner, effect.getSource(),
                "effect source should be the summoner"
        );
        assertEquals(spawnedObject, effect.getTarget(),
                "effect target should be the spawned object"
        );
        assertEquals(originItem.getUuid(), effect.getOriginItem(),
                "effect should share origin item with SpawnObject subevent"
        );

        effect = UUIDTable.getEffect(spawnedObject.getEffects().getString(1));
        assertEquals("std:common/damage/immunity/poison", effect.getId(),
                "new effect has the wrong id"
        );
        assertEquals(summoner, effect.getSource(),
                "effect source should be the summoner"
        );
        assertEquals(spawnedObject, effect.getTarget(),
                "effect target should be the spawned object"
        );
        assertEquals(originItem.getUuid(), effect.getOriginItem(),
                "effect should share origin item with SpawnObject subevent"
        );
    }

    @Test
    @DisplayName("adds extra events")
    void addsExtraEvents() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putJsonArray("extra_events", new JsonArray() {{
                        this.addString("std:spell/fire_bolt");
                        this.addString("std:common/dodge");
                    }});
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        JsonArray events = context.getContextObjects().get(0).getEvents();

        assertEquals(2, events.size(),
                "new object should have 2 events"
        );
        assertEquals("std:spell/fire_bolt", events.getString(0),
                "new object missing event"
        );
        assertEquals("std:common/dodge", events.getString(1),
                "new object missing event"
        );
    }

    @Test
    @DisplayName("adds extra object tags")
    void addsExtraObjectTags() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putJsonArray("extra_tags", new JsonArray() {{
                        this.addString("extra-tag-1");
                        this.addString("extra-tag-2");
                    }});
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertTrue(context.getContextObjects().get(0).getTags().asList().contains("extra-tag-1"),
                "new object should have an extra tag"
        );
        assertTrue(context.getContextObjects().get(0).getTags().asList().contains("extra-tag-2"),
                "new object should be given all extra tags"
        );
    }

    @Test
    @DisplayName("applies bonuses to object")
    void appliesBonusesToObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putJsonArray("object_bonuses", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("field", "health_data.temporary");
                            this.putInteger("bonus", 10);
                        }});
                    }});
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertEquals(10, context.getContextObjects().get(0).getHealthData().getInteger("temporary"),
                "object should receive a +10 bonus to temporary hit points"
        );
    }

    @Test
    @DisplayName("assigns object default proficiency bonus")
    void assignsObjectDefaultProficiencyBonus() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(1);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertNotEquals(1, context.getContextObjects().get(0).getEffectiveProficiencyBonus(context),
                "new object should not extend source proficiency bonus by default"
        );
    }

    @Test
    @DisplayName("extends origin object proficiency bonus")
    void extendsOriginObjectProficiencyBonus() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(5);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putBoolean("extend_proficiency_bonus", true);
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertEquals(5, context.getContextObjects().get(0).getEffectiveProficiencyBonus(context),
                "new object should extend source proficiency bonus"
        );
    }

    @Test
    @DisplayName("sets origin object")
    void setsOriginObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertEquals(summoner.getUuid(), context.getContextObjects().get(0).getOriginObject(),
                "new object should have summoner as origin object"
        );
    }

    @Test
    @DisplayName("does not spawn proxy object by default")
    void doesNotSpawnProxyObjectByDefault() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertFalse(context.getContextObjects().get(0).getProxy(),
                "new object should not be a proxy object"
        );
    }

    @Test
    @DisplayName("spawns proxy object")
    void spawnsProxyObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        new SpawnObject()
                .joinSubeventData(new JsonObject() {{
                    this.putString("object_id", "debug:dummy");
                    this.putBoolean("proxy", true);
                }})
                .setSource(summoner)
                .prepare(context, TestUtils.TEST_ARRAY_10_10_10)
                .setTarget(summoner)
                .invoke(context, TestUtils.TEST_ARRAY_10_10_10);

        assertTrue(context.getContextObjects().get(0).getProxy(),
                "new object should be a proxy object"
        );
    }

}
