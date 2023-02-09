package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.json.JsonObject;

import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLEffects.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTO extends UUIDTableElementTO {

    // JSON property aliases
    public static final String SUBEVENT_FILTERS_ALIAS = "subevent_filters";
    public static final String SOURCE_ALIAS = "source";
    public static final String TARGET_ALIAS = "target";

    @JsonProperty(SUBEVENT_FILTERS_ALIAS)
    HashMap<String, Object> subeventFilters;
    @JsonProperty(SOURCE_ALIAS)
    String source;
    @JsonProperty(TARGET_ALIAS)
    String target;

    // TODO javadoc here
    @SuppressWarnings("unused")
    public RPGLEffectTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

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

    // TODO javadoc here
    public RPGLEffectTemplate toRPGLEffectTemplate() {
        RPGLEffectTemplate rpglEffectTemplate = new RPGLEffectTemplate() {{
            this.putJsonObject(SUBEVENT_FILTERS_ALIAS, new JsonObject(subeventFilters));
            // source not needed for template
            // target not needed for template
        }};
        rpglEffectTemplate.join(super.getTemplateData());
        return rpglEffectTemplate;
    }

    // TODO javadoc here
    public RPGLEffect toRPGLEffect() {
        RPGLEffect rpglEffect = new RPGLEffect() {{
            this.putJsonObject(SUBEVENT_FILTERS_ALIAS, new JsonObject(subeventFilters));
            this.setSource(source);
            this.setTarget(target);
        }};
        rpglEffect.join(super.getTemplateData());
        return rpglEffect;
    }

}
