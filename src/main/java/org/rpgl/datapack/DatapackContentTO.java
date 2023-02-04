package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.json.JsonObject;

import java.util.HashMap;

public class DatapackContentTO {

    // JSON property aliases
    public static final String METADATA_ALIAS = "metadata";
    public static final String NAME_ALIAS = "name";
    public static final String DESCRIPTION_ALIAS = "description";
    public static final String ID_ALIAS = "id";

    @JsonProperty(METADATA_ALIAS)
    HashMap<String, Object> metadata;
    @JsonProperty(NAME_ALIAS)
    String name;
    @JsonProperty(DESCRIPTION_ALIAS)
    String description;
    @JsonProperty(ID_ALIAS)
    String id;

    public DatapackContentTO() {

    }

    public DatapackContentTO(JsonObject rpglObject) {
        this.metadata = rpglObject.getJsonObject(METADATA_ALIAS).asMap();
        this.name = rpglObject.getString(NAME_ALIAS);
        this.description = rpglObject.getString(DESCRIPTION_ALIAS);
        this.id = rpglObject.getString(ID_ALIAS);
    }

    protected JsonObject getTemplateData() {
        return new JsonObject() {{
            this.putJsonObject(METADATA_ALIAS, new JsonObject(metadata));
            this.putString(NAME_ALIAS, name);
            this.putString(DESCRIPTION_ALIAS, description);
            this.putString(ID_ALIAS, id);
        }};
    }

}
