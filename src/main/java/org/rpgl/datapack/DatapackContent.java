package org.rpgl.datapack;

import org.rpgl.json.JsonObject;

public class DatapackContent extends JsonObject {

    public JsonObject getMetadata() {
        return this.getJsonObject(DatapackContentTO.METADATA_ALIAS);
    }

    public String getName() {
        return this.getString(DatapackContentTO.NAME_ALIAS);
    }

    public String getDescription() {
        return this.getString(DatapackContentTO.DESCRIPTION_ALIAS);
    }

    public String getId() {
        return this.getString(DatapackContentTO.ID_ALIAS);
    }

}
