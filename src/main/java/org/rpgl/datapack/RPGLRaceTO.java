package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLRace;
import org.rpgl.json.JsonObject;

import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLRaces.
 *
 * @author Calvin Withun
 */
public class RPGLRaceTO extends DatapackContentTO {

    public static final String ABILITY_SCORE_INCREASES_ALIAS = "ability_score_increases";
    public static final String FEATURES_ALIAS = "features";

    @JsonProperty(ABILITY_SCORE_INCREASES_ALIAS)
    HashMap<String, Object> abilityScoreIncreases;
    @JsonProperty(FEATURES_ALIAS)
    HashMap<String, Object> classFeatures;

    /**
     * Default constructor for RPGLRaceTO class.
     */
    @SuppressWarnings("unused")
    public RPGLRaceTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * This method translates the stored data into a RPGLRace object.
     *
     * @return a RPGLRace
     */
    public RPGLRace toRPGLRace() {
        RPGLRace race = new RPGLRace() {{
            this.putJsonObject(ABILITY_SCORE_INCREASES_ALIAS, new JsonObject(abilityScoreIncreases));
            this.putJsonObject(FEATURES_ALIAS, new JsonObject(classFeatures));
        }};
        race.join(super.getTemplateData());
        return race;
    }

}
