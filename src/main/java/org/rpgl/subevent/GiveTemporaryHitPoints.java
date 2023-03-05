package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.Objects;

public class GiveTemporaryHitPoints extends Subevent implements CancelableSubevent {

    public GiveTemporaryHitPoints() {
        super("give_temporary_hit_points");
    }

    @Override
    public Subevent clone() {
        return null;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        return null;
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        if (this.isNotCanceled()) {
            int newTemporaryHitPoints = this.getTemporaryHitPoints(this.unpackTemporaryHitPointsData(
                    this.json.getJsonObject("temporary_hit_points")
            ));
            RPGLObject target = this.getTarget();
            JsonObject targetHealthData = target.getHealthData();
            if (newTemporaryHitPoints > targetHealthData.getInteger("temporary")) {
                // TODO make this an optional accept-or-reject sort of thing
                targetHealthData.putInteger("temporary", newTemporaryHitPoints);
                JsonArray riderEffects = this.json.getJsonArray("rider_effects");
                for (int i = 0; i < riderEffects.size(); i++) {
                    RPGLEffect effect = RPGLFactory.newEffect(riderEffects.getString(i));
                    target.addEffect(effect);
                }
            }
        }
    }

    @Override
    public void cancel() {
        this.json.putBoolean("cancel", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !Objects.requireNonNullElse(this.json.getBoolean("cancel"), false);
    }

    /**
     * This helper method unpacks all dice used in determining the quantity of temporary hit points granted by this
     * Subevent and returns the unpacked representation
     *
     * @param temporaryHitPointsData compacted JSON data indicating how many temporary hit points are generated by this
     *                               Subevent.
     * @return an unpacked representation of temporaryHitPointsData
     */
    JsonObject unpackTemporaryHitPointsData(JsonObject temporaryHitPointsData) {
        JsonObject unpackedTemporaryHitPointsData = new JsonObject();
        unpackedTemporaryHitPointsData.putInteger("bonus", temporaryHitPointsData.getInteger("bonus"));
        JsonArray dice = temporaryHitPointsData.getJsonArray("dice");
        JsonArray unpackedDice = new JsonArray();
        for (int i = 0; i < dice.size(); i++) {
            JsonObject die = dice.getJsonObject(i);
            JsonObject unpackedDie = new JsonObject() {{
                this.putInteger("size", die.getInteger("size"));
                this.putJsonArray("determined", die.getJsonArray("determined"));
            }};
            for (int j = 0; j < die.getInteger("count"); j++) {
                unpackedDice.addJsonObject(unpackedDie.deepClone());
            }
        }
        unpackedTemporaryHitPointsData.putJsonArray("dice", unpackedDice);
        return unpackedTemporaryHitPointsData;
    }

    /**
     * This helper method looks at the dice and bonus to find the total temporary hit points generated by this Subevent.
     *
     * @param temporaryHitPointsData JSON data indicating how many temporary hit points are generated by this Subevent.
     * @return a quantity of temporary hit points
     */
    int getTemporaryHitPoints(JsonObject temporaryHitPointsData) {
        int sum = Objects.requireNonNullElse(temporaryHitPointsData.getInteger("bonus"), 0);
        JsonArray diceArray = Objects.requireNonNullElse(temporaryHitPointsData.getJsonArray("dice"), new JsonArray());
        for (int i = 0; i < diceArray.size(); i++) {
            JsonObject die = diceArray.getJsonObject(i);
            sum += Die.roll(die.getInteger("size"), die.getJsonArray("determined").asList());
        }
        return sum;
    }
}
