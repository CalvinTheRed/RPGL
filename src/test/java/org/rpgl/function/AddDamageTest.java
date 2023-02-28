package org.rpgl.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddDamageTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @AfterEach
    void afterEach() {
        DummySubevent.resetCounter();
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

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, null, functionJson, context),
                "AddDamage function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("unpackCompactedDamageDice returns damage with unpacked damage dice")
    void unpackCompactedDamageDice_returnsDamageWithUnpackedDamageDice() {
        AddDamage addDamage = new AddDamage();
        JsonArray damage = new JsonArray() {{
            /*[
                {
                    "type": "fire",
                    "dice": [
                        { "count": 2, "size": 6, "determined": [ 3 ] },
                        { "count": 2, "size": 4, "determined": [ 2 ] },
                    ]
                },{
                    "type": "cold",
                    "dice": [
                        { "count": 1, "size": 10, "determined": [ 5 ] }
                    ]
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("type", "fire");
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("count", 2);
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("count", 2);
                        this.putInteger("size", 4);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(2);
                        }});
                    }});
                }});
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("type", "cold");
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("count", 1);
                        this.putInteger("size", 10);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(5);
                        }});
                    }});
                }});
            }});
        }};

        String expected = """
                [{"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[2],"size":4},{"determined":[2],"size":4}],"type":"fire"},{"dice":[{"determined":[5],"size":10}],"type":"cold"}]""";
        assertEquals(expected, addDamage.unpackCompactedDamageDice(damage).toString(),
                "calling unpackCompactedDamageDice should unpack the compacted damage dice passed to it"
        );
    }

}
