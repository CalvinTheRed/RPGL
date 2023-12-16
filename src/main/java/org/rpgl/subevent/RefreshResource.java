package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This Subevent is dedicated to refreshing a number of RPGLResources according to their Subevent ID and potency. This
 * Subevent allows for prioritization by high, low, or random potency, as well as bounding the potencies which can be
 * refreshed.
 * <br>
 * <br>
 * source: a RPGLObject causing for resources to be refreshed
 * <br>
 * target: a RPGLObject whose resources are being refreshed
 *
 * @author Calvin Withun
 */
public class RefreshResource extends Subevent {

    public RefreshResource() {
        super("refresh_resource");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new RefreshResource();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new RefreshResource();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.asMap().putIfAbsent("count", Integer.MAX_VALUE);
        this.json.asMap().putIfAbsent("maximum_potency", 0);
        this.json.asMap().putIfAbsent("minimum_potency", Integer.MAX_VALUE);
        this.json.asMap().putIfAbsent("selection_mode", "low_first");
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) {
        switch (this.json.getString("selection_mode")) {
            case "low_first" -> this.runLowFirst();
            case "high_first" -> this.runHighFirst();
            case "random" -> this.runRandom();
        }
    }

    /**
     * This helper method is a version of the run method which prioritizes refreshing exhausted resources from lowest
     * potency to highest.
     */
    void runLowFirst() {
        String resourceTag = this.json.getString("resource_tag");
        final int[] count = { this.json.getInteger("count") };
        final int minimumPotency = this.json.getInteger("minimum_potency");
        final int maximumPotency = this.json.getInteger("maximum_potency");

        this.getTarget().getResourceObjects().stream().sorted(Comparator.comparing(RPGLResource::getPotency)).forEach(resource -> {
            if (count[0] > 0
                    && resource.getExhausted()
                    && resource.hasTag(resourceTag)
                    && resource.getPotency() >= minimumPotency
                    && resource.getPotency() <= maximumPotency) {
                resource.refresh();
                count[0]--;
            }
        });
    }

    /**
     * This helper method is a version of the run method which prioritizes refreshing exhausted resources from highest
     * potency to lowest.
     */
    void runHighFirst() {
        String resourceTag = this.json.getString("resource_tag");
        final int[] count = { this.json.getInteger("count") };
        final int minimumPotency = this.json.getInteger("minimum_potency");
        final int maximumPotency = this.json.getInteger("maximum_potency");

        this.getTarget().getResourceObjects().stream().sorted(Collections.reverseOrder(Comparator.comparing(RPGLResource::getPotency))).forEach(resource -> {
            if (count[0] > 0
                    && resource.getExhausted()
                    && resource.hasTag(resourceTag)
                    && resource.getPotency() >= minimumPotency
                    && resource.getPotency() <= maximumPotency) {
                resource.refresh();
                count[0]--;
            }
        });
    }

    /**
     * This helper method is a version of the run method which prioritizes refreshing exhausted resources in a random
     * order.
     */
    void runRandom() {
        String resourceTag = this.json.getString("resource_tag");
        int count = this.json.getInteger("count");
        int minimumPotency = this.json.getInteger("minimum_potency");
        int maximumPotency = this.json.getInteger("maximum_potency");

        List<RPGLResource> resources = this.getTarget().getResourceObjects();
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        for (Integer i : indices) {
            RPGLResource resource = resources.get(i);
            if (count > 0
                    && resource.getExhausted()
                    && resource.hasTag(resourceTag)
                    && resource.getPotency() >= minimumPotency
                    && resource.getPotency() <= maximumPotency) {
                resource.refresh();
                count--;
            }
        }
    }

}
