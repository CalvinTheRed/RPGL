package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLResource;
import org.rpgl.core.RPGLResourceTemplate;
import org.rpgl.json.JsonArray;

import java.util.ArrayList;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLResources.
 *
 * @author Calvin Withun
 */
public class RPGLResourceTO extends RPGLTaggableTO {

    // JSON property aliases
    public static final String POTENCY_ALIAS = "potency";
    public static final String EXHAUSTED_ALIAS = "exhausted";
    public static final String REFRESH_CRITERION_ALIAS = "refresh_criterion";
    public static final String ORIGIN_ITEM_ALIAS = "origin_item";

    @JsonProperty(POTENCY_ALIAS)
    Integer potency;
    @JsonProperty(EXHAUSTED_ALIAS)
    Boolean exhausted;
    @JsonProperty(REFRESH_CRITERION_ALIAS)
    ArrayList<Object> refreshCriterion;
    @JsonProperty(ORIGIN_ITEM_ALIAS)
    String originItem;

    /**
     * Default constructor for RPGLResourceTO class.
     */
    @SuppressWarnings("unused")
    public RPGLResourceTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLResource. Intended to be used for saving
     * data.
     *
     * @param rpglResource a fully instantiated RPGLResource
     */
    public RPGLResourceTO(RPGLResource rpglResource) {
        super(rpglResource);
        this.potency = rpglResource.getPotency();
        this.exhausted = rpglResource.getExhausted();
        this.refreshCriterion = rpglResource.getRefreshCriterion().asList();
        this.originItem = rpglResource.getOriginItem();
    }

    /**
     * This method translates the stored data into a RPGLResourceTemplate object.
     *
     * @return a RPGLResourceTemplate
     */
    public RPGLResourceTemplate toRPGLResourceTemplate() {
        RPGLResourceTemplate rpglResourceTemplate = new RPGLResourceTemplate() {{
            this.putInteger(POTENCY_ALIAS, potency);
            this.putBoolean(EXHAUSTED_ALIAS, exhausted);
            this.putJsonArray(REFRESH_CRITERION_ALIAS, new JsonArray(refreshCriterion));
            //origin item is not needed for template
        }};
        rpglResourceTemplate.join(super.getTemplateData());
        return rpglResourceTemplate;
    }

    /**
     * This method translates the stored data into a RPGLResource object.
     *
     * @return a RPGLResource
     */
    public RPGLResource toRPGLResource() {
        RPGLResource rpglResource = new RPGLResource() {{
            this.setPotency(potency);
            this.setExhausted(exhausted);
            this.setRefreshCriterion(new JsonArray(refreshCriterion));
            this.setOriginItem(originItem);
        }};
        rpglResource.join(super.getTemplateData());
        rpglResource.join(super.getUUIDTableElementData());
        return rpglResource;
    }

}
