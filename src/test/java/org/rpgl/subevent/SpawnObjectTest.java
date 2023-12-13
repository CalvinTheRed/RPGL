package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLFactory;
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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
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
        RPGLObject summoner = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(summoner);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        assertEquals(2, context.getContextObjects().size(),
                "There should be 2 context objects in context following spawn"
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
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", "user-one");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", "user-two");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
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
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", "user-one");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", "user-two");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
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
    @DisplayName("invoke adds extra tags")
    void invoke_addsExtraTags() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(summoner);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
            this.putJsonArray("extra_tags", new JsonArray() {{
                this.addString("extra-tag-1");
                this.addString("extra-tag-2");
            }});
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        context.remove(summoner);

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
        context.add(summoner);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:summon/summon_undead/skeletal");
            this.putJsonArray("object_bonuses", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("field", "classes[0].level");
                    this.putInteger("bonus", 1);
                }});
            }});
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        context.remove(summoner);

        assertEquals(4, context.getContextObjects().get(0).getLevel("std:summon/summon_undead"),
                "object should receive a +1 bonus to level"
        );
    }

    @Test
    @DisplayName("invoke does not extend proficiency bonus by default")
    void invoke_doesNotExtendProficiencyBonusByDefault() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(summoner);

        summoner.setProficiencyBonus(1);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        context.remove(summoner);

        assertNotEquals(1, context.getContextObjects().get(0).getEffectiveProficiencyBonus(context),
                "new object should not extend source proficiency bonus by default"
        );
    }

    @Test
    @DisplayName("invoke extends proficiency bonus")
    void invoke_extendsProficiencyBonus() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(summoner);

        summoner.setProficiencyBonus(1);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
            this.putBoolean("extend_proficiency_bonus", true);
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        context.remove(summoner);

        assertEquals(1, context.getContextObjects().get(0).getEffectiveProficiencyBonus(context),
                "new object should extend source proficiency bonus"
        );
    }

    @Test
    @DisplayName("invoke sets origin object")
    void invoke_setsOriginObject() throws Exception {
        RPGLObject summoner = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(summoner);

        SpawnObject spawnObject = new SpawnObject();
        spawnObject.joinSubeventData(new JsonObject() {{
            this.putString("object_id", "std:dragon/red/young");
        }});
        spawnObject.setSource(summoner);
        spawnObject.prepare(context, List.of());
        spawnObject.setTarget(summoner);
        spawnObject.invoke(context, List.of());

        context.remove(summoner);

        assertEquals(summoner.getUuid(), context.getContextObjects().get(0).getOriginObject(),
                "new object should have summoner as origin object"
        );
    }

    // TODO unit test needed for the as_origin field of an object specifier object: { from..., object..., as_origin... }

}
