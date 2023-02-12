package org.rpgl.subevent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for class CriticalHitDamageCollection.
 *
 * @author Calvin Withun
 */
public class CriticalHitDamageCollectionTest {

    @Test
    @DisplayName("doubleDice number of dice in the damage collection are doubled")
    void doubleDice_numberOfDiceIsDoubled() {
        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "critical_hit_damage_collection",
                "damage": [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 1
                    },{
                        "type": "cold",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putString("subevent", "critical_hit_damage_collection");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});
        criticalHitDamageCollection.doubleDice();

        String expected = """
                [{"bonus":1,"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6}],"type":"fire"},{"bonus":1,"dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}],"type":"cold"}]""";
        assertEquals(expected, criticalHitDamageCollection.getDamageCollection().toString(),
                "the number of dice should be doubled after calling doubleDice()"
        );
    }

}
