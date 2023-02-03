package org.rpgl.core;

import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

public class RPGLEffectTemplate extends JsonObject {

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
        RPGLEffect effect = new RPGLEffect();
        effect.join(this);
        UUIDTable.register(effect);
        return effect;
    }

}
