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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddDamage class.
 *
 * @author Calvin Withun
 */
public class AddDamageTest {

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to collection (range)")
    void execute_addsCorrectBonusToCollection_range() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "damage_formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "count": 1, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 2
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
                    this.putString("damage_type", "fire");
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
        effect.setName("TEST");

        addDamage.execute(effect, damageCollection, functionJson, context);
        String expected = """
        [{"bonus":2,"damage_type":"fire","dice":[{"determined":[3],"size":6}]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage range to collection"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to collection (modifier)")
    void execute_addsCorrectBonusToCollection_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 20);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "damage_formula": "modifier",
                        "ability": "str",
                        "damage_type": "fire",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "modifier");
                    this.putString("ability", "str");
                    this.putString("damage_type", "fire");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        addDamage.execute(effect, damageCollection, functionJson, context);
        String expected = """
        [{"bonus":5,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage modifier to collection"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to collection (ability)")
    void execute_addsCorrectBonusToCollection_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 20);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "damage_formula": "ability",
                        "ability": "str",
                        "damage_type": "fire",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "ability");
                    this.putString("ability", "str");
                    this.putString("damage_type", "fire");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        addDamage.execute(effect, damageCollection, functionJson, context);
        String expected = """
        [{"bonus":20,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage ability to collection"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to collection (proficiency)")
    void execute_addsCorrectBonusToCollection_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.setProficiencyBonus(3);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "damage_formula": "proficiency",
                        "damage_type": "fire",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "proficiency");
                    this.putString("damage_type", "fire");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        addDamage.execute(effect, damageCollection, functionJson, context);
        String expected = """
        [{"bonus":3,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage proficiency to collection"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to collection (level with specified class)")
    void execute_addsCorrectBonusToCollection_levelWithSpecifiedClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "damage_formula": "level",
                        "class": "std:common/base",
                        "damage_type": "fire",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "level");
                    this.putString("class", "std:common/base");
                    this.putString("damage_type", "fire");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        addDamage.execute(effect, damageCollection, functionJson, context);
        String expected = """
        [{"bonus":1,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage level to collection"
        );
    }

    @Test
    @DisplayName("execute adds correct bonus to collection (level without specified class)")
    void execute_addsCorrectBonusToCollection_levelWithoutSpecifiedClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(source);
        damageCollection.prepare(context);

        AddDamage addDamage = new AddDamage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_damage",
                "damage": [
                    {
                        "damage_formula": "level",
                        "damage_type": "fire",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
           }*/
            this.putString("function", "add_damage");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "level");
                    this.putString("damage_type", "fire");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }};

        RPGLEffect effect = new RPGLEffect();
        effect.setName("TEST");

        addDamage.execute(effect, damageCollection, functionJson, context);
        String expected = """
        [{"bonus":9,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "execute should add appropriate damage level to collection"
        );
    }
}
