package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new InfoSubevent();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public InfoSubevent invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (InfoSubevent) super.invoke(context, originPoint);
    }

    @Override
    public InfoSubevent joinSubeventData(JsonObject other) {
        return (InfoSubevent) super.joinSubeventData(other);
    }

    @Override
    public InfoSubevent prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        return this;
    }

    @Override
    public InfoSubevent run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public InfoSubevent setOriginItem(String originItem) {
        return (InfoSubevent) super.setOriginItem(originItem);
    }

    @Override
    public InfoSubevent setSource(RPGLObject source) {
        return (InfoSubevent) super.setSource(source);
    }

    @Override
    public InfoSubevent setTarget(RPGLObject target) {
        return (InfoSubevent) super.setTarget(target);
    }

    @Override
    public void cancel() {
        this.json.putBoolean("canceled", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !this.json.getBoolean("canceled");
    }

}
