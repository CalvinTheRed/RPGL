package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class DatapackContentTO {

    // JSON property aliases
    public static final String METADATA_ALIAS = "metadata";
    public static final String NAME_ALIAS = "name";
    public static final String ID_ALIAS = "id";

    @JsonProperty(METADATA_ALIAS)
    Map<String, Object> metadata;
    @JsonProperty(NAME_ALIAS)
    String name;
    @JsonProperty(ID_ALIAS)
    String id;

    public DatapackContentTO(JsonObject rpglObject) {
        this.metadata = rpglObject.getMap(METADATA_ALIAS);
        this.name = (String) rpglObject.get(NAME_ALIAS);
        this.id = (String) rpglObject.get(ID_ALIAS);
    }

    protected Map<String, Object> getTemplateData() {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put(METADATA_ALIAS, metadata);
        templateData.put(NAME_ALIAS, name);
        templateData.put(ID_ALIAS, id);
        return templateData;
    }

}
