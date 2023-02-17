package org.rpgl.datapack;

import org.rpgl.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class loads datapacks into RPGL.
 *
 * @author Calvin Withun
 */
public final class DatapackLoader {

    public static final Map<String, Datapack> DATAPACKS = new HashMap<>();

    /**
     * This method loads all datapacks within a directory into RPGL. This method must be called before any datapack
     * content can be used.
     *
     * @param directory a File directory containing datapack directories
     */
    public static void loadDatapacks(File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                checkPackInfo(file);
                Datapack datapack = new Datapack(file);
                DATAPACKS.put(file.getName(), datapack);
            }
        }
    }

    /**
     * This method verifies a datapack by checking the content of its pack.info file.
     *
     * @param directory a File directory for a datapack
     *
     * @throws RuntimeException if pack.info does not exist, is formatted incorrectly, or specifies an unsupported
     */
    static void checkPackInfo(File directory) {
        try {
            DatapackInfo datapackInfo = JsonObject.MAPPER.readValue(new File(directory.getAbsolutePath() + "\\pack.info"), DatapackInfo.class);
            assert datapackInfo.version != null; // TODO this needs a better check...
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
