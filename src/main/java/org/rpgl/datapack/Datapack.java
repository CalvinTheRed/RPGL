package org.rpgl.datapack;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.core.RPGLEventTemplate;
import org.rpgl.core.RPGLItemTemplate;
import org.rpgl.core.RPGLObjectTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a datapack, a collection of customizable data used to load novel content into RPGL for the user
 * to use.
 *
 * @author Calvin Withun
 */
public class Datapack {

    private final Map<String, RPGLEffectTemplate> EFFECT_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLEventTemplate>  EVENT_TEMPLATES  = new HashMap<>();
    private final Map<String, RPGLItemTemplate>   ITEM_TEMPLATES   = new HashMap<>();
    private final Map<String, RPGLObjectTemplate> OBJECT_TEMPLATES = new HashMap<>();

    /**
     * Constructor for the Datapack class. This constructor loads all data located within a single datapack and stores
     * it in the constructed object for future reference.
     *
     * @param directory a datapack directory
     */
    public Datapack (File directory) {
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            if (subDirectory.isDirectory()) {
                switch (subDirectory.getName()) {
                    case "effects" -> loadEffectTemplates(subDirectory);
                    case "events"  -> loadEventTemplates(subDirectory);
                    case "items"   -> loadItemTemplates(subDirectory);
                    case "objects" -> loadObjectTemplates(subDirectory);
                }
            }
        }
    }

    /**
     * This method loads all effect templates stored in a single directory into the object.
     *
     * @param directory an effects directory
     */
    private void loadEffectTemplates(File directory) {
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            String effectId = subDirectory.getName().substring(0, subDirectory.getName().indexOf('.'));
            try {
                RPGLEffectTemplate effectTemplate = new RPGLEffectTemplate(JsonParser.parseObjectFile(directory));
                EFFECT_TEMPLATES.put(effectId, effectTemplate);
            } catch (JsonFormatException | FileNotFoundException e) {
//                throw new RuntimeException(String.format(
//                        "encountered an error trying to load effect {}:{}",
//                        NAMESPACE,
//                        effectId
//                ), e);
            }
        }
    }

    /**
     * This method loads all event templates stored in a single directory into the object.
     *
     * @param directory an events directory
     */
    private void loadEventTemplates(File directory) {
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            String eventId = subDirectory.getName().substring(0, subDirectory.getName().indexOf('.'));
            try {
                RPGLEventTemplate eventTemplate = new RPGLEventTemplate(JsonParser.parseObjectFile(directory));
                EVENT_TEMPLATES.put(eventId, eventTemplate);
            } catch (JsonFormatException | FileNotFoundException e) {
//                throw new RuntimeException(String.format(
//                        "encountered an error trying to load event {}:{}",
//                        NAMESPACE,
//                        eventId
//                ), e);
            }
        }
    }

    /**
     * This method loads all item templates stored in a single directory into the object.
     *
     * @param directory an items directory
     */
    private void loadItemTemplates(File directory) {
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            String itemId = subDirectory.getName().substring(0, subDirectory.getName().indexOf('.'));
            try {
                RPGLItemTemplate itemTemplate = new RPGLItemTemplate(JsonParser.parseObjectFile(directory));
                ITEM_TEMPLATES.put(itemId, itemTemplate);
            } catch (JsonFormatException | FileNotFoundException e) {
//                throw new RuntimeException(String.format(
//                        "encountered an error trying to load item {}:{}",
//                        NAMESPACE,
//                        itemId
//                ), e);
            }
        }
    }

    /**
     * This method loads all object templates stored in a single directory into the object.
     *
     * @param directory an objects directory
     */
    private void loadObjectTemplates(File directory) {
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            String objectId = subDirectory.getName().substring(0, subDirectory.getName().indexOf('.'));
            try {
                RPGLObjectTemplate objectTemplate = new RPGLObjectTemplate(JsonParser.parseObjectFile(directory));
                OBJECT_TEMPLATES.put(objectId, objectTemplate);
            } catch (JsonFormatException | FileNotFoundException e) {
//                throw new RuntimeException(String.format(
//                        "encountered an error trying to load object {}:{}",
//                        NAMESPACE,
//                        objectId
//                ), e);
            }
        }
    }

    /**
     * This method returns a specified RPGLEffectTemplate object.
     *
     * @param effectName an RPGLEffectTemplate name
     * @return an RPGLEffectTemplate object
     */
    public RPGLEffectTemplate getEffectTemplate(String effectName) {
        return EFFECT_TEMPLATES.get(effectName);
    }

    /**
     * This method returns a specified RPGLEventTemplate object.
     *
     * @param eventName an RPGLEventTemplate name
     * @return an RPGLEventTemplate object
     */
    public RPGLEventTemplate getEventTemplate(String eventName) {
        return EVENT_TEMPLATES.get(eventName);
    }

    /**
     * This method returns a specified RPGLItemTemplate object.
     *
     * @param itemName an RPGLItemTemplate name
     * @return an RPGLItemTemplate object
     */
    public RPGLItemTemplate getItemTemplate(String itemName) {
        return ITEM_TEMPLATES.get(itemName);
    }

    /**
     * This method returns a specified RPGLObjectTemplate object.
     *
     * @param objectName an RPGLObjectTemplate name
     * @return an RPGLObjectTemplate object
     */
    public RPGLObjectTemplate getObjectTemplate(String objectName) {
        return OBJECT_TEMPLATES.get(objectName);
    }

}
