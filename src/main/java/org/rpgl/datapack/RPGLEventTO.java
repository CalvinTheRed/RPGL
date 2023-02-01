package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLEventTemplate;

import java.util.List;
import java.util.Map;

public class RPGLEventTO extends UUIDTableElementTO {

    // JSON property aliases
    public static final String AREA_OF_EFFECT_ALIAS = "area_of_effect";
    public static final String SUBEVENTS_ALIAS = "subevents";

    @JsonProperty(AREA_OF_EFFECT_ALIAS)
    Map<String, Object> areaOfEffect;
    @JsonProperty(SUBEVENTS_ALIAS)
    List<Object> subevents;

    public RPGLEventTemplate toRPGLEventTemplate() {
        RPGLEventTemplate rpglEventTemplate = new RPGLEventTemplate();
        rpglEventTemplate.putAll(super.getTemplateData());
        rpglEventTemplate.put(AREA_OF_EFFECT_ALIAS, areaOfEffect);
        rpglEventTemplate.put(SUBEVENTS_ALIAS, subevents);
        return rpglEventTemplate;
    }

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLEvent. This has no intended use case at
     * the time being...
     *
     * @param rpglEvent a fully instantiated RPGLEvent
     */
    public RPGLEventTO(RPGLEvent rpglEvent) {
        super(rpglEvent);
        this.areaOfEffect = (Map<String, Object>) rpglEvent.get(AREA_OF_EFFECT_ALIAS);
        this.subevents = (List<Object>) rpglEvent.get(SUBEVENTS_ALIAS);
    }

    public RPGLEffect toRPGLEvent() {
        RPGLEffect rpglEffect = new RPGLEffect();
        rpglEffect.put(AREA_OF_EFFECT_ALIAS, areaOfEffect);
        rpglEffect.put(SUBEVENTS_ALIAS, subevents);
        return rpglEffect;
    }

}
