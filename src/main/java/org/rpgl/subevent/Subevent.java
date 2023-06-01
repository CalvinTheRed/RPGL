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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public JsonObject json = new JsonObject();

    List<RPGLEffect> modifyingEffects = new LinkedList<>();

    final String subeventId;

    /**
     * This method populates Subevent.SUBEVENTS.
     *
     * @param includeTestingSubevents whether testing-only Subevents should be loaded into RPGL
     */
    public static void initialize(boolean includeTestingSubevents) {
        Subevent.SUBEVENTS.clear();

        Subevent.SUBEVENTS.put("attack_roll", new AttackRoll());
        Subevent.SUBEVENTS.put("contest", new Contest());
        Subevent.SUBEVENTS.put("deal_damage", new DealDamage());
        Subevent.SUBEVENTS.put("give_effect", new GiveEffect());
        Subevent.SUBEVENTS.put("give_resource", new GiveResource());
        Subevent.SUBEVENTS.put("give_temporary_hit_points", new GiveTemporaryHitPoints());
        Subevent.SUBEVENTS.put("heal", new Heal());
        Subevent.SUBEVENTS.put("info_subevent", new InfoSubevent());
        //Subevent.SUBEVENTS.put("remove_effect", new RemoveEffect());
        Subevent.SUBEVENTS.put("saving_throw", new SavingThrow());
        Subevent.SUBEVENTS.put("take_resource", new TakeResource());

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
        this.json.putString("subevent", subeventId);
        this.json.asMap().putIfAbsent("tags", new ArrayList<>());
    }

    /**
     * Returns whether the provided tag is present in the Subevent.
     *
     * @param tag a subevent tag
     * @return true if the tag is present, false otherwise
     */
    public boolean hasTag(String tag) {
        // TODO does this method need to exist when getTags() exists?
        return this.getTags().asList().contains(tag);
    }

    /**
     * Adds a tag to the subevent tags array.
     *
     * @param tag a subevent tag
     */
    public void addTag(String tag) {
        this.getTags().addString(tag);
    }

    /**
     * Returns the Subevent's tage.
     *
     * @return a JsonArray of tags
     */
    public JsonArray getTags() {
        return this.json.getJsonArray("tags");
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
        if (!Objects.equals(expected, this.json.getString("subevent"))) {
            SubeventMismatchException e = new SubeventMismatchException(expected, this.json.getString("subevent"));
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
        this.json.join(subeventData);
    }

    /**
     * This method creates a deep clone of the Subevent. This method is meant to be used to clone a modified Subevent
     * prior to distributing clones to the targets of an RPGLEvent.
     *
     * @return a deep clone of the Subevent
     */
    @Override
    public abstract Subevent clone();

    /**
     * This method creates a new and independent Subevent of the same type as the calling Subevent. The provided JSON
     * data will be used to populate the new Subevent. This method's primary use case is to create a new Subevent when
     * retrieving a Subevent from the Subevent.SUBEVENTS map.
     *
     * @param jsonData the JSON data to be joined to the new Subevent after being cloned
     * @return a new instance of the Subevent with the passed json data joined to it
     */
    public abstract Subevent clone(JsonObject jsonData);

    /**
     * Gives a Subevent the opportunity to prepare information prior to being copied out to each individual target.
     * This method might not do anything for simpler Subevents. The <code>setSource(...)</code> method must be used to
     * assign a source RPGLObject to the Subevent for this method to work reliably.
     *
     * @param context the context in which the Subevent is being prepared\
     *
     * @throws Exception if an exception occurs
     */
    public void prepare(RPGLContext context) throws Exception {
        if (this.json.getJsonArray("tags") == null) {
            this.json.putJsonArray("tags", new JsonArray());
        }
    }

    /**
     * This method facilitates the invocation of a Subevent. It verifies the Subevent, processes it, runs it, and then
     * passes the completed version of it to the RPGLContext for viewing.
     *
     * @param context the context in which the Subevent is being invoked
     *
     * @throws Exception if an exception occurs
     */
    public void invoke(RPGLContext context) throws Exception {
        this.verifySubevent(this.subeventId);
        context.processSubevent(this, context);
        this.run(context);
        context.viewCompletedSubevent(this);
    }

    /**
     * This method contains the logic definitive of the Subevent. A Subevent should be prepared and processed before
     * this method is called. This method does nothing by default.
     *
     * @param context the context in which the Subevent is being invoked
     *
     * @throws Exception if an exception occurs
     */
    public void run(RPGLContext context) throws Exception {
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
            if (Objects.equals(effectId, modifyingEffect.getString("id"))) {
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
            this.json.putString("source", null);
        } else {
            this.json.putString("source", source.getUuid());
        }
    }

    /**
     * Assigns a RPGLObject as the target of this Subevent.
     *
     * @param target an RPGLObject
     */
    public void setTarget(RPGLObject target) {
        if (target == null) {
            this.json.putString("target", null);
        } else {
            this.json.putString("target", target.getUuid());
        }
    }

    /**
     * Returns the source RPGLObject of the Subevent (or null if one is not assigned).
     *
     * @return the RPGLObject which initiated this Subevent
     */
    public RPGLObject getSource() {
        return UUIDTable.getObject(this.json.getString("source"));
    }

    /**
     * Returns the target RPGLObject of the Subevent (or null if one is not assigned).
     *
     * @return the RPGLObject towards which this Subevent is directed
     */
    public RPGLObject getTarget() {
        return UUIDTable.getObject(this.json.getString("target"));
    }

    /**
     * This method returns the ID for this Subevent.
     *
     * @return the ID of this Subevent
     */
    public String getSubeventId() {
        return this.subeventId;
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

}
