package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.json.JsonObject;

/**
 * This class is used to create transfer objects between a datapack and RPGL. This class is the base class of RPGLEffect,
 * RPGLItem, and RPGLObject (the types which are stored in UUIDTable).
 *
 * @author Calvin Withun
 */
public class UUIDTableElementTO extends DatapackContentTO {

    // JSON property aliases

    public static final String UUID_ALIAS = "uuid";

    @JsonProperty(UUID_ALIAS)
    String uuid;

    // TODO javadoc here
    public UUIDTableElementTO() {

    }

    // TODO javadoc here
    public UUIDTableElementTO(JsonObject rpglObject) {
        super(rpglObject);
        this.uuid = rpglObject.getString(UUID_ALIAS);
        // TODO how will this be used when loading in a saved UUIDTableElement file?
    }

}
