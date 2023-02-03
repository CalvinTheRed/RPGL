package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.json.JsonObject;

import java.util.Map;

public class RPGLEffectTO extends UUIDTableElementTO {

    // JSON property aliases
    public static final String SUBEVENT_FILTERS_ALIAS = "subevent_filters";
    public static final String SOURCE_ALIAS = "source";
    public static final String TARGET_ALIAS = "target";

    @JsonProperty(SUBEVENT_FILTERS_ALIAS)
    Map<String, Object> subeventFilters;
    @JsonProperty(SOURCE_ALIAS)
    String source;
    @JsonProperty(TARGET_ALIAS)
    String target;

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLEffect. Intended to be used for saving data.
     *
     * @param rpglEffect a fully instantiated RPGLEffect
     */
    public RPGLEffectTO(RPGLEffect rpglEffect) {
        super(rpglEffect);
        this.subeventFilters = rpglEffect.getJsonObject(SUBEVENT_FILTERS_ALIAS).asMap();
        this.source = rpglEffect.getSource();
        this.target = rpglEffect.getTarget();
    }

    public RPGLEffectTemplate toRPGLEffectTemplate() {
        RPGLEffectTemplate rpglEffectTemplate = new RPGLEffectTemplate();
        rpglEffectTemplate.asMap().putAll(super.getTemplateData());
        rpglEffectTemplate.putJsonObject(SUBEVENT_FILTERS_ALIAS, new JsonObject(subeventFilters));
        return rpglEffectTemplate;
    }

    public RPGLEffect toRPGLEffect() {
        RPGLEffect rpglEffect = new RPGLEffect();
        rpglEffect.putJsonObject(SUBEVENT_FILTERS_ALIAS, new JsonObject(subeventFilters));
        rpglEffect.setSource(source);
        rpglEffect.setTarget(target);
        return rpglEffect;
    }

}
