package org.rpgl.scenarios;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.math.Die;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

public class HappyFunTimesTest {

    private RPGLObject leatherArmorDummy;
    private RPGLObject chainShirtDummy;

    private static final String LEATHER_ARMOR_DUMMY_JSON_STRING = """
            {
              "metadata": {
                "author": "Calvin Withun"
              },
              "name": "Studded Leather Dummy",
              "ability_scores": {
                "str": 12,
                "dex": 12,
                "con": 12,
                "int": 12,
                "wis": 12,
                "cha": 12
              },
              "health_data": {
                "base": 25,
                "max": 25,
                "current": 25,
                "temporary": 0
              },
              "proficiency_bonus": 2
            }
            """;

    private static final String CHAIN_SHIRT_DUMMY_JSON_STRING = """
            {
              "metadata": {
                "author": "Calvin Withun"
              },
              "name": "Chain Mail Dummy",
              "ability_scores": {
                "str": 12,
                "dex": 12,
                "con": 12,
                "int": 12,
                "wis": 12,
                "cha": 12
              },
              "health_data": {
                "base": 25,
                "max": 25,
                "current": 25,
                "temporary": 0
              },
              "proficiency_bonus": 2
            }
            """;

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        Die.setTesting(true);
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @BeforeEach
    void beforeEach() throws JsonFormatException {
        leatherArmorDummy = RPGLFactory.newObject("dummy:blank");
        assert leatherArmorDummy != null;
        leatherArmorDummy.join(JsonParser.parseObjectString(LEATHER_ARMOR_DUMMY_JSON_STRING));

        chainShirtDummy = RPGLFactory.newObject("dummy:blank");
        assert chainShirtDummy != null;
        chainShirtDummy.join(JsonParser.parseObjectString(CHAIN_SHIRT_DUMMY_JSON_STRING));
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
    }

    @Test
    void test() throws Exception {

        RPGLItem dagger_1 = RPGLFactory.newItem("demo:dagger");
        RPGLItem dagger_2 = RPGLFactory.newItem("demo:dagger");
        RPGLItem shield = RPGLFactory.newItem("demo:shield");
        RPGLItem leatherArmor = RPGLFactory.newItem("demo:leather_armor");
        RPGLItem chainShirt = RPGLFactory.newItem("demo:chain_shirt");
        assert dagger_1 != null;
        assert dagger_2 != null;
        assert shield != null;
        assert leatherArmor != null;
        assert chainShirt != null;

        RPGLContext context = new RPGLContext();
        context.add(leatherArmorDummy);
        context.add(chainShirtDummy);

        leatherArmorDummy.giveItem((String) leatherArmor.get("uuid"));
        leatherArmorDummy.giveItem((String) dagger_1.get("uuid"));
        chainShirtDummy.giveItem((String) chainShirt.get("uuid"));
        chainShirtDummy.giveItem((String) dagger_2.get("uuid"));
        chainShirtDummy.giveItem((String) shield.get("uuid"));

        leatherArmorDummy.equipItem((String) leatherArmor.get("uuid"), "armor");
        leatherArmorDummy.equipItem((String) dagger_1.get("uuid"), "hand_1");
        chainShirtDummy.equipItem((String) chainShirt.get("uuid"), "armor");
        chainShirtDummy.equipItem((String) dagger_2.get("uuid"), "hand_1");
        chainShirtDummy.equipItem((String) shield.get("uuid"), "hand_2");

        System.out.println("Chain armor HP (before): " + chainShirtDummy.seek("health_data.current")); // 25

        leatherArmorDummy.invokeEvent(
                new RPGLObject[] {chainShirtDummy},
                Objects.requireNonNull(RPGLFactory.newEvent("demo:acid_splash")),
                context
        );

        System.out.println("Chain armor HP (after): " + chainShirtDummy.seek("health_data.current")); // 25-([3]+0)])=22

    }

}
