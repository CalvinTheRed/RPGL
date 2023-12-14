package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingCollection;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddHealing class.
 *
 * @author Calvin Withun
 */
public class AddHealingTest {

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
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context, List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent (range)")
    void execute_addsHealingToSubevent_range() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count": 1, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 2
                    }
                ]
           }*/
            this.putString("function", "add_healing");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);

        addHealing.execute(effect, healingCollection, functionJson, context, List.of());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[3],"size":6}]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add healing to HealingCollection subevent"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent (modifier)")
    void execute_addsHealingToSubevent_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": [
                    {
                        "formula": "modifier",
                        "ability": "dex",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_healing");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "modifier");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);

        addHealing.execute(effect, healingCollection, functionJson, context, List.of());

        String expected = """
                [{"bonus":5,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add source's dex modifier to HealingCollection subevent"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent (ability)")
    void execute_addsHealingToSubevent_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("dex", 20);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": [
                    {
                        "formula": "ability",
                        "ability": "dex",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_healing");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "ability");
                    this.putString("ability", "dex");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);

        addHealing.execute(effect, healingCollection, functionJson, context, List.of());

        String expected = """
                [{"bonus":20,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add source's dex score to HealingCollection subevent"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent (proficiency)")
    void execute_addsHealingToSubevent_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": [
                    {
                        "formula": "proficiency",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_healing");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "proficiency");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);

        addHealing.execute(effect, healingCollection, functionJson, context, List.of());

        String expected = """
                [{"bonus":2,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add source's proficiency modifier to HealingCollection subevent"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent (level with specified class)")
    void execute_addsHealingToSubevent_levelWithSpecifiedClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": [
                    {
                        "formula": "level",
                        "class": "std:common/base",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_healing");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "level");
                    this.putString("class", "std:common/base");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);

        addHealing.execute(effect, healingCollection, functionJson, context, List.of());

        String expected = """
                [{"bonus":1,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add source's level to HealingCollection subevent"
        );
    }

    @Test
    @DisplayName("execute adds healing to subevent (level without specified class)")
    void execute_addsHealingToSubevent_levelWithoutSpecifiedClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        HealingCollection healingCollection = new HealingCollection();
        healingCollection.setSource(source);
        healingCollection.prepare(context, List.of());

        AddHealing addHealing = new AddHealing();
        JsonObject functionJson = new JsonObject() {{
           /*{
                "function": "add_healing",
                "healing": [
                    {
                        "formula": "level",
                        "object": {
                            "from": "effect",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_healing");
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "level");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "effect");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(source);
        effect.setTarget(target);

        addHealing.execute(effect, healingCollection, functionJson, context, List.of());

        String expected = """
                [{"bonus":9,"dice":[]}]""";
        assertEquals(expected, healingCollection.getHealingCollection().toString(),
                "execute should add source's level to HealingCollection subevent"
        );
    }
}
