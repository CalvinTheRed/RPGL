package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLEventTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RPGLEventTO extends UUIDTableElementTO {

    // JSON property aliases
    public static final String AREA_OF_EFFECT_ALIAS = "area_of_effect";
    public static final String SUBEVENTS_ALIAS = "subevents";

    @JsonProperty(AREA_OF_EFFECT_ALIAS)
    HashMap<String, Object> areaOfEffect;
    @JsonProperty(SUBEVENTS_ALIAS)
    ArrayList<Object> subevents;

    public RPGLEventTO() {

    }

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLEvent. This has no intended use case at
     * the time being...
     *
     * @param rpglEvent a fully instantiated RPGLEvent
     */
    public RPGLEventTO(RPGLEvent rpglEvent) {
        super(rpglEvent);
        this.areaOfEffect = rpglEvent.getJsonObject(AREA_OF_EFFECT_ALIAS).asMap();
        this.subevents = rpglEvent.getJsonArray(SUBEVENTS_ALIAS).asList();
    }

    public RPGLEventTemplate toRPGLEventTemplate() {
        RPGLEventTemplate rpglEventTemplate = new RPGLEventTemplate() {{
            this.putJsonObject(AREA_OF_EFFECT_ALIAS, new JsonObject(areaOfEffect));
            this.putJsonArray(SUBEVENTS_ALIAS, new JsonArray(subevents));
        }};
        rpglEventTemplate.join(super.getTemplateData());
        return rpglEventTemplate;
    }

    public RPGLEvent toRPGLEvent() {
        RPGLEvent rpglEvent = new RPGLEvent() {{
            this.putJsonObject(AREA_OF_EFFECT_ALIAS, new JsonObject(areaOfEffect));
            this.putJsonArray(SUBEVENTS_ALIAS, new JsonArray(subevents));
        }};
        rpglEvent.join(super.getTemplateData());
        return rpglEvent;
    }

}
