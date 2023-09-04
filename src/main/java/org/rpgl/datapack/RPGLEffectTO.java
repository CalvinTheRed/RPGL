package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLEffects.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTO extends RPGLTaggableTO {

    // JSON property aliases
    public static final String SUBEVENT_FILTERS_ALIAS = "subevent_filters";
    public static final String SOURCE_ALIAS = "source";
    public static final String TARGET_ALIAS = "target";
    public static final String ORIGIN_ITEM_ALIAS = "origin_item";
    public static final String SCALE_ALIAS = "scale";

    @JsonProperty(SUBEVENT_FILTERS_ALIAS)
    HashMap<String, Object> subeventFilters;
    @JsonProperty(SOURCE_ALIAS)
    String source;
    @JsonProperty(TARGET_ALIAS)
    String target;
    @JsonProperty(ORIGIN_ITEM_ALIAS)
    String originItem;
    @JsonProperty(SCALE_ALIAS)
    ArrayList<Object> scale;

    /**
     * Default constructor for RPGLEffectTO class.
     */
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
        this.subeventFilters = rpglEffect.getSubeventFilters().asMap();
        this.source = rpglEffect.getSource().getUuid();
        this.target = rpglEffect.getTarget().getUuid();
        this.originItem = rpglEffect.getOriginItem();
        this.scale = rpglEffect.getScale().asList();
    }

    /**
     * This method translates the stored data into a RPGLEffectTemplate object.
     *
     * @return a RPGLEffectTemplate
     */
    public RPGLEffectTemplate toRPGLEffectTemplate() {
        RPGLEffectTemplate rpglEffectTemplate = new RPGLEffectTemplate() {{
            this.putJsonObject(SUBEVENT_FILTERS_ALIAS, new JsonObject(subeventFilters));
            this.putJsonArray(SCALE_ALIAS, new JsonArray(scale));
            // source not needed for template
            // target not needed for template
            // origin item not needed for template
        }};
        rpglEffectTemplate.join(super.getTemplateData());
        return rpglEffectTemplate;
    }

    /**
     * This method translates the stored data into a RPGLEffect object.
     *
     * @return a RPGLEffect
     */
    public RPGLEffect toRPGLEffect() {
        RPGLEffect rpglEffect = new RPGLEffect() {{
            this.setSubeventFilters(new JsonObject(subeventFilters));
            this.putString(SOURCE_ALIAS, source);
            this.putString(TARGET_ALIAS, target);
            this.setOriginItem(originItem);
            this.setScale(new JsonArray(scale));
        }};
        rpglEffect.join(super.getTemplateData());
        rpglEffect.join(super.getUUIDTableElementData());
        return rpglEffect;
    }

}
