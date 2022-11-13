package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * RPGLEvents are combinations of Subevents which define an emergent event which can be performed by a RPGLObject.
 *
 * @author Calvin Withun
 */
public class RPGLEvent extends JsonObject {

    /**
     * 	<p><b><i>RPGLEvent</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * RPGLEvent(JsonObject eventJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	A copy-constructor for the RPGLEvent class.
     * 	</p>
     *
     * 	@param eventJson the data to be joined to the new RPGLEvent
     */
    RPGLEvent(JsonObject eventJson) {
        this.join(eventJson);
    }

}
