package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This Subevent is dedicated to communicating the occurrence of some non-functional but informative change, such as
 * starting a turn, ending a turn, running out of temporary hit points, etc. This type of Subevent allows for a more
 * flexible degree of responsiveness to be achieved than would be possible through the use of functional Subevents alone.
 * <br>
 * <br>
 * Source: an RPGLObject undergoing a non-functional change
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class InfoSubevent extends Subevent implements CancelableSubevent {

    public InfoSubevent() {
        super("info_subevent");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new InfoSubevent();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new InfoSubevent();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void cancel() {
        this.json.putBoolean("canceled", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !Objects.requireNonNullElse(this.json.getBoolean("canceled"), false);
    }

}
