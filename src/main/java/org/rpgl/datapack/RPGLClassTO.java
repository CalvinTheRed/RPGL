package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLClass;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RPGLClassTO extends DatapackContentTO {

    public static final String HIT_DIE_ALIAS = "hit_die";
    public static final String SUBCLASS_LEVEL_ALIAS = "subclass_level";
    public static final String ABILITY_SCORE_INCREASES_ALIAS = "ability_score_increases";
    public static final String MULTICLASSING_REQUIREMENTS_ALIAS = "multiclassing_requirements";
    public static final String NESTED_CLASSES_ALIAS = "nested_classes";
    public static final String STARTING_FEATURES_ALIAS = "starting_features";
    public static final String FEATURES_ALIAS = "features";

    @JsonProperty(HIT_DIE_ALIAS)
    Integer hitDie;
    @JsonProperty(SUBCLASS_LEVEL_ALIAS)
    Integer subclassLevel;
    @JsonProperty(ABILITY_SCORE_INCREASES_ALIAS)
    ArrayList<Object> abilityScoreIncreases;
    @JsonProperty(MULTICLASSING_REQUIREMENTS_ALIAS)
    ArrayList<Object> multiclassingRequirements;
    @JsonProperty(NESTED_CLASSES_ALIAS)
    HashMap<String, Object> classContributions;
    @JsonProperty(STARTING_FEATURES_ALIAS)
    HashMap<String, Object> startingClassFeatures;
    @JsonProperty(FEATURES_ALIAS)
    HashMap<String, Object> classFeatures;

    /**
     * Default constructor for RPGLClassTO class.
     */
    @SuppressWarnings("unused")
    public RPGLClassTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * This method translates the stored data into a RPGLClass object.
     *
     * @return a RPGLClass
     */
    public RPGLClass toRPGLClass() {
        RPGLClass rpglClass = new RPGLClass() {{
            this.putInteger(HIT_DIE_ALIAS, hitDie);
            this.putInteger(SUBCLASS_LEVEL_ALIAS, subclassLevel);
            this.putJsonArray(ABILITY_SCORE_INCREASES_ALIAS, new JsonArray(abilityScoreIncreases));
            this.putJsonArray(MULTICLASSING_REQUIREMENTS_ALIAS, new JsonArray(multiclassingRequirements));
            this.putJsonObject(NESTED_CLASSES_ALIAS, new JsonObject(classContributions));
            this.putJsonObject(STARTING_FEATURES_ALIAS, new JsonObject(startingClassFeatures));
            this.putJsonObject(FEATURES_ALIAS, new JsonObject(classFeatures));
        }};
        rpglClass.join(super.getTemplateData());
        return rpglClass;
    }

}
