package org.rpgl.core;

import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLEffect objects. Data stored in this
 * object is copied and then processed to create a specific RPGLEffect defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTemplate extends JsonObject {

    /**
     * Constructs a new RPGLEffect object corresponding to the contents of the RPGLEffectTemplate object. The new
     * object is registered to the UUIDTable class when it is constructed.
     *
     * @param originItem an item UUID to be stored for the new effect's origin item
     * @param resources a list of resources used to produce a new effect
     * @return a new RPGLEffect object
     */
    public RPGLEffect newInstance(String originItem, List<RPGLResource> resources) {
        RPGLEffect effect = new RPGLEffect();
        effect.join(this);
        effect.asMap().putIfAbsent(RPGLEffectTO.SCALE_ALIAS, new ArrayList<>());
        processScale(effect, resources);
        effect.setOriginItem(originItem);
        UUIDTable.register(effect);
        return effect;
    }

    /**
     * Constructs a new RPGLEffect object corresponding to the contents of the RPGLEffectTemplate object. The new
     * object is registered to the UUIDTable class when it is constructed.
     *
     * @return a new RPGLEffect object
     */
    public RPGLEffect newInstance() {
        return this.newInstance(null, List.of());
    }

    /**
     * This helper method scales the effect in accordance with the resources used to produce it, if applicable.
     *
     * @param effect an effect being constructed
     * @param resources a list of resources used to produce the passed effect
     */
    static void processScale(RPGLEffect effect, List<RPGLResource> resources) {
        JsonArray scaleArray = effect.getScale();
        for (int i = 0; i < scaleArray.size(); i++) {
            JsonObject scaleJson = scaleArray.getJsonObject(i);
            RPGLResource resource = getResourceByTags(resources, scaleJson.getJsonArray("resource_tags"));
            if (resource != null) {
                int potencyDifference = resource.getPotency() - scaleJson.getInteger("minimum_potency");
                if (potencyDifference > 0) {
                    JsonArray scaling = scaleJson.getJsonArray("scale");
                    for (int j = 0; j < scaling.size(); j++) {
                        JsonObject scalingElement = scaling.getJsonObject(j);
                        int magnitude = scalingElement.getInteger("magnitude");
                        String field = scalingElement.getString("field");
                        effect.insertInteger(field, effect.seekInteger(field) + potencyDifference * magnitude);
                    }
                }
            }
        }
    }

    /**
     * This helper method isolates a single resource according to specified resource tags from a list of resources.
     *
     * @param resources a list of resources
     * @param resourceTags a list of tags, of which a resource must contain at least one to be returned
     * @return a resource with a desired tag
     */
    static RPGLResource getResourceByTags(List<RPGLResource> resources, JsonArray resourceTags) {
        for (RPGLResource resource : resources) {
            if (resource.getTags().containsAny(resourceTags.asList())) {
                return resource;
            }
        }
        return null;
    }

}
