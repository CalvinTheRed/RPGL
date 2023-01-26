package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;

import java.util.Objects;

/**
 * This Subevent is dedicated to assigning an RPGLEffect to an RPGLObject.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to apply an RPGLEffect to another RPGLObject
 * <br>
 * Target: an RPGLObject to whom an RPGLEffect is being applied
 *
 * @author Calvin Withun
 */
public class GiveEffect extends Subevent {

    public GiveEffect() {
        super("give_effect");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GiveEffect();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new GiveEffect();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        if (!this.isCancelled()) {
            RPGLEffect effect = RPGLFactory.newEffect((String) this.subeventJson.get("effect"));
            if (effect != null) {
                this.getTarget().addEffect(effect);
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>cancel</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void cancel()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method "cancels" this Subevent, causing the RPGLEffect to not be applied to <code>target</code>. This is
     * 	meant to be used in cases where <code>target</code> is immune to select status effects.
     * 	</p>
     */
    public void cancel() {
        this.subeventJson.put("cancel", true);
    }

    /**
     * 	<p>
     * 	<b><i>isCancelled</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * boolean isCancelled()
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method returns whether the Subevent was cancelled.
     * 	</p>
     *
     *  @return true if the Subevent was cancelled.
     */
    boolean isCancelled() {
        return Objects.requireNonNullElse((Boolean) this.subeventJson.get("cancel"), false);
    }

}
