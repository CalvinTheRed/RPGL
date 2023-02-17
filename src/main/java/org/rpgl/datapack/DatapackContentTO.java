package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.json.JsonObject;

import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL. This is the base class for all transfer
 * objects utilized by RPGL.
 *
 * @author Calvin Withun
 */
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

    /**
     * Default constructor for DatapackContentTO class.
     */
    public DatapackContentTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * Constructor to be used when storing data from a fully instantiated object. Intended to be used for saving data.
     *
     * @param datapackContent a fully instantiated DatapackContent object
     */
    public DatapackContentTO(DatapackContent datapackContent) {
        this.metadata = datapackContent.getMetadata().asMap();
        this.name = datapackContent.getName();
        this.description = datapackContent.getDescription();
        this.id = datapackContent.getId();
    }

    /**
     * This method returns json data representing the data stored by this object relevant to universally shared
     * datapack template data.
     *
     * @return a JsonObject
     */
    protected JsonObject getTemplateData() {
        return new JsonObject() {{
            this.putJsonObject(METADATA_ALIAS, new JsonObject(metadata));
            this.putString(NAME_ALIAS, name);
            this.putString(DESCRIPTION_ALIAS, description);
            this.putString(ID_ALIAS, id);
        }};
    }

}
