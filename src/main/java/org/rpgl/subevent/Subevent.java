package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.uuidtable.UUIDTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is used by an RPGLEvent in order to define what happens when it is invoked.
 *
 * @author Calvin Withun
 */
public abstract class Subevent {

    /**
     * A map of all Subevents which can be used in the JSON of an RPGLEvent.
     */
    public static final Map<String, Subevent> SUBEVENTS;

    JsonObject subeventJson = new JsonObject();
    LinkedList<RPGLEffect> modifyingEffects = new LinkedList<>();

    private final String subeventId;

    static {
        SUBEVENTS = new HashMap<>();
        // AbilityCheck
        Subevent.SUBEVENTS.put("attack_roll", new AttackRoll());
        // Damage?
        Subevent.SUBEVENTS.put("dummy_subevent", new DummySubevent());
        Subevent.SUBEVENTS.put("give_effect", new GiveEffect());
        Subevent.SUBEVENTS.put("saving_throw", new SavingThrow());
        // TakeEffect
    }

    /**
     * 	<p><b><i>Subevent</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * Subevent(String subeventId)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructor for Subevent. New Subevents should be constructed via cloning from <code>Subevent.SUBEVENTS</code>
     * 	rather than through the use of constructors.
     * 	</p>
     *
     * 	@param subeventId the ID for the Subevent being constructed
     */
    public Subevent(String subeventId) {
        this.subeventId = subeventId;
    }

    /**
     * 	<p><b><i>verifySubevent</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void verifySubevent(String expected, JsonObject subeventJson)
     * 	throws SubeventMismatchException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Verifies that the additional information provided to <code>invoke(...)</code> is intended for the Subevent type
     * 	being invoked.
     * 	</p>
     *
     * 	@param expected the expected subeventId
     * 	@throws SubeventMismatchException if functionJson is for a different function than the one being executed
     */
    void verifySubevent(String expected) throws SubeventMismatchException {
        if (!expected.equals(this.subeventJson.get("subevent"))) {
            throw new SubeventMismatchException(expected, (String) this.subeventJson.get("subevent"));
        }
    }

    /**
     * 	<p><b><i>joinSubeventJson</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void joinSubeventJson(JsonObject subeventJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method joins the passed JSON data to the current Subevent JSON data. This method is primarily intended to
     * 	be used when Subevents must be created which are not included in <code>Subevent.SUBEVENTS</code>.
     * 	</p>
     *
     * 	@param subeventJson the JSON data to be joined to the current Subevent JSON
     */
    public void joinSubeventJson(JsonObject subeventJson) {
        this.subeventJson.join(subeventJson);
    }

    @Override
    public abstract Subevent clone();

    /**
     * 	<p><b><i>clone</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Subevent clone(JsonObject subeventJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method creates a new and independent Subevent of the same type as the calling Subevent. The provided JSON
     * 	data will be used to populate the new Subevent. This method's primary use case is to create a new Subevent when
     * 	retrieving a Subevent from the Subevent.SUBEVENTS map.
     * 	</p>
     *
     * 	@param subeventJson the JSON data to be joined to the new Subevent
     */
    public abstract Subevent clone(JsonObject subeventJson);

    /**
     * 	<p><b><i>prepare</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void prepare(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Gives a Subevent the opportunity to prepare information prior to being copied out to each individual target.
     * 	This method might not do anything for simpler Subevents. The <code>setSource(...)</code> method must be used to
     * 	assign a source RPGLObject to the Subevent for this method to work reliably.
     * 	</p>
     *
     * 	@param context the context in which the Subevent is being prepared
     * 	@throws Exception if an exception occurs (any type of error may occur from calling this method)
     */
    public void prepare(RPGLContext context) throws Exception {
        // This method has no behavior by default. It is left empty
        // here for ease of developing derived classes elsewhere.
    }

    /**
     * 	<p><b><i>invoke</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void invoke(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method causes the Subevent to be submitted to all RPGLEffects in scope of the RPGLContext provided and then
     * 	execute its intended behavior. The <code>setTarget(...)</code> method must be used to assign a target RPGLObject
     * 	to the Subevent for this method to work reliably.
     * 	</p>
     *
     * 	@param context the context in which the Subevent is being invoked
     * 	@throws Exception if an exception occurs (any type of error may occur from calling this method)
     */
    public void invoke(RPGLContext context) throws Exception {
        this.verifySubevent(this.subeventId);
        context.processSubevent(this);
    }

    /**
     * 	<p><b><i>addModifyingEffect</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addModifyingEffect(RPGLEffect effect)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Adds a modifying RPGLEffect to the Subevent. Once a RPGLEffect is added in this way, the Subevent cannot be
     * 	modified by another RPGLEffect with the same effectId. RPGLEffects added in this way cannot be removed, and
     * 	carry over when the <code>clone()</code> method is called (but not <code>clone(...)</code>).
     * 	</p>
     *
     * 	@param effect an RPGLEffect which has executed Functions in response to this Subevent being invoked
     */
    public void addModifyingEffect(RPGLEffect effect) {
        this.modifyingEffects.add(effect);
    }

    /**
     * 	<p><b><i>hasModifyingEffect</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean hasModifyingEffect(RPGLEffect effect)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Checks if the Subevent has already been modified by an RPGLEffect with the same effectId as the passed RPGLEffect.
     * 	</p>
     *
     * 	@param effect an RPGLEffect
     */
    public boolean hasModifyingEffect(RPGLEffect effect) {
        String effectId = (String) effect.get("id");
        for (RPGLEffect modifyingEffect : modifyingEffects) {
            if (effectId.equals(modifyingEffect.get("id"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 	<p><b><i>setSource</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setSource(RPGLObject source)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Assigns a RPGLObject as the source of this Subevent.
     * 	</p>
     *
     * 	@param source an RPGLObject
     */
    public void setSource(RPGLObject source) {
        if (source == null) {
            this.subeventJson.put("source", null);
        } else {
            this.subeventJson.put("source", source.get("uuid"));
        }
    }

    /**
     * 	<p><b><i>setTarget</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setTarget(RPGLObject source)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Assigns a RPGLObject as the target of this Subevent.
     * 	</p>
     *
     * 	@param target an RPGLObject
     */
    public void setTarget(RPGLObject target) {
        if (target == null) {
            this.subeventJson.put("target", null);
        } else {
            this.subeventJson.put("target", target.get("uuid"));
        }
    }

    /**
     * 	<p><b><i>setSource</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLObject getSource()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the source RPGLObject of the Subevent (or null if one is not assigned).
     * 	</p>
     */
    public RPGLObject getSource() {
        return UUIDTable.getObject((String) this.subeventJson.get("source"));
    }

    /**
     * 	<p><b><i>setTarget</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLObject getTarget()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the target RPGLObject of the Subevent (or null if one is not assigned).
     * 	</p>
     */
    public RPGLObject getTarget() {
        return UUIDTable.getObject((String) this.subeventJson.get("target"));
    }

    @Override
    public String toString() {
        return this.subeventJson.toString();
    }

}
