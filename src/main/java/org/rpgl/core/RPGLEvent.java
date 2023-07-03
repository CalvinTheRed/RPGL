package org.rpgl.core;

import org.rpgl.datapack.DatapackContent;
import org.rpgl.datapack.RPGLEventTO;
import org.rpgl.exception.ResourceCountException;
import org.rpgl.exception.ResourceMismatchException;
import org.rpgl.exception.InsufficientResourcePotencyException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;

/**
 * This class represents any high-level verbs which occur in RPGL. Examples of this include actions such as casting
 * Fireball, swinging a Longsword, or taking the Dodge action.
 *
 * @author Calvin Withun
 */
public class RPGLEvent extends DatapackContent {

    /**
     * Returns the RPGLEvent's area of effect.
     *
     * @return a JsonObject representing the area of effect of the RPGLEvent
     */
    public JsonObject getAreaOfEffect() {
        return this.getJsonObject(RPGLEventTO.AREA_OF_EFFECT_ALIAS);
    }

    /**
     * Setter for area of effect.
     *
     * @param areaOfEffect a new area of effect JsonObject
     */
    public void setAreaOfEffect(JsonObject areaOfEffect) {
        this.putJsonObject(RPGLEventTO.AREA_OF_EFFECT_ALIAS, areaOfEffect);
    }

    /**
     * Returns the Subevents composing the RPGLEvent.
     *
     * @return a JsonArray containing Subevent instructions
     */
    public JsonArray getSubevents() {
        return this.getJsonArray(RPGLEventTO.SUBEVENTS_ALIAS);
    }

    /**
     * Setter for subevents.
     *
     * @param subevents a new subevents JsonArray
     */
    public void setSubevents(JsonArray subevents) {
        this.putJsonArray(RPGLEventTO.SUBEVENTS_ALIAS, subevents);
    }

    /**
     * Returns the cost of the RPGLEvent.
     *
     * @return a JsonArray containing cost information
     */
    public JsonArray getCost() {
        return this.getJsonArray(RPGLEventTO.COST_ALIAS);
    }

    /**
     * Setter for cost.
     *
     * @param cost a new cost for the RPGLEvent
     */
    public void setCost(JsonArray cost) {
        this.putJsonArray(RPGLEventTO.COST_ALIAS, cost);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * Returns the origin item UUID for the RPGLEvent if it has one.
     *
     * @return an RPGLItem UUID, or null if the event was not produced using an item.
     */
    public String getOriginItem() {
        return this.getString("origin_item");
    }

    /**
     * Sets the origin item UUID of the RPGLEvent.
     *
     * @param originItem a RPGLItem UUID
     */
    public void setOriginItem(String originItem) {
        this.putString("origin_item", originItem);
    }

    public void scale(List<RPGLResource> resources) throws ResourceCountException,
            ResourceMismatchException, InsufficientResourcePotencyException {
        if (this.doResourcesSatisfyCost(resources)) {
            JsonArray cost = this.getCost();
            for (int i = 0; i < cost.size(); i++) {
                JsonObject costElement = cost.getJsonObject(i);
                RPGLResource providedResource = resources.get(i);
                int potencyDifference = providedResource.getPotency() - costElement.getInteger("minimum_potency");
                if (potencyDifference > 0) {
                    JsonArray scaling = costElement.getJsonArray("scale");
                    for (int j = 0; j < scaling.size(); j++) {
                        JsonObject scalingElement = scaling.getJsonObject(j);
                        int magnitude = scalingElement.getInteger("magnitude");
                        String field = scalingElement.getString("field");
                        this.insertInteger(field, this.seekInteger(field) + potencyDifference * magnitude);
                    }
                }
            }
        }
    }

    public boolean doResourcesSatisfyCost(List<RPGLResource> resources) throws ResourceCountException,
            ResourceMismatchException, InsufficientResourcePotencyException {
        JsonArray cost = this.getCost();
        if (cost.size() == resources.size()) {
            for (int i = 0; i < cost.size(); i++) {
                JsonObject costElement = cost.getJsonObject(i);
                RPGLResource resource = resources.get(i);
                if (!resource.getTags().containsAny(costElement.getJsonArray("resource_tags").asList())) {
                    throw new ResourceMismatchException(costElement.getJsonArray("resource_tags"), resource.getTags());
                }
                if (resource.getPotency() < costElement.getInteger("minimum_potency")) {
                    throw new InsufficientResourcePotencyException(
                            resource.getId(), costElement.getInteger("minimum_potency"), resource.getPotency()
                    );
                }
            }
            return true;
        }
        throw new ResourceCountException(cost.size(), resources.size());
    }

}
