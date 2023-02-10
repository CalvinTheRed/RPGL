package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTableElement;

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

    /**
     * Default constructor for UUIDTableElementTO class.
     */
    public UUIDTableElementTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * Constructor to be used when storing data from a fully instantiated UUIDTableElement. Intended to be used for
     * saving data.
     *
     * @param uuidTableElement a fully instantiated UUIDTableElement
     */
    public UUIDTableElementTO(UUIDTableElement uuidTableElement) {
        super(uuidTableElement);
        this.uuid = uuidTableElement.getString(UUID_ALIAS);
    }

    /**
     * 	<p><b><i>getUUIDTableElementData</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonObject getUUIDTableElementData()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns json data representing the data stored by this object relevant to UUIDTableElement data.
     * 	</p>
     *
     * 	@return a JsonObject
     */
    public JsonObject getUUIDTableElementData() {
        return new JsonObject() {{
            this.putString(UUID_ALIAS, uuid);
        }};
    }

}
