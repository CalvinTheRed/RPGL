package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
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
    private final Map<Integer, List<RPGLObject>> initiativeTable;

    private boolean initiativeRolled = false;

    public RPGLContext() {
        this.contextObjects = new HashMap<>();
        this.initiativeTable = new HashMap<>();
    }

    public abstract boolean isObjectsTurn(RPGLObject object);

    /**
     * This method propagates a Subevent to each RPGLObject in context to allow their RPGLEffects to respond to it.
     * This is the mechanism by which Subevents are intended to be invoked.
     *
     * @param subevent a Subevent
     *
     * @throws Exception if an exception occurs
     */
    public void processSubevent(Subevent subevent, RPGLContext context) throws Exception {
        boolean wasProcessed;
        do {
            wasProcessed = false;
            for (Map.Entry<String, RPGLObject> contextObjectsEntry : this.contextObjects.entrySet()) {
                wasProcessed |= contextObjectsEntry.getValue().processSubevent(subevent, context);
            }
        } while (wasProcessed);
    }

    public void add(RPGLObject object) throws Exception {
        this.contextObjects.putIfAbsent(object.getUuid(), object);
        if (this.initiativeRolled) {
            this.trackObjectInitiative(this.rollObjectInitiative(object), object);
        }
    }

    public void remove(RPGLObject object) {
        this.contextObjects.remove(object.getUuid());
        if (this.initiativeRolled){
            for (Map.Entry<Integer, List<RPGLObject>> entry : this.initiativeTable.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().remove(object);
                }
            }
        }
    }

    public Map<Integer, List<RPGLObject>> getInitiativeTable() {
        return this.initiativeTable;
    }

    public void rollForInitiative() throws Exception {
        if (!this.initiativeRolled) {
            for (Map.Entry<String, RPGLObject> entry : this.contextObjects.entrySet()) {
                RPGLObject object = entry.getValue();
                this.trackObjectInitiative(this.rollObjectInitiative(object), object);
            }
            this.initiativeRolled = true;
        }
    }

    int rollObjectInitiative(RPGLObject object) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "dex",
                "tags": [ "initiative" ],
                "determined": [ 10, 10 ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "dex");
            this.putJsonArray("tags", new JsonArray() {{
                this.addString("initiative");
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
                this.addInteger(10);
            }});
        }});
        abilityCheck.setSource(object);
        abilityCheck.prepare(this);
        abilityCheck.setTarget(object);
        abilityCheck.invoke(this);

        return abilityCheck.get();
    }

    void trackObjectInitiative(int initiative, RPGLObject object) {
        List<RPGLObject> initiativeRow = this.initiativeTable.get(initiative);
        if (initiativeRow == null) {
            initiativeRow = new ArrayList<>();
            initiativeRow.add(object);
            this.initiativeTable.put(initiative, initiativeRow);
        } else {
            initiativeRow.add(object);
        }
    }

    public void merge(RPGLContext other) throws Exception {
        this.contextObjects.putAll(other.contextObjects);
        if (this.initiativeRolled || other.initiativeRolled) {
            this.rollForInitiative();
            other.rollForInitiative();
            for (Map.Entry<Integer, List<RPGLObject>> otherEntry : other.initiativeTable.entrySet()) {
                if (this.initiativeTable.get(otherEntry.getKey()) == null) {
                    this.initiativeTable.put(otherEntry.getKey(), otherEntry.getValue());
                } else {
                    this.initiativeTable.get(otherEntry.getKey()).addAll(otherEntry.getValue());
                }
            }
        }
    }

}
