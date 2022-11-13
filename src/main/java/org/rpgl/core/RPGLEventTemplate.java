package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * This class contains a JSON template defining a particular type of RPGLEvent. It is not intended to be used for any
 * purpose other than constructing new RPGLEvent objects.
 *
 * @author Calvin Withun
 */
public class RPGLEventTemplate extends JsonObject {

    /**
     * 	<p><b><i>RPGLEventTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEventTemplate(JsonObject eventTemplateJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	The constructor for the RPGLEventTemplate class.
     * 	</p>
     *
     * 	@param eventTemplateJson the JSON data to be joined to the new RPGLEventTemplate object.
     */
    public RPGLEventTemplate(JsonObject eventTemplateJson) {
        this.join(eventTemplateJson);
    }

    /**
     * 	<p><b><i>newInstance</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEvent newInstance()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructs a new RPGLEvent object corresponding to the contents of the RPGLEventTemplate object. The new object
     * 	is registered to the UUIDTable class when it is constructed.
     * 	</p>
     *
     * 	@return a new RPGLEvent object
     */
    public RPGLEvent newInstance() {
        return new RPGLEvent(this);
    }

}
