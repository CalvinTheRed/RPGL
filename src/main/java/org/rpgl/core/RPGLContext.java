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
public class RPGLContext {

    private final Map<String, RPGLObject> contextObjects;

    private final Map<Double, RPGLObject> turnOrder;

    private Double currentInitiative;

    /**
     * Default constructor for RPGLContext. This is the typical way a RPGLContext object is meant to be constructed.
     */
    public RPGLContext() {
        this.contextObjects = new HashMap<>();
        this.turnOrder = new HashMap<>();
        this.currentInitiative = null;
    }

    /**
     * This method adds a RPGLObject to the context. The passed object will be assigned an initiative score and added to
     * the turn order. This initiative accounts for dexterity scores, and assigns random bonuses to differentiate what
     * would otherwise result in a tie.
     *
     * @param object an RPGLObject to be added into the context
     */
    public void add(RPGLObject object) throws Exception {
        String objectUuid = object.getUuid();
        this.contextObjects.putIfAbsent(objectUuid, object);
        Double initiative = this.rollObjectInitiative(object);
        turnOrder.put(initiative, object);
    }

    /**
     * This method adds a RPGLObject to the context, using a pre-determined initiative value. This method should only be
     * used during testing, as it risks overriding existing initiative scores if the same initiative is passed more than
     * once.
     *
     * @param object an RPGLObject to be added into the context
     */
    public void add(RPGLObject object, double initiative) throws Exception {
        String objectUuid = object.getUuid();
        this.contextObjects.putIfAbsent(objectUuid, object);
        turnOrder.put(initiative, object);
    }

    /**
     * This method removes a RPGLObject from the context. This includes removing it from context as well as forgetting
     * its place in the initiative order.
     *
     * @param objectUuid the UUID of an RPGLObject to be removed from context
     * @return the RPGLObject removed from context
     */
    public RPGLObject remove(String objectUuid) {
        RPGLObject object = this.contextObjects.remove(objectUuid);
        for (Map.Entry<Double, RPGLObject> entry : this.turnOrder.entrySet()) {
            if (entry.getValue() == object) {
                this.turnOrder.remove(entry.getKey());
                break;
            }
        }
        return object;
    }

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

    double rollObjectInitiative(RPGLObject object) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "dex",
                "tags": [ "initiative" ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "dex");
            this.putJsonArray("tags", new JsonArray() {{
                this.addString("initiative");
            }});
        }});
        abilityCheck.setSource(object);
        abilityCheck.prepare(this);
        abilityCheck.setTarget(object);
        abilityCheck.invoke(this);
        // initiative = ability check + (ability score / 100) + (random bonus / 10,000)
        return abilityCheck.get()
                + ( (double) object.getAbilityScoreFromAbilityName("dex", this) / 100)
                + (Math.random() / 10000);
    }

    public RPGLObject nextObject() {
        List<Double> initiativeScores = new ArrayList<>(turnOrder.keySet().stream().sorted().toList());
        int initiativeIndex = initiativeScores.indexOf(this.currentInitiative);
        if (initiativeIndex == initiativeScores.size() - 1) {
            initiativeIndex = 0;
        } else {
            initiativeIndex++;
        }
        this.currentInitiative = initiativeScores.get(initiativeIndex);
        return turnOrder.get(this.currentInitiative);
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
