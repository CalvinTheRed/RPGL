package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is used by an RPGLEvent in order to define what happens when it is invoked.
 *
 * @author Calvin Withun
 */
public abstract class Subevent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subevent.class);

    /**
     * A map of all Subevents which can be used in the JSON of an RPGLEvent.
     */
    public static final Map<String, Subevent> SUBEVENTS = new HashMap<>();

    JsonObject subeventJson = new JsonObject();
    List<RPGLEffect> modifyingEffects = new LinkedList<>();

    final String subeventId;

    /**
     * This method populates Condition.CONDITIONS.
     *
     * @param includeTestingSubevents whether testing-only Subevents should be loaded into RPGL
     */
    public static void initialize(boolean includeTestingSubevents) {
        Subevent.SUBEVENTS.clear();

        Subevent.SUBEVENTS.put("attack_roll",   new AttackRoll());
        Subevent.SUBEVENTS.put("contest",       new Contest());
        Subevent.SUBEVENTS.put("deal_damage",   new DealDamage());
        Subevent.SUBEVENTS.put("give_effect",   new GiveEffect());
        //Subevent.SUBEVENTS.put("remove_effect", new RemoveEffect());
        Subevent.SUBEVENTS.put("saving_throw",  new SavingThrow());

        if (includeTestingSubevents) {
            Subevent.SUBEVENTS.put("dummy_subevent", new DummySubevent());
        }
    }

    /**
     * Constructor for Subevent. New Subevents should be constructed via cloning from <code>Subevent.SUBEVENTS</code>
     * rather than through the use of constructors.
     *
     * @param subeventId the ID for the Subevent being constructed
     */
    public Subevent(String subeventId) {
        this.subeventId = subeventId;
    }

    /**
     * Returns whether the provided tag is present in the Subevent.
     *
     * @param tag a subevent tag
     * @return true if the tag is present, false otherwise
     */
    public boolean hasTag(String tag) {
        return this.subeventJson.getJsonArray("tags").asList().contains(tag);
    }

    /**
     * Adds a tag to the subevent tags array.
     *
     * @param tag a subevent tag
     */
    public void addTag(String tag) {
        this.subeventJson.getJsonArray("tags").addString(tag);
    }
    /**
     * Verifies that the additional information provided to <code>invoke(...)</code> is intended for the Subevent type
     * being invoked.
     *
     * @param expected the expected subeventId
     *
     * @throws SubeventMismatchException if functionJson is for a different function than the one being executed
     */
    void verifySubevent(String expected) throws SubeventMismatchException {
        if (!expected.equals(this.subeventJson.getString("subevent"))) {
            SubeventMismatchException e = new SubeventMismatchException(expected, this.subeventJson.getString("subevent"));
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * This method joins the passed JSON data to the current Subevent JSON data. This method is primarily intended to be
     * used when Subevents must be created which are not included in <code>Subevent.SUBEVENTS</code>.
     *
     * @param subeventData the JSON data to be joined to the current Subevent JSON
     */
    public void joinSubeventData(JsonObject subeventData) {
        this.subeventJson.join(subeventData);
    }

    @Override
    public abstract Subevent clone(); // used to clone modified Subevents out to targets

    /**
     * This method creates a new and independent Subevent of the same type as the calling Subevent. The provided JSON
     * data will be used to populate the new Subevent. This method's primary use case is to create a new Subevent when
     * retrieving a Subevent from the Subevent.SUBEVENTS map.
     *
     * @param jsonData the JSON data to be joined to the new Subevent after being cloned
     */
    public abstract Subevent clone(JsonObject jsonData);

    /**
     * Gives a Subevent the opportunity to prepare information prior to being copied out to each individual target.
     * This method might not do anything for simpler Subevents. The <code>setSource(...)</code> method must be used to
     * assign a source RPGLObject to the Subevent for this method to work reliably.
     *
     * @param context the context in which the Subevent is being prepared\
     *
     * @throws Exception if an exception occurs (any type of error may occur from calling this method)
     */
    public void prepare(RPGLContext context) throws Exception {
        if (this.subeventJson.getJsonArray("tags") == null) {
            this.subeventJson.putJsonArray("tags", new JsonArray());
        }
    }

    /**
     * This method causes the Subevent to be submitted to all RPGLEffects in scope of the RPGLContext provided and then
     * execute its intended behavior. The <code>setTarget(...)</code> method must be used to assign a target RPGLObject
     * to the Subevent for this method to work reliably.
     *
     * @param context the context in which the Subevent is being invoked
     *
     * @throws Exception if an exception occurs (any type of error may occur from calling this method)
     */
    public void invoke(RPGLContext context) throws Exception {
        this.verifySubevent(this.subeventId);
        context.processSubevent(this);
    }

    /**
     * Adds a modifying RPGLEffect to the Subevent. Once a RPGLEffect is added in this way, the Subevent cannot be
     * modified by another RPGLEffect with the same effectId. RPGLEffects added in this way cannot be removed, and
     * carry over when the <code>clone()</code> method is called (but not <code>clone(...)</code>).
     *
     * @param effect an RPGLEffect which has executed Functions in response to this Subevent being invoked
     */
    public void addModifyingEffect(RPGLEffect effect) {
        this.modifyingEffects.add(effect);
    }

    /**
     * Checks if the Subevent has already been modified by an RPGLEffect with the same effectId as the passed RPGLEffect.
     *
     * @param effect an RPGLEffect
     * @return true if the Subevent has been modified by an instance of the passed Subevent already
     */
    public boolean hasModifyingEffect(RPGLEffect effect) {
        String effectId = effect.getString("id");
        for (RPGLEffect modifyingEffect : modifyingEffects) {
            if (effectId.equals(modifyingEffect.getString("id"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns a RPGLObject as the source of this Subevent.
     *
     * @param source an RPGLObject
     */
    public void setSource(RPGLObject source) {
        if (source == null) {
            this.subeventJson.putString("source", null);
        } else {
            this.subeventJson.putString("source", source.getUuid());
        }
    }

    /**
     * Assigns a RPGLObject as the target of this Subevent.
     *
     * @param target an RPGLObject
     */
    public void setTarget(RPGLObject target) {
        if (target == null) {
            this.subeventJson.putString("target", null);
        } else {
            this.subeventJson.putString("target", target.getUuid());
        }
    }

    /**
     * Returns the source RPGLObject of the Subevent (or null if one is not assigned).
     *
     * @return the RPGLObject which initiated this Subevent
     */
    public RPGLObject getSource() {
        return UUIDTable.getObject(this.subeventJson.getString("source"));
    }

    /**
     * Returns the target RPGLObject of the Subevent (or null if one is not assigned).
     *
     * @return the RPGLObject towards which this Subevent is directed
     */
    public RPGLObject getTarget() {
        return UUIDTable.getObject(this.subeventJson.getString("target"));
    }

    /**
     * This method returns the ID for this Subevent.
     *
     * @return the ID of this SUbevent
     */
    public String getSubeventId() {
        return this.subeventId;
    }

    @Override
    public String toString() {
        return this.subeventJson.toString();
    }

}
