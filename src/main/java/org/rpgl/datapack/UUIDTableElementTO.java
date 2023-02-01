package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.JsonObject;

public class UUIDTableElementTO extends DatapackContentTO {

    // JSON property aliases

    public static final String UUID_ALIAS = "uuid";

    @JsonProperty(UUID_ALIAS)
    String uuid;

    public UUIDTableElementTO(JsonObject rpglObject) {
        super(rpglObject);
        this.uuid = (String) rpglObject.get(UUID_ALIAS);
    }

}
