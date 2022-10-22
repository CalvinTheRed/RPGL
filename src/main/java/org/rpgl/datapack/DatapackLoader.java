package org.rpgl.datapack;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class loads datapacks into RPGL.
 *
 * @author Calvin Withun
 */
public final class DatapackLoader {

    public static final Map<String, Datapack> DATAPACKS;

    static {
        DATAPACKS = new HashMap<>();
    }

    /**
     * This method must be called at the beginning of program execution. This is where the process of loading datapacks
     * into RPGL begins.
     */
    public static void loadDatapacks(File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                try {
                    checkPackInfo(file);
                    Datapack datapack = new Datapack(file);
                    DATAPACKS.put(file.getName(), datapack);
                } catch (RuntimeException e) {
                    // datapack failed to load
                }
            }
        }
    }

    /**
     * This method verifies a datapack by checking its pack.info file.
     *
     * @param directory a datapack directory
     * @throws RuntimeException if pack.info does not exist, is formatted incorrectly, or specifies an unsupported
     * version.
     */
    private static void checkPackInfo(File directory) {
        String namespace = directory.getName();
        String version = null;
        try {
            JsonObject infoData = JsonParser.parseObjectFile(directory.getAbsolutePath() + "\\pack.info");
            version = (String) infoData.get("version");
            assert version != null; // TODO this needs a better check...
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
    }

}
