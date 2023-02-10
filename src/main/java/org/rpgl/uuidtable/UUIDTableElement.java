package org.rpgl.uuidtable;

import org.rpgl.datapack.UUIDTableElementTO;
import org.rpgl.json.JsonObject;

/**
 * This class is the base class of any object which gets stored in UUIDTable.
 *
 * @author Calvin Withun
 */
public class UUIDTableElement extends JsonObject {

    /**
     * 	<p><b><i>getUuid</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String getUuid()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the UUID of this object.
     * 	</p>
     *
     * 	@return the UUID of this object
     */
    public String getUuid() {
        return this.getString(UUIDTableElementTO.UUID_ALIAS);
    }


    /**
     * 	<p><b><i>setUuid</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setUuid(String uuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Sets the UUID of this object.
     * 	</p>
     *
     * 	@param uuid the UUID to be assigned to this object
     */
    public void setUuid(String uuid) {
        this.putString(UUIDTableElementTO.UUID_ALIAS, uuid);
    }

    /**
     * 	<p><b><i>deleteUuid</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void deleteUuid()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Deletes the key-value pair storing this object's UUID.
     * 	</p>
     */
    public void deleteUuid() {
        this.asMap().remove(UUIDTableElementTO.UUID_ALIAS);
    }

}
