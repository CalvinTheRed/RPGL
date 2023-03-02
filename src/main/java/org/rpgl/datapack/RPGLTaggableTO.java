package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLTaggable;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;

/**
 * This class is used to create transfer objects between a datapack and RPGL. This class is the base class of RPGLEffect,
 * RPGLItem, and RPGLObject (the types which are stored in UUIDTable).
 *
 * @author Calvin Withun
 */
public class RPGLTaggableTO extends UUIDTableElementTO {

    // JSON property aliases

    public static final String TAGS_ALIAS = "tags";

    @JsonProperty(TAGS_ALIAS)
    ArrayList<Object> tags;

    /**
     * Default constructor for UUIDTableElementTO class.
     */
    public RPGLTaggableTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLTaggable. Intended to be used for saving
     * data.
     *
     * @param rpglTaggable a fully instantiated RPGLTaggable
     */
    public RPGLTaggableTO(RPGLTaggable rpglTaggable) {
        super(rpglTaggable);
        this.tags = rpglTaggable.getTags().asList();
    }

    /**
     * This method returns json data representing the data stored by this object relevant to RPGLTaggable data.
     *
     * @return a JsonObject
     */
    public JsonObject getRPGLTaggableData() {
        return new JsonObject() {{
            this.putJsonArray(TAGS_ALIAS, new JsonArray(tags));
        }};
    }

    @Override
    public JsonObject getTemplateData() {
        JsonObject templateData = super.getTemplateData();
        templateData.join(new JsonObject() {{
            this.putJsonArray(TAGS_ALIAS, new JsonArray(tags));
        }});
        return templateData;
    }

}
