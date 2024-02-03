package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLObject objects. Data stored in this
 * object is copied and then processed to create a specific RPGLObject defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTemplate extends RPGLTemplate {

    public RPGLObjectTemplate() {
        super();
    }

    public RPGLObjectTemplate(JsonObject other) {
        this();
        this.join(other);
    }

    @Override
    public RPGLObject newInstance() {
        RPGLObject object = new RPGLObject();
        this.setup(object);
        UUIDTable.register(object);
        processEffects(object);
        processInventory(object);
        processEquippedItems(object);
        processResources(object);
        processClasses(object);
        return object;
    }

    @Override
    public void setup(JsonObject object) {
        super.setup(object);
        object.asMap().putIfAbsent(RPGLObjectTO.EFFECTS_ALIAS, new ArrayList<>());
        object.asMap().putIfAbsent(RPGLObjectTO.INVENTORY_ALIAS, new ArrayList<>());
        object.asMap().putIfAbsent(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, new HashMap<String, Object>());
        object.asMap().putIfAbsent(RPGLObjectTO.EVENTS_ALIAS, new ArrayList<>());
        object.asMap().putIfAbsent(RPGLObjectTO.RESOURCES_ALIAS, new ArrayList<>());
        object.asMap().putIfAbsent(RPGLObjectTO.CLASSES_ALIAS, new ArrayList<>());
        object.asMap().putIfAbsent(RPGLObjectTO.RACES_ALIAS, new ArrayList<>());
        object.asMap().putIfAbsent(RPGLObjectTO.CHALLENGE_RATING_ALIAS, 0.0);
        object.asMap().putIfAbsent(RPGLObjectTO.PROXY_ALIAS, false);
    }

    @Override
    public RPGLObjectTemplate applyBonuses(JsonArray bonuses) {
        return new RPGLObjectTemplate(super.applyBonuses(bonuses));
    }

    /**
     * This helper method converts effect IDs in an RPGLObjectTemplate's effects array to RPGLEffects. The UUID's of
     * these new RPGLEffects replace the original array contents.
     *
     * @param object an RPGLObject
     */
    static void processEffects(RPGLObject object) {
        JsonArray effectIdArray = object.getEffects();
        JsonArray effectUuidArray = new JsonArray();
        for (int i = 0; i < effectIdArray.size(); i++) {
            String effectId = effectIdArray.getString(i);
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            if (effect != null) {
                effectUuidArray.addString(
                        effect.setSource(object).setTarget(object).getUuid()
                );
            }
        }
        object.setEffects(effectUuidArray);
    }

    /**
     * This helper method processes the equipped items as defined by an RPGLObjectTemplate. All items are created,
     * loaded into UUIDTable, and then assigned to the corresponding equipment slots in the object being created and
     * are appended to the inventory list. Note that this method must be called AFTER the <code>processInventory()</code>
     * method to work properly.
     *
     * @param object a RPGLObject being created by this object
     */
    static void processEquippedItems(RPGLObject object) {
        JsonObject equippedItemIds = object.getEquippedItems();
        JsonArray inventoryUuids = object.getInventory();
        JsonObject equippedItemUuids = new JsonObject();
        for (Map.Entry<String, ?> equippedItemEntry : equippedItemIds.asMap().entrySet()) {
            String equippedItemId = equippedItemIds.getString(equippedItemEntry.getKey());
            RPGLItem item = RPGLFactory.newItem(equippedItemId);
            equippedItemUuids.putString(equippedItemEntry.getKey(), item.getUuid());
            inventoryUuids.addString(item.getUuid());
        }
        object.setEquippedItems(equippedItemUuids);
    }

    /**
     * This helper method processes the equipment as defined by an RPGLObjectTemplate. All items are created and added
     * to the inventory list. Note that this method must be called BEFORE the <code>processEquippedItems()</code>
     * method for that method to work properly.
     *
     * @param object a RPGLObject being created by this object
     */
    static void processInventory(RPGLObject object) {
        JsonArray inventoryItemIds = object.getInventory();
        JsonArray inventoryItemUuids = new JsonArray();
        for (int i = 0; i < inventoryItemIds.size(); i++) {
            String itemId = inventoryItemIds.getString(i);
            RPGLItem item = RPGLFactory.newItem(itemId);
            inventoryItemUuids.addString(item.getUuid());
        }
        object.setInventory(inventoryItemUuids);
    }

    /**
     * This helper method converts resource IDs in an RPGLObjectTemplate's resources array to RPGLResources. The UUID's
     * of these new RPGLResources replace the original array contents.
     *
     * @param object an RPGLObject
     */
    static void processResources(RPGLObject object) {
        JsonArray resources = object.getResources();
        JsonArray resourceUuids = new JsonArray();
        for (int i = 0; i < resources.size(); i++) {
            JsonObject resourceInstructions = resources.getJsonObject(i);
            int count = Objects.requireNonNullElse(resourceInstructions.getInteger("count"), 1);
            for (int j = 0; j < count; j++) {
                resourceUuids.addString(RPGLFactory.newResource(resourceInstructions.getString("resource")).getUuid());
            }
        }
        object.setResources(resourceUuids);
    }

    /**
     * This helper method processes the classes in the RPGLObjectTemplate's classes array and assigns features and
     * levels to the object under construction accordingly.
     *
     * @param object an RPGLObject
     */
    static void processClasses(RPGLObject object) {
        JsonArray classes = object.removeJsonArray(RPGLObjectTO.CLASSES_ALIAS);
        object.setClasses(new JsonArray());
        // set classes and nested classes
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classData = classes.getJsonObject(i);
            String classId = classData.getString("id");
            int level = Objects.requireNonNullElse(classData.getInteger("level"), 1);
            JsonObject choices = Objects.requireNonNullElse(classData.getJsonObject("choices"), new JsonObject());
            for (int j = 0; j < level; j++) {
                object.levelUp(classId, choices);
            }
            // re-assign additional nested classes
            JsonObject additionalNestedClasses = Objects.requireNonNullElse(classData.getJsonObject("additional_nested_classes"), new JsonObject());
            for (Map.Entry<String, ?> additionalNestedClassEntry : additionalNestedClasses.asMap().entrySet()) {
                JsonObject additionalNestedClassData = additionalNestedClasses.getJsonObject(additionalNestedClassEntry.getKey());
                object.addAdditionalNestedClass(
                        classId,
                        additionalNestedClassEntry.getKey(),
                        additionalNestedClassData.getInteger("scale"),
                        additionalNestedClassData.getBoolean("round_up")
                );
            }
            // update nested classes
            object.levelUpNestedClasses(classId, choices);
        }
    }

}
