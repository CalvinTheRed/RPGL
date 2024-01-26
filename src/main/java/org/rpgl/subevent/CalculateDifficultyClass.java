package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.function.AddBonus;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This subevent is dedicated to calculating the save difficulty class against which saving throws are made.
 * <br>
 * <br>
 * Source: the RPGLObject whose save difficulty class is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateDifficultyClass extends Calculation {

    public CalculateDifficultyClass() {
        super("calculate_difficulty_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateDifficultyClass();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateDifficultyClass();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        Integer difficultyClass = this.json.getInteger("difficulty_class");
        if (difficultyClass == null) {
            super.setBase(8);
            new AddBonus().execute(null, this, new JsonObject() {{
                /*{
                    "function": "add_bonus",
                    "bonus": [
                        {
                            "formula": "proficiency",
                            "object": {
                                "from": "subevent",
                                "object": "source"
                            }
                        },
                        {
                            "formula": "modifier",
                            "ability": <difficulty_class_ability>
                            "object": {
                                "from": "subevent",
                                "object": "source"
                            }
                        }
                    ]
                }*/
                this.putString("function", "add_bonus");
                this.putJsonArray("bonus", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "proficiency");
                        this.putJsonObject("object", new JsonObject() {{
                            this.putString("from", "subevent");
                            this.putString("object", "source");
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putString("formula", "modifier");
                        this.putString("ability", json.getString("difficulty_class_ability"));
                        this.putJsonObject("object", new JsonObject() {{
                            this.putString("from", "subevent");
                            this.putString("object", "source");
                        }});
                    }});
                }});
            }}, context);
        } else {
            super.setBase(difficultyClass);
        }
    }

}
