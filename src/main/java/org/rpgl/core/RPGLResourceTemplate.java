package org.rpgl.core;

import org.rpgl.datapack.RPGLResourceTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLResource objects. Data stored in this
 * object is copied and then processed to create a specific RPGLResource defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLResourceTemplate extends JsonObject {

    private static final JsonObject DEFAULT_REQUIRED_GENERATOR = new JsonObject() {{
        this.putJsonArray("dice", new JsonArray());
        this.putInteger("bonus", 1);
    }};

    private static final JsonArray DEFAULT_REFRESH_CRITERION = new JsonArray() {{
       this.addJsonObject(new JsonObject() {{
           this.putString("subevent", "info_subevent");
           this.putJsonArray("tags", new JsonArray() {{
               this.addString("start_turn");
           }});
       }});
    }};

    /**
     * Constructs a new RPGLResource object corresponding to the contents of the RPGLResourceTemplate object. The new
     * object is registered to the UUIDTable class when it is constructed.
     *
     * @param originItem an item UUID to be stored for the new resource's origin item
     * @return a new RPGLResource object
     */
    public RPGLResource newInstance(String originItem) {
        RPGLResource resource = new RPGLResource();
        resource.join(this);
        resource.setOriginItem(originItem);
        resource.asMap().putIfAbsent(RPGLResourceTO.POTENCY_ALIAS, 1);
        resource.asMap().putIfAbsent(RPGLResourceTO.EXHAUSTED_ALIAS, false);
        if (resource.getRefreshCriterion().asList().isEmpty()) {
            resource.setRefreshCriterion(DEFAULT_REFRESH_CRITERION.deepClone());
        }
        UUIDTable.register(resource);
        processRefreshCriterion(resource);
        processRefreshCriterionGenerators(resource);
        return resource;
    }

    /**
     * Constructs a new RPGLResource object corresponding to the contents of the RPGLResourceTemplate object. The new
     * object is registered to the UUIDTable class when it is constructed.
     *
     * @return a new RPGLResource object
     */
    public RPGLResource newInstance() {
        return this.newInstance(null);
    }

    /**
     * This helper method sets default values for a resource's refresh criteria.
     *
     * @param resource a RPGLResource
     */
    static void processRefreshCriterion(RPGLResource resource) {
        JsonArray refreshCriterion = resource.getRefreshCriterion();
        for (int i = 0; i < refreshCriterion.size(); i++) {
            JsonObject criterion = refreshCriterion.getJsonObject(i);
            criterion.asMap().putIfAbsent("actor", "source");
            criterion.asMap().putIfAbsent("chance", 100);
            criterion.asMap().putIfAbsent("required", 0);
            criterion.asMap().putIfAbsent("required_generator", DEFAULT_REQUIRED_GENERATOR.deepClone().asMap());
            criterion.asMap().putIfAbsent("completed", 0);
        }
    }

    /**
     * This helper method unpacks all dice in the required generators for a resource's refresh criteria.
     *
     * @param resource a RPGLResource
     */
    static void processRefreshCriterionGenerators(RPGLResource resource) {
        JsonArray refreshCriterion = resource.getRefreshCriterion();
        for (int i = 0; i < refreshCriterion.size(); i++) {
            JsonObject requiredGenerator = refreshCriterion.getJsonObject(i).getJsonObject("required_generator");
            JsonArray dice = Die.unpack(requiredGenerator.removeJsonArray("dice"));
            requiredGenerator.putJsonArray("dice", dice);
        }
    }

}
