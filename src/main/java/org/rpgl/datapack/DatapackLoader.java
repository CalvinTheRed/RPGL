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
     * 	<p><b><i>loadDatapacks</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static void loadDatapacks(File directory)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method loads all datapacks within a directory into RPGL. This method must be called before any datapack
     * 	content can be used.
     * 	</p>
     *
     * 	@param directory a File directory containing datapack directories
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
     * 	<p><b><i>checkPackInfo</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void checkPackInfo(File directory)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method verifies a datapack by checking the content of its pack.info file.
     * 	</p>
     *
     *  @param directory a File directory for a datapack
     *  @throws RuntimeException if pack.info does not exist, is formatted incorrectly, or specifies an unsupported
     */
    static void checkPackInfo(File directory) {
        String namespace = directory.getName();
        String version = null;
        try {
            JsonObject infoData = JsonParser.parseObjectFile(directory.getAbsolutePath() + "\\pack.info");
            version = (String) infoData.get("version");
            assert version != null; // TODO this needs a better check...
        } catch (JsonFormatException | FileNotFoundException e) {
            throw new RuntimeException(String.format(
                    "datapack %s is missing a pack.info file or it is formatted incorrectly",
                    namespace
            ), e);
        } catch (AssertionError e) {
            throw new RuntimeException(String.format(
                    "datapack %s is not supported (version %s)",
                    namespace,
                    version
            ), e);
        }
    }

}
