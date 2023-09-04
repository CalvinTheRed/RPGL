package org.rpgl.subevent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DamageCollection class.
 *
 * @author Calvin Withun
 */
public class DamageCollectionTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new DamageCollection();
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
    @DisplayName("addDamage new damage values are added")
    void addDamage_newDamageValuesAdded() {
        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", new JsonArray());
        }});

        damageCollection.addDamage(new JsonObject() {{
            /*{
                "damage_type": "fire",
                "dice": [
                    { "size": 4, "determined": [ 2 ] },
                    { "size": 4, "determined": [ 2 ] }
                ],
                "bonus": 5
            }*/
            this.putString("damage_type", "fire");
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 4);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(2);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("size", 4);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(2);
                    }});
                }});
            }});
            this.putInteger("bonus", 5);
        }});

        String expected = """
                [{"bonus":5,"damage_type":"fire","dice":[{"determined":[2],"size":4},{"determined":[2],"size":4}]}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "new damage values should be added"
        );
    }

    @Test
    @DisplayName("includesDamageType returns tue (type is present)")
    void includesDamageType_returnsTrue_typePresent() {
        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        assertTrue(damageCollection.includesDamageType("fire"),
                "includesDamageType() should return true when type is present"
        );
    }

    @Test
    @DisplayName("includesDamageType returns tue (type is present)")
    void includesDamageType_returnsFalse_typeAbsent() {
        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", new JsonArray());
        }});

        assertFalse(damageCollection.includesDamageType("fire"),
                "includesDamageType() should return false when type is absent"
        );
    }

}
