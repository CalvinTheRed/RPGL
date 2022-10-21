package org.rpgl.datapack;

import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DatapackManager {

    private static final Map<String, Datapack> DATAPACKS;
    private static final File DATAPACKS_DIRECTORY;

    static {
        DATAPACKS = new HashMap<>();
        try {
            DATAPACKS_DIRECTORY = new File(Objects.requireNonNull(DatapackManager.class.getClassLoader().getResource("datapacks")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load datapacks directory.", e);
        }
    }

    public static void loadDatapacks() {
        for (File file : Objects.requireNonNull(DATAPACKS_DIRECTORY.listFiles())) {
            if (file.isDirectory()) {
                try {
                    Datapack datapack = new Datapack(file);
                    DATAPACKS.put(file.getName(), datapack);
                } catch (RuntimeException e) {
                    // datapack failed to load
                }
            }
        }
    }

    public static RPGLEffect newEffect(String effectId) {
        String[] effectIdSplit = effectId.split(":");
        return DATAPACKS.get(effectIdSplit[0]).getEffectTemplate(effectIdSplit[1]).getInstance();
    }

    public static RPGLEvent newEvent(String eventId) {
        String[] eventIdSplit = eventId.split(":");
        return DATAPACKS.get(eventIdSplit[0]).getEventTemplate(eventIdSplit[1]).getInstance();
    }

    public static RPGLItem newItem(String itemId) {
        String[] itemIdSplit = itemId.split(":");
        return DATAPACKS.get(itemIdSplit[0]).getItemTemplate(itemIdSplit[1]).getInstance();
    }

    public static RPGLObject newObject(String objectId) {
        String[] objectIdSplit = objectId.split(":");
        return DATAPACKS.get(objectIdSplit[0]).getObjectTemplate(objectIdSplit[1]).getInstance();
    }

}
