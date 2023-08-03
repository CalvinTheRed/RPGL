package org.rpgl.datapack;

import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.core.RPGLEventTemplate;
import org.rpgl.core.RPGLItemTemplate;
import org.rpgl.core.RPGLObjectTemplate;
import org.rpgl.core.RPGLResourceTemplate;
import org.rpgl.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Datapack.class);

    private final Map<String, RPGLEffectTemplate> EFFECT_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLEventTemplate> EVENT_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLItemTemplate> ITEM_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLObjectTemplate> OBJECT_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLResourceTemplate> RESOURCE_TEMPLATES = new HashMap<>();

    String datapackNamespace;

    /**
     * Constructor for the Datapack class. This constructor loads all data located within a single datapack and stores
     * it in the constructed object for future reference.
     *
     * @param directory a File directory for a datapack
     */
    public Datapack (File directory) {
        this.datapackNamespace = directory.getName();
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            if (subDirectory.isDirectory()) {
                switch (subDirectory.getName()) {
                    case "effects" -> loadEffectTemplates(subDirectory);
                    case "events" -> loadEventTemplates(subDirectory);
                    case "items" -> loadItemTemplates(subDirectory);
                    case "objects" -> loadObjectTemplates(subDirectory);
                    case "resources" -> loadResourceTemplates(subDirectory);
                }
            }
        }
    }

    /**
     * This method loads all effect templates stored in a single directory into the object.
     *
     * @param directory a File directory for the effects in a datapack
     */
    void loadEffectTemplates(File directory) {
        this.loadEffectTemplates("", directory);
    }

    /**
     * This helper method recursively loads all effect templates stored in a directory into the object.
     *
     * @param templateNameBase the file path leading from the base effects directory to the provided directory
     * @param directory a File directory for effects in a datapack
     */
    private void loadEffectTemplates(String templateNameBase, File directory) {
        for (File effectFile : Objects.requireNonNull(directory.listFiles())) {
            if (effectFile.isDirectory()) {
                this.loadEffectTemplates(templateNameBase + effectFile.getName() + "/", effectFile);
            } else {
                String effectId = effectFile.getName().substring(0, effectFile.getName().indexOf('.'));
                try {
                    RPGLEffectTemplate rpglEffectTemplate = JsonObject.MAPPER
                            .readValue(effectFile, RPGLEffectTO.class)
                            .toRPGLEffectTemplate();
                    rpglEffectTemplate.putString(
                            DatapackContentTO.ID_ALIAS,
                            this.datapackNamespace + ":" + templateNameBase + effectId
                    );
                    this.EFFECT_TEMPLATES.put(templateNameBase + effectId, rpglEffectTemplate);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * This method loads all event templates stored in a single directory into the object.
     *
     * @param directory a File directory for the events in a datapack
     */
    void loadEventTemplates(File directory) {
        this.loadEventTemplates("", directory);
    }

    /**
     * This helper method recursively loads all event templates stored in a directory into the object.
     *
     * @param templateNameBase the file path leading from the base events directory to the provided directory
     * @param directory a File directory for events in a datapack
     */
    private void loadEventTemplates(String templateNameBase, File directory) {
        for (File eventFile : Objects.requireNonNull(directory.listFiles())) {
            if (eventFile.isDirectory()) {
                this.loadEventTemplates(templateNameBase + eventFile.getName() + "/", eventFile);
            } else {
                String eventId = eventFile.getName().substring(0, eventFile.getName().indexOf('.'));
                try {
                    RPGLEventTemplate rpglEventTemplate = JsonObject.MAPPER
                            .readValue(eventFile, RPGLEventTO.class)
                            .toRPGLEventTemplate();
                    rpglEventTemplate.putString(
                            DatapackContentTO.ID_ALIAS,
                            this.datapackNamespace + ":" + templateNameBase + eventId
                    );
                    this.EVENT_TEMPLATES.put(templateNameBase + eventId, rpglEventTemplate);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * This method loads all item templates stored in a single directory into the object.
     *
     * @param directory a File directory for the items in a datapack
     */
    void loadItemTemplates(File directory) {
        this.loadItemTemplates("", directory);
    }

    /**
     * This helper method recursively loads all item templates stored in a directory into the object.
     *
     * @param templateNameBase the file path leading from the base items directory to the provided directory
     * @param directory a File directory for items in a datapack
     */
    private void loadItemTemplates(String templateNameBase, File directory) {
        for (File itemFile : Objects.requireNonNull(directory.listFiles())) {
            if (itemFile.isDirectory()) {
                this.loadItemTemplates(templateNameBase + itemFile.getName() + "/", itemFile);
            } else {
                String itemId = itemFile.getName().substring(0, itemFile.getName().indexOf('.'));
                try {
                    RPGLItemTemplate rpglItemTemplate = JsonObject.MAPPER
                            .readValue(itemFile, RPGLItemTO.class)
                            .toRPGLItemTemplate();
                    rpglItemTemplate.putString(
                            DatapackContentTO.ID_ALIAS,
                            this.datapackNamespace + ":" + templateNameBase + itemId
                    );
                    this.ITEM_TEMPLATES.put(templateNameBase + itemId, rpglItemTemplate);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * This method loads all object templates stored in a single directory into the object.
     *
     * @param directory a File directory for the objects in a datapack
     */
    void loadObjectTemplates(File directory) {
        this.loadObjectTemplates("", directory);
    }

    /**
     * This helper method recursively loads all object templates stored in a directory into the object.
     *
     * @param templateNameBase the file path leading from the base objects directory to the provided directory
     * @param directory a File directory for objects in a datapack
     */
    private void loadObjectTemplates(String templateNameBase, File directory) {
        for (File objectFile : Objects.requireNonNull(directory.listFiles())) {
            if (objectFile.isDirectory()) {
                this.loadObjectTemplates(templateNameBase + objectFile.getName() + "/", objectFile);
            } else {
                String objectId = objectFile.getName().substring(0, objectFile.getName().indexOf('.'));
                try {
                    RPGLObjectTemplate rpglObjectTemplate = JsonObject.MAPPER
                            .readValue(objectFile, RPGLObjectTO.class)
                            .toRPGLObjectTemplate();
                    rpglObjectTemplate.putString(
                            DatapackContentTO.ID_ALIAS,
                            this.datapackNamespace + ":" + templateNameBase + objectId
                    );
                    this.OBJECT_TEMPLATES.put(templateNameBase + objectId, rpglObjectTemplate);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * This method loads all resource templates stored in a single directory into the object.
     *
     * @param directory a File directory for the resources in a datapack
     */
    void loadResourceTemplates(File directory) {
        this.loadResourceTemplates("", directory);
    }

    /**
     * This helper method recursively loads all resource templates stored in a directory into the object.
     *
     * @param templateNameBase the file path leading from the base resources directory to the provided directory
     * @param directory a File directory for resources in a datapack
     */
    private void loadResourceTemplates(String templateNameBase, File directory) {
        for (File resourceFile : Objects.requireNonNull(directory.listFiles())) {
            if (resourceFile.isDirectory()) {
                this.loadResourceTemplates(templateNameBase + resourceFile.getName() + "/", resourceFile);
            } else {
                String resourceId = resourceFile.getName().substring(0, resourceFile.getName().indexOf('.'));
                try {
                    RPGLResourceTemplate rpglResourceTemplate = JsonObject.MAPPER
                            .readValue(resourceFile, RPGLResourceTO.class)
                            .toRPGLResourceTemplate();
                    rpglResourceTemplate.putString(
                            DatapackContentTO.ID_ALIAS,
                            this.datapackNamespace + ":" + templateNameBase + resourceId
                    );
                    this.RESOURCE_TEMPLATES.put(templateNameBase + resourceId, rpglResourceTemplate);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * This method returns a specified RPGLEffectTemplate object.
     *
     * @param effectName the name of an effect template stored in this datapack
     */
    public RPGLEffectTemplate getEffectTemplate(String effectName) {
        return this.EFFECT_TEMPLATES.get(effectName);
    }

    /**
     * This method returns a specified RPGLEventTemplate object.
     *
     * @param eventName the name of an event template stored in this datapack
     */
    public RPGLEventTemplate getEventTemplate(String eventName) {
        return this.EVENT_TEMPLATES.get(eventName);
    }

    /**
     * This method returns a specified RPGLItemTemplate object.
     *
     * @param itemName the name of an item template stored in this datapack
     */
    public RPGLItemTemplate getItemTemplate(String itemName) {
        return this.ITEM_TEMPLATES.get(itemName);
    }

    /**
     * This method returns a specified RPGLObjectTemplate object.
     *
     * @param objectName the name of an object template stored in this datapack
     */
    public RPGLObjectTemplate getObjectTemplate(String objectName) {
        return this.OBJECT_TEMPLATES.get(objectName);
    }

    /**
     * This method returns a specified RPGLResourceTemplate object.
     *
     * @param resourceName the name of a resource template stored in this datapack
     */
    public RPGLResourceTemplate getResourceTemplate(String resourceName) {
        return this.RESOURCE_TEMPLATES.get(resourceName);
    }

}
