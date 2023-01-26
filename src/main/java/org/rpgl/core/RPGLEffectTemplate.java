package org.rpgl.core;

import org.jsonutils.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This class contains a JSON template defining a particular type of RPGLEffect. It is not intended to be used for any
 * purpose other than constructing new RPGLEffect objects.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTemplate extends JsonObject {

    /**
     * 	<p><b><i>RPGLEffectTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEffectTemplate(JsonObject effectTemplateJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	The constructor for the RPGLEffectTemplate class.
     * 	</p>
     *
     * 	@param effectTemplateJson the JSON data to be joined to the new RPGLEffectTemplate object.
     */
    public RPGLEffectTemplate(JsonObject effectTemplateJson) {
        this.join(effectTemplateJson);
    }

    /**
     * 	<p><b><i>newInstance</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEffect newInstance()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructs a new RPGLEffect object corresponding to the contents of the RPGLEffectTemplate object. The new
     * 	object is registered to the UUIDTable class when it is constructed.
     * 	</p>
     *
     * 	@return a new RPGLEffect object
     */
    public RPGLEffect newInstance() {
        RPGLEffect effect = new RPGLEffect(this);
        UUIDTable.register(effect);
        return effect;
    }

}
