package org.rpgl.core;

public class RPGLEventTemplate extends JsonObject {

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
        RPGLEvent event = new RPGLEvent();
        event.join(this);
        return event ;
    }

}
