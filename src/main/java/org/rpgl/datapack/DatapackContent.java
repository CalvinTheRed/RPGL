package org.rpgl.datapack;

import org.rpgl.json.JsonObject;

/**
 * This class represents any content loaded from a datapack into RPGL.
 *
 * @author Calvin Withun
 */
public class DatapackContent extends JsonObject {

    /**
     * Returns the metadata of the DatapackContent.
     *
     * @return a JsonObject storing the DatapackContent's metadata
     */
    public JsonObject getMetadata() {
        return this.getJsonObject(DatapackContentTO.METADATA_ALIAS);
    }

    /**
     * Returns the name of the DatapackContent.
     *
     * @return the name of the DatapackContent
     */
    public String getName() {
        return this.getString(DatapackContentTO.NAME_ALIAS);
    }

    /**
     * Returns the description of the DatapackContent.
     *
     * @return the description of the DatapackContent
     */
    public String getDescription() {
        return this.getString(DatapackContentTO.DESCRIPTION_ALIAS);
    }

    /**
     * Returns the ID (not UUID) of the DatapackContent.
     *
     * @return the ID of the DatapackContent
     */
    public String getId() {
        return this.getString(DatapackContentTO.ID_ALIAS);
    }

}
