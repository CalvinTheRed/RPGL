package org.rpgl.uuidtable;

import org.jsonutils.JsonObject;

/**
 * 	This class represents any object which can be stored in the UUIDTable.
 *
 * @author Calvin Withun
 */
public abstract class UUIDTableElement extends JsonObject {

    /**
     * 	<p><b><i>setUuid</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setUuid(String uuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method assigns a UUID to the UUIDTableElement object.
     * 	</p>
     *
     *  @param uuid a UUID String
     */
    public void setUuid(String uuid) {
        this.put("uuid", uuid);
    }

    /**
     * 	<p><b><i>deleteUuid</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String deleteUuid()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method deletes and returns the UUID from the UUIDTableElement, in order to guard against future UUID
     * 	collisions.
     * 	</p>
     *
     *  @return a UUID String
     */
    public String deleteUuid() {
        return (String) this.remove("uuid");
    }

    /**
     * 	<p><b><i>getUuid</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String getUuid()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the UUIDTableElement object's assigned UUID.
     * 	</p>
     *
     *  @return a UUID String
     */
    public String getUuid() {
        return (String) this.get("uuid");
    }
}
