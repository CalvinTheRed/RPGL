package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * This Subevent is dedicated to exhausting a number of RPGLResources according to their Subevent ID and potency. This
 * Subevent allows for prioritization by high, low, or random potency, as well as bounding the potencies which can be
 * exhausted.
 * <br>
 * <br>
 * source: a RPGLObject causing for resources to be exhausted
 * <br>
 * target: a RPGLObject whose resources are being exhausted
 *
 * @author Calvin Withun
 */
public class ExhaustResource extends Subevent {

    // TODO allow for this to exhaust based on tags?

    public ExhaustResource() {
        super("exhaust_resource");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new ExhaustResource();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new ExhaustResource();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public ExhaustResource invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (ExhaustResource) super.invoke(context, originPoint);
    }

    @Override
    public ExhaustResource joinSubeventData(JsonObject other) {
        return (ExhaustResource) super.joinSubeventData(other);
    }

    @Override
    public ExhaustResource prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("count", Integer.MAX_VALUE);
        this.json.asMap().putIfAbsent("maximum_potency", Integer.MAX_VALUE);
        this.json.asMap().putIfAbsent("minimum_potency", 0);
        this.json.asMap().putIfAbsent("selection_mode", "low_first");
        return this;
    }

    @Override
    public ExhaustResource run(RPGLContext context, JsonArray originPoint) {
        switch (this.json.getString("selection_mode")) {
            case "low_first" -> this.runLowFirst();
            case "high_first" -> this.runHighFirst();
            case "random" -> this.runRandom();
        }
        return this;
    }

    @Override
    public ExhaustResource setOriginItem(String originItem) {
        return (ExhaustResource) super.setOriginItem(originItem);
    }

    @Override
    public ExhaustResource setSource(RPGLObject source) {
        return (ExhaustResource) super.setSource(source);
    }

    @Override
    public ExhaustResource setTarget(RPGLObject target) {
        return (ExhaustResource) super.setTarget(target);
    }

    /**
     * This helper method is a version of the run method which prioritizes exhausting available resources from lowest
     * potency to highest.
     */
    void runLowFirst() {
        String resourceId = this.json.getString("resource");
        final int[] count = { this.json.getInteger("count") };
        final int minimumPotency = this.json.getInteger("minimum_potency");
        final int maximumPotency = this.json.getInteger("maximum_potency");

        super.getTarget().getResourceObjects().stream().sorted(Comparator.comparing(RPGLResource::getPotency)).forEach(resource -> {
            if (count[0] > 0
                    && !resource.getExhausted()
                    && Objects.equals(resourceId, resource.getId())
                    && resource.getPotency() >= minimumPotency
                    && resource.getPotency() <= maximumPotency) {
                resource.exhaust();
                count[0]--;
            }
        });
    }

    /**
     * This helper method is a version of the run method which prioritizes exhausting available resources from highest
     * potency to lowest.
     */
    void runHighFirst() {
        String resourceId = this.json.getString("resource");
        final int[] count = { this.json.getInteger("count") };
        final int minimumPotency = this.json.getInteger("minimum_potency");
        final int maximumPotency = this.json.getInteger("maximum_potency");

        super.getTarget().getResourceObjects().stream().sorted(Collections.reverseOrder(Comparator.comparing(RPGLResource::getPotency))).forEach(resource -> {
            if (count[0] > 0
                    && !resource.getExhausted()
                    && Objects.equals(resourceId, resource.getId())
                    && resource.getPotency() >= minimumPotency
                    && resource.getPotency() <= maximumPotency) {
                resource.exhaust();
                count[0]--;
            }
        });
    }

    /**
     * This helper method is a version of the run method which prioritizes exhausting available resources in a random
     * order.
     */
    void runRandom() {
        String resourceId = this.json.getString("resource");
        int count = this.json.getInteger("count");
        int minimumPotency = this.json.getInteger("minimum_potency");
        int maximumPotency = this.json.getInteger("maximum_potency");

        List<RPGLResource> resources = super.getTarget().getResourceObjects();
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        for (Integer i : indices) {
            RPGLResource resource = resources.get(i);
            if (count > 0
                    && !resource.getExhausted()
                    && Objects.equals(resourceId, resource.getId())
                    && resource.getPotency() >= minimumPotency
                    && resource.getPotency() <= maximumPotency) {
                resource.exhaust();
                count--;
            }
        }
    }

}
