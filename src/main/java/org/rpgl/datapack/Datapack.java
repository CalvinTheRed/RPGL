package org.rpgl.datapack;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
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

public class Datapack {

    private final Map<String, RPGLEffectTemplate> EFFECT_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLEventTemplate>  EVENT_TEMPLATES  = new HashMap<>();
    private final Map<String, RPGLItemTemplate>   ITEM_TEMPLATES   = new HashMap<>();
    private final Map<String, RPGLObjectTemplate> OBJECT_TEMPLATES = new HashMap<>();

    public Datapack (File directory) {
        String namespace = directory.getName();

        // verify pack.info file exists and indicates a supported version
        String version = null;
        try {
            JsonObject infoData = JsonParser.parseObjectFile(directory.getAbsolutePath() + "\\pack.info");
            version = (String) infoData.get("version");
            assert version != null;
        } catch (JsonFormatException | FileNotFoundException e) {
            throw new RuntimeException(String.format(
                    "datapack {} is missing a pack.info file or it is formatted incorrectly",
                    namespace
            ), e);
        } catch (AssertionError e) {
            throw new RuntimeException(String.format(
                    "datapack {} is not supported (version {})",
                    namespace,
                    version
            ), e);
        }

        // load templates for each data type
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

    public RPGLEffectTemplate getEffectTemplate(String effectName) {
        return EFFECT_TEMPLATES.get(effectName);
    }

    public RPGLEventTemplate getEventTemplate(String eventName) {
        return EVENT_TEMPLATES.get(eventName);
    }

    public RPGLItemTemplate getItemTemplate(String itemName) {
        return ITEM_TEMPLATES.get(itemName);
    }

    public RPGLObjectTemplate getObjectTemplate(String objectName) {
        return OBJECT_TEMPLATES.get(objectName);
    }

}
