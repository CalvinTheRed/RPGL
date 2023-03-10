package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.Subevent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    public RPGLContext() {
        this.contextObjects = new HashMap<>();
        this.turnOrder = new HashMap<>();
        this.currentInitiative = null;
    }

    /**
     * Inline constructor for RPGLContext class. This constructor is meant to be used when a temporary RPGLContext is
     * being created. This temporary RPGLContext should not be used to track turn order, and only exists in order to
     * facilitate a RPGLEvent which spans an area which is much larger than an existing RPGLContext would account for.
     * For example, this constructor may be used to include all RPGLObjects in a 1-mile radius of the center of a large
     * area-of-effect spell, even though the spell was cast from within a much smaller RPGLContext.
     *
     * @param objects a collection of RPGLObjects to be included in this context
     */
    public RPGLContext(Collection<RPGLObject> objects) {
        this.contextObjects = new HashMap<>();
        this.turnOrder = new HashMap<>();
        this.currentInitiative = null;
        objects.forEach(object -> {
            contextObjects.put(object.getUuid(), object);
        });
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
     * @param object     an RPGLObject to be added into the context
     * @param initiative an initiative score
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

    /**
     * This helper method is used to roll initiative for an RPGLObject being added to this RPGLContext. In testing mode,
     * the first two d20 rolls entailed by this check will always be 10's. An initiative roll is determined by the
     * following equation:
     * <br>
     * <br>
     * <code>initiative = dex ability check + (dex score / 100) + (random bonus / 10,000)</code>
     *
     * @param object an RPGLObject whose initiative is being rolled
     * @return an initiative score for object
     *
     * @throws Exception if an exception occurs
     */
    double rollObjectInitiative(RPGLObject object) throws Exception {
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

        return abilityCheck.get()
                + ( (double) object.getAbilityScoreFromAbilityName("dex", this) / 100)
                + (Math.random() / 10000);
    }

    /**
     * Returns the RPGLObject which is active in the current turn.
     *
     * @return an RPGLObject
     */
    public RPGLObject currentObject() {
        return turnOrder.get(this.currentInitiative);
    }

    /**
     * This method transitions to the turn of the next RPGLObject, in order of descending initiative. If this method has
     * not been called on this RPGLContext yet, it returns the RPGLObject with the highest initiative score. This method
     * also facilitates the ending of turns and the beginning of turns, as appropriate. If the current initiative score
     * is the lowest initiative in context, this method will loop back to the highest initiative score and start the
     * cycle over again. This method returns the RPGLObject which is active in the new turn.
     *
     * @return an RPGLObject
     */
    public RPGLObject nextObject() throws Exception {
        List<Double> initiativeScores = new ArrayList<>(this.turnOrder.keySet().stream().sorted().toList());
        Collections.reverse(initiativeScores);
        RPGLObject lastObject = this.turnOrder.get(this.currentInitiative);
        int initiativeIndex = initiativeScores.indexOf(this.currentInitiative);
        if (initiativeIndex == initiativeScores.size() - 1) {
            // loop back around to the highest initiative in context
            initiativeIndex = 0;
        } else {
            initiativeIndex++;
        }
        this.currentInitiative = initiativeScores.get(initiativeIndex);
        RPGLObject nextObject = this.turnOrder.get(this.currentInitiative);
        if (lastObject != null) {
            // end turn of last object, unless this is the first turn
            lastObject.endTurn(this);
        }
        // start turn of next object
        nextObject.startTurn(this);
        return nextObject;
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
