package org.rpgl.core;

import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.subevent.Subevent;

import java.util.HashMap;
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
public class RPGLContext {

    private final Map<String, RPGLObject> contextObjects;

    /**
     * Default constructor for RPGLContext. This is the typical way a RPGLContext object is meant to be constructed.
     */
    public RPGLContext() {
        this.contextObjects = new HashMap<>();
    }

    /**
     * This method adds a RPGLObject to the context.
     *
     * @param object an RPGLObject to be added into the context
     */
    public void add(RPGLObject object) {
        this.contextObjects.put(object.getUuid(), object);
    }

    /**
     * This method removes a RPGLObject from the context.
     *
     * @param objectUuid the UUID of an RPGLObject to be removed from context
     * @return the RPGLObject removed from context
     */
    public RPGLObject remove(String objectUuid) {
        return this.contextObjects.remove(objectUuid);
    }

    /**
     * This method propagates a Subevent to each RPGLObject in context to allow their RPGLEffects to respond to it.
     * This is the mechanism by which Subevents are intended to be invoked.
     *
     * @param subevent a Subevent
     *
     * @throws ConditionMismatchException if a Condition was presented incorrectly formatted JSON data
     * @throws FunctionMismatchException  if a Function was presented incorrectly formatted JSON data.
     */
    public void processSubevent(Subevent subevent) throws ConditionMismatchException, FunctionMismatchException {
        boolean wasProcessed;
        do {
            wasProcessed = false;
            for (Map.Entry<String, RPGLObject> contextObjectsEntry : this.contextObjects.entrySet()) {
                wasProcessed |= contextObjectsEntry.getValue().processSubevent(subevent);
            }
        } while (wasProcessed);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (Map.Entry<String, RPGLObject> contextObjectEntry : this.contextObjects.entrySet()) {
            stringBuilder.append(contextObjectEntry.getValue().getUuid());
            stringBuilder.append(',');
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

}
