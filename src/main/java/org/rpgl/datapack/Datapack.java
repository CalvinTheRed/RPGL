package org.rpgl.datapack;

import org.rpgl.core.RPGLClass;
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
    private final Map<String, RPGLClass> CLASSES = new HashMap<>();

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
                    case "classes" -> loadClasses(subDirectory);
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
        for (File effectFile : Objects.requireNonNull(directory.listFiles())) {
            String effectId = effectFile.getName().substring(0, effectFile.getName().indexOf('.'));
            try {
                RPGLEffectTemplate rpglEffectTemplate = JsonObject.MAPPER.readValue(effectFile, RPGLEffectTO.class).toRPGLEffectTemplate();
                rpglEffectTemplate.putString(DatapackContentTO.ID_ALIAS, this.datapackNamespace + ":" + effectId);
                this.EFFECT_TEMPLATES.put(effectId, rpglEffectTemplate);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * This method loads all event templates stored in a single directory into the object.
     *
     * @param directory a File directory for the events in a datapack
     */
    private void loadEventTemplates(File directory) {
        for (File eventFile : Objects.requireNonNull(directory.listFiles())) {
            String eventId = eventFile.getName().substring(0, eventFile.getName().indexOf('.'));
            try {
                RPGLEventTemplate rpglEventTemplate = JsonObject.MAPPER.readValue(eventFile, RPGLEventTO.class).toRPGLEventTemplate();
                rpglEventTemplate.putString(DatapackContentTO.ID_ALIAS, this.datapackNamespace + ":" + eventId);
                this.EVENT_TEMPLATES.put(eventId, rpglEventTemplate);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * This method loads all item templates stored in a single directory into the object.
     *
     * @param directory a File directory for the items in a datapack
     */
    private void loadItemTemplates(File directory) {
        for (File itemFile : Objects.requireNonNull(directory.listFiles())) {
            String itemId = itemFile.getName().substring(0, itemFile.getName().indexOf('.'));
            try {
                RPGLItemTemplate rpglItemTemplate = JsonObject.MAPPER.readValue(itemFile, RPGLItemTO.class).toRPGLItemTemplate();
                rpglItemTemplate.putString(DatapackContentTO.ID_ALIAS, this.datapackNamespace + ":" + itemId);
                this.ITEM_TEMPLATES.put(itemId, rpglItemTemplate);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * This method loads all object templates stored in a single directory into the object.
     *
     * @param directory a File directory for the objects in a datapack
     */
    private void loadObjectTemplates(File directory) {
        for (File objectFile : Objects.requireNonNull(directory.listFiles())) {
            String objectId = objectFile.getName().substring(0, objectFile.getName().indexOf('.'));
            try {
                RPGLObjectTemplate rpglObjectTemplate = JsonObject.MAPPER.readValue(objectFile, RPGLObjectTO.class).toRPGLObjectTemplate();
                rpglObjectTemplate.putString(DatapackContentTO.ID_ALIAS, this.datapackNamespace + ":" + objectId);
                this.OBJECT_TEMPLATES.put(objectId, rpglObjectTemplate);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * This method loads all resource templates stored in a single directory into the object.
     *
     * @param directory a File directory for the resources in a datapack
     */
    private void loadResourceTemplates(File directory) {
        for (File resourceFile : Objects.requireNonNull(directory.listFiles())) {
            String resourceId = resourceFile.getName().substring(0, resourceFile.getName().indexOf('.'));
            try {
                RPGLResourceTemplate rpglResourceTemplate = JsonObject.MAPPER.readValue(resourceFile, RPGLResourceTO.class).toRPGLResourceTemplate();
                rpglResourceTemplate.putString(DatapackContentTO.ID_ALIAS, this.datapackNamespace + ":" + resourceId);
                this.RESOURCE_TEMPLATES.put(resourceId, rpglResourceTemplate);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * This method loads all classes stored in a single directory into the object.
     *
     * @param directory a File directory for the classes in a datapack
     */
    private void loadClasses(File directory) {
        for (File classFile : Objects.requireNonNull(directory.listFiles())) {
            String classId = classFile.getName().substring(0, classFile.getName().indexOf('.'));
            try {
                RPGLClass rpglClass = JsonObject.MAPPER.readValue(classFile, RPGLClassTO.class).toRPGLClass();
                rpglClass.putString(DatapackContentTO.ID_ALIAS, this.datapackNamespace + ":" + classId);
                this.CLASSES.put(classId, rpglClass);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * This method returns a specified RPGLEffectTemplate object.
     *
     * @param effectId the name of an effect template stored in this datapack
     */
    public RPGLEffectTemplate getEffectTemplate(String effectId) {
        return this.EFFECT_TEMPLATES.get(effectId);
    }

    /**
     * This method returns a specified RPGLEventTemplate object.
     *
     * @param eventId the ID of an event template stored in this datapack
     */
    public RPGLEventTemplate getEventTemplate(String eventId) {
        return this.EVENT_TEMPLATES.get(eventId);
    }

    /**
     * This method returns a specified RPGLItemTemplate object.
     *
     * @param itemId the ID of an item template stored in this datapack
     */
    public RPGLItemTemplate getItemTemplate(String itemId) {
        return this.ITEM_TEMPLATES.get(itemId);
    }

    /**
     * This method returns a specified RPGLObjectTemplate object.
     *
     * @param objectId the ID of an object template stored in this datapack
     */
    public RPGLObjectTemplate getObjectTemplate(String objectId) {
        return this.OBJECT_TEMPLATES.get(objectId);
    }

    /**
     * This method returns a specified RPGLResourceTemplate object.
     *
     * @param resourceId the ID of a resource template stored in this datapack
     */
    public RPGLResourceTemplate getResourceTemplate(String resourceId) {
        return this.RESOURCE_TEMPLATES.get(resourceId);
    }

    /**
     * This method returns a specified RPGLClass object.
     *
     * @param classId the ID of a RPGLClass object stored in this datapack
     */
    public RPGLClass getClass(String classId) {
        return this.CLASSES.get(classId);
    }

}
