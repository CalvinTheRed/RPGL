package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLEventTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLEvents.
 *
 * @author Calvin Withun
 */
public class RPGLEventTO extends UUIDTableElementTO {

    // JSON property aliases
    public static final String AREA_OF_EFFECT_ALIAS = "area_of_effect";
    public static final String SUBEVENTS_ALIAS = "subevents";

    @JsonProperty(AREA_OF_EFFECT_ALIAS)
    HashMap<String, Object> areaOfEffect;
    @JsonProperty(SUBEVENTS_ALIAS)
    ArrayList<Object> subevents;

    // TODO javadoc here
    @SuppressWarnings("unused")
    public RPGLEventTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    // TODO javadoc here
    public RPGLEventTemplate toRPGLEventTemplate() {
        RPGLEventTemplate rpglEventTemplate = new RPGLEventTemplate() {{
            this.putJsonObject(AREA_OF_EFFECT_ALIAS, new JsonObject(areaOfEffect));
            this.putJsonArray(SUBEVENTS_ALIAS, new JsonArray(subevents));
        }};
        rpglEventTemplate.join(super.getTemplateData());
        return rpglEventTemplate;
    }

    // TODO javadoc here
    public RPGLEvent toRPGLEvent() {
        RPGLEvent rpglEvent = new RPGLEvent() {{
            this.putJsonObject(AREA_OF_EFFECT_ALIAS, new JsonObject(areaOfEffect));
            this.putJsonArray(SUBEVENTS_ALIAS, new JsonArray(subevents));
        }};
        rpglEvent.join(super.getTemplateData());
        return rpglEvent;
    }

}
