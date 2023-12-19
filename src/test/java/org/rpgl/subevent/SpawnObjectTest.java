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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Subevent subevent = new SpawnObject();
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
    @DisplayName("invoke spawns correct object and adds to context and UUIDTable")
    void invoke_spawnsCorrectObjectAndAddsToContextAndUUIDTable() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

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
    @DisplayName("invoke defaults to source user id")
    void invoke_defaultsToSourceUserId() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", "user-one");
        RPGLObject target = RPGLFactory.newObject("debug:dummy", "user-two");
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(source);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(target);
        spawnObject.invoke(context, List.of());

        assertEquals(2, UUIDTable.getObjectsByUserId(source.getUserId()).size(),
                "SpawnObject should use source's user id by default"
        );
    }

    @Test
    @DisplayName("invoke uses target user id")
    void invoke_usesTargetUserId() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", "user-one");
        RPGLObject target = RPGLFactory.newObject("debug:dummy", "user-two");
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
            this.putString("controlled_by", "target");
        }});
        spawnObject.setSource(source);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(target);
        spawnObject.invoke(context, List.of());

        assertEquals(2, UUIDTable.getObjectsByUserId(target.getUserId()).size(),
                "SpawnObject should use target's user id when specified"
        );
    }

    @Test
    @DisplayName("invoke adds extra effects")
    void invoke_addsExtraEffects() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        RPGLItem originItem = RPGLFactory.newItem("std:weapon/melee/simple/dagger");

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
            this.putJsonArray("extra_effects", new JsonArray() {{
                this.addString("std:common/damage/immunity/fire");
                this.addString("std:common/damage/immunity/poison");
            }});
        }});
        spawnObject.setOriginItem(originItem.getUuid());
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

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
    @DisplayName("invoke adds extra events")
    void invoke_addsExtraEvents() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
            this.putJsonArray("extra_events", new JsonArray() {{
                this.addString("std:spell/fire_bolt");
                this.addString("std:common/dodge");
            }});
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

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
    @DisplayName("invoke adds extra tags")
    void invoke_addsExtraTags() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
            this.putJsonArray("extra_tags", new JsonArray() {{
                this.addString("extra-tag-1");
                this.addString("extra-tag-2");
            }});
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        assertTrue(context.getContextObjects().get(0).getTags().asList().contains("extra-tag-1"),
                "new object should have an extra tag"
        );
        assertTrue(context.getContextObjects().get(0).getTags().asList().contains("extra-tag-2"),
                "new object should be given all extra tags"
        );
    }

    @Test
    @DisplayName("invoke object receives correct bonuses")
    void invoke_objectReceivesCorrectBonuses() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
            this.putJsonArray("object_bonuses", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("field", "health_data.temporary");
                    this.putInteger("bonus", 10);
                }});
            }});
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        assertEquals(10, context.getContextObjects().get(0).getHealthData().getInteger("temporary"),
                "object should receive a +10 bonus to temporary hit points"
        );
    }

    @Test
    @DisplayName("invoke does not extend proficiency bonus by default")
    void invoke_doesNotExtendProficiencyBonusByDefault() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        summoner.setProficiencyBonus(1);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        assertNotEquals(1, context.getContextObjects().get(0).getEffectiveProficiencyBonus(context),
                "new object should not extend source proficiency bonus by default"
        );
    }

    @Test
    @DisplayName("invoke extends proficiency bonus")
    void invoke_extendsProficiencyBonus() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        summoner.setProficiencyBonus(5);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
            this.putBoolean("extend_proficiency_bonus", true);
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        assertEquals(5, context.getContextObjects().get(0).getEffectiveProficiencyBonus(context),
                "new object should extend source proficiency bonus"
        );
    }

    @Test
    @DisplayName("invoke sets origin object")
    void invoke_setsOriginObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        assertEquals(summoner.getUuid(), context.getContextObjects().get(0).getOriginObject(),
                "new object should have summoner as origin object"
        );
    }

    @Test
    @DisplayName("addSpawnObjectBonus adds bonuses")
    void addSpawnObjectBonus_addsBonuses() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.addSpawnObjectBonus(new JsonObject() {{
            /*{
              "field": "health_data.temporary",
              "bonus": 10
            }*/
            this.putString("field", "health_data.temporary");
            this.putInteger("bonus", 10);
        }});

        String expected = """
                [{"bonus":10,"field":"health_data.temporary"}]""";
        assertEquals(expected, spawnObject.json.getJsonArray("object_bonuses").toString(),
                "new object should have provided bonus"
        );
    }

    @Test
    @DisplayName("addSpawnObjectTags adds effect")
    void addSpawnObjectEffect_addsEffect() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.addSpawnObjectEffect("std:common/damage/immunity/fire");

        String expected = """
                ["std:common/damage/immunity/fire"]""";
        assertEquals(expected, spawnObject.json.getJsonArray("extra_effects").toString(),
                "new object should have provided effect"
        );
    }

    @Test
    @DisplayName("addSpawnObjectEvent adds event")
    void addSpawnObjectEvent_addsEvent() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.addSpawnObjectEvent("std:spell/fire_bolt");

        String expected = """
                ["std:spell/fire_bolt"]""";
        assertEquals(expected, spawnObject.json.getJsonArray("extra_events").toString(),
                "new object should have provided event"
        );
    }

    @Test
    @DisplayName("addSpawnObjectTags adds tags")
    void addSpawnObjectTag_addsTag() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "debug:dummy");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.addSpawnObjectTag("test-tag");

        String expected = """
                ["test-tag"]""";
        assertEquals(expected, spawnObject.json.getJsonArray("extra_tags").toString(),
                "new object should have provided tag"
        );
    }

}
