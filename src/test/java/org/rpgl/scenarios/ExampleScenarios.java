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

public class ExampleScenarios {

    private RPGLObject leatherArmorDummy;
    private RPGLObject chainShirtDummy;

    private RPGLObject dragon;

    private RPGLObject victim_1;
    private RPGLObject victim_2;
    private RPGLObject victim_3;
    private RPGLObject victim_4;
    private RPGLObject victim_5;
    private RPGLObject victim_6;

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
                "str": 16,
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

    private static final String VICTIM_STATS = """
            {
              "metadata": {
                "author": "Calvin Withun"
              },
              "name": "Woe Unto Me",
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

    private static final String DRAGON_STATS = """
            {
              "metadata": {
                "author": "Calvin Withun"
              },
              "name": "DRAGOOOOON!!!",
              "ability_scores": {
                "str": 12,
                "dex": 12,
                "con": 21,
                "int": 12,
                "wis": 12,
                "cha": 12
              },
              "health_data": {
                "base": 178,
                "max": 178,
                "current": 178,
                "temporary": 0
              },
              "proficiency_bonus": 4
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

        dragon = RPGLFactory.newObject("dummy:blank");
        assert dragon != null;
        dragon.join(JsonParser.parseObjectString(DRAGON_STATS));

        victim_1 = RPGLFactory.newObject("dummy:blank");
        assert victim_1 != null;
        victim_1.join(JsonParser.parseObjectString(VICTIM_STATS));

        victim_2 = RPGLFactory.newObject("dummy:blank");
        assert victim_2 != null;
        victim_2.join(JsonParser.parseObjectString(VICTIM_STATS));

        victim_3 = RPGLFactory.newObject("dummy:blank");
        assert victim_3 != null;
        victim_3.join(JsonParser.parseObjectString(VICTIM_STATS));

        victim_4 = RPGLFactory.newObject("dummy:blank");
        assert victim_4 != null;
        victim_4.join(JsonParser.parseObjectString(VICTIM_STATS));

        victim_5 = RPGLFactory.newObject("dummy:blank");
        assert victim_5 != null;
        victim_5.join(JsonParser.parseObjectString(VICTIM_STATS));

        victim_6 = RPGLFactory.newObject("dummy:blank");
        assert victim_6 != null;
        victim_6.join(JsonParser.parseObjectString(VICTIM_STATS));
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

        // CHAIN SHIRT DUMMY ATTACKS

        System.out.println("Chain Shirt AC: " + chainShirtDummy.getBaseArmorClass(context));

        System.out.println("Chain armor HP (initial): " + chainShirtDummy.seek("health_data.current")); // 25

        leatherArmorDummy.invokeEvent(
                new RPGLObject[] {chainShirtDummy},
                Objects.requireNonNull(RPGLFactory.newEvent("demo:acid_splash")),
                context
        );

        System.out.println("Chain armor HP (after acid splash): " + chainShirtDummy.seek("health_data.current")); // 25-([3]+0)])=22

        leatherArmorDummy.invokeEvent(
                new RPGLObject[] {chainShirtDummy},
                Objects.requireNonNull(RPGLFactory.newEvent("demo:fire_bolt")),
                context
        );

        System.out.println("Chain armor HP (after fire bolt): " + chainShirtDummy.seek("health_data.current")); // 22-([5]+0)])=17

        leatherArmorDummy.invokeEvent(
                new RPGLObject[] {chainShirtDummy},
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );

        System.out.println("Chain armor HP (after hand_1 attack): " + chainShirtDummy.seek("health_data.current")); // 17-([2]+1)])=14

        // LEATHER ARMOR DUMMY ATTACKS

        // Set the dagger to attack using STR instead of the default DEX for melee attacks
        dagger_2.setAttackAbility("melee", "str");
        System.out.println("Dagger melee attack uses " + dagger_2.getAttackAbility("melee") + "!");

        System.out.println("Leather armor HP (initial): " + leatherArmorDummy.seek("health_data.current")); // 25

        chainShirtDummy.invokeEvent(
                new RPGLObject[] {leatherArmorDummy},
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );

        System.out.println("Leather armor HP (after hand_1 attack): " + leatherArmorDummy.seek("health_data.current")); // 25-([2]+3)])=20

    }

    @Test
    void test2() throws Exception {
        RPGLContext context = new RPGLContext();
        context.add(dragon);
        context.add(victim_1);
        context.add(victim_2);
        context.add(victim_3);
        context.add(victim_4);
        context.add(victim_5);
        context.add(victim_6);

        dragon.invokeEvent(
                new RPGLObject[] {victim_1, victim_2, victim_3, victim_4, victim_5, victim_6},
                Objects.requireNonNull(RPGLFactory.newEvent("demo:young_red_dragon_breath")),
                context
        );

        // Legion of Melted Steel (below)

        System.out.println("Victim 1's charred corpse: " + victim_1.seek("health_data.current")); // 25-([3x16]+0)=-23
        System.out.println("Victim 2's charred corpse: " + victim_2.seek("health_data.current")); // 25-([3x16]+0)=-23
        System.out.println("Victim 3's charred corpse: " + victim_3.seek("health_data.current")); // 25-([3x16]+0)=-23
        System.out.println("Victim 4's charred corpse: " + victim_4.seek("health_data.current")); // 25-([3x16]+0)=-23
        System.out.println("Victim 5's charred corpse: " + victim_5.seek("health_data.current")); // 25-([3x16]+0)=-23
        System.out.println("Victim 6's charred corpse: " + victim_6.seek("health_data.current")); // 25-([3x16]+0)=-23
    }

}
