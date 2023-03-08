package org.rpgl.datapack;

import org.rpgl.core.RPGLObject;
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
     * Setter for metadata.
     *
     * @param metadata a new metadata JsonObject
     */
    public void setMetadata(RPGLObject metadata) {
        this.putJsonObject(DatapackContentTO.METADATA_ALIAS, metadata);
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
     * Setter for name.
     *
     * @param name a new name String
     */
    public void setName(String name) {
        this.putString(DatapackContentTO.NAME_ALIAS, name);
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
     * Setter for description.
     *
     * @param description a new description String
     */
    public void setDescription(String description) {
        this.putString(DatapackContentTO.DESCRIPTION_ALIAS, description);
    }

    /**
     * Returns the ID (not UUID) of the DatapackContent.
     *
     * @return the ID of the DatapackContent
     */
    public String getId() {
        return this.getString(DatapackContentTO.ID_ALIAS);
    }

    /**
     * Setter for id.
     *
     * @param id a new id String
     */
    public void setId(String id) {
        this.putString(DatapackContentTO.ID_ALIAS, id);
    }

}
