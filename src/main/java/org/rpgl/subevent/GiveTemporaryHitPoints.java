package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
            int newTemporaryHitPoints = this.json.getInteger("temporary_hit_points");
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
}
