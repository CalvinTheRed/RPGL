package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.subevent.Subevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the context in which actions take place in this library. Typically, there will exist one
 * RPGLContext object for every encounter which takes place in RPGL, though if combat spans a large area, it may make
 * more sense to break it into several lighter RPGLContext objects. Conversely, if there are many light RPGLContexts
 * covering a large area, but an RPGLEvent is invoked which covers area delegated to distinct RPGLContexts, it would
 * make sense to create a temporary RPGLContext to represent the union of several smaller RPGLContext objects.
 *
 * @author Calvin Withun
 */
public abstract class RPGLContext {

    private final Map<String, RPGLObject> contextObjects;

    public RPGLContext() {
        this.contextObjects = new HashMap<>();
    }

    public abstract boolean isObjectsTurn(RPGLObject object);

    /**
     * This method propagates a Subevent to each RPGLObject in context to allow their RPGLEffects to respond to it.
     * This is the mechanism by which Subevents are intended to be invoked.
     *
     * @param subevent a Subevent
     * @param context the context in which the passed subevent is being processed
     * @param originPoint the point from which the passed subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    public void processSubevent(Subevent subevent, RPGLContext context, JsonArray originPoint) throws Exception {
        boolean wasProcessed;
        do {
            wasProcessed = false;
            for (Map.Entry<String, RPGLObject> contextObjectsEntry : this.contextObjects.entrySet()) {
                wasProcessed |= contextObjectsEntry.getValue().processSubevent(subevent, context, originPoint);
            }
        } while (wasProcessed);
    }

    /**
     * Adds a RPGLObject to the context
     *
     * @param object a RPGLObject
     */
    public void add(RPGLObject object) {
        this.contextObjects.putIfAbsent(object.getUuid(), object);
    }

    /**
     * Removes a RPGLObject from the context
     *
     * @param object a RPGLObject
     */
    public void remove(RPGLObject object) {
        this.contextObjects.remove(object.getUuid());
    }

    /**
     * Adds the RPGLObjects from another context to this context.
     *
     * @param other a RPGLContext
     */
    public void merge(RPGLContext other) {
        this.contextObjects.putAll(other.contextObjects);
    }

    /**
     * Returns a list of all RPGLObjects in context.
     *
     * @return a list of RPGLObjects
     */
    public List<RPGLObject> getContextObjects() {
        ArrayList<RPGLObject> objects = new ArrayList<>();
        for (Map.Entry<String, RPGLObject> entry : this.contextObjects.entrySet()) {
            objects.add(entry.getValue());
        }
        return objects;
    }

    /**
     * This method is intended to give an RPGLContext object the ability to view Subevents after they are completed.
     * This may be used to present results of rolls to the user. Note that doing anything to modify the Subevent at this
     * point will not impact the results of the Subevent. By default, this method does nothing.
     *
     * @param subevent a completed Subevent
     */
    public void viewCompletedSubevent(@SuppressWarnings("unused") Subevent subevent) {
    }

    /**
     * This method clears all objects from the context.
     */
    public void clear() {
        this.contextObjects.clear();
    }

}
