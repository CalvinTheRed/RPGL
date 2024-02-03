package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This Subevent is dedicated to removing a tag from an item (specifically the origin item of an event).
 * <br>
 * <br>
 * Source: an RPGLObject removing a tag from an origin item
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class RemoveOriginItemTag extends Subevent {

    public RemoveOriginItemTag() {
        super("remove_origin_item_tag");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new RemoveOriginItemTag();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new RemoveOriginItemTag();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public RemoveOriginItemTag invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (RemoveOriginItemTag) super.invoke(context, originPoint);
    }

    @Override
    public RemoveOriginItemTag joinSubeventData(JsonObject other) {
        return (RemoveOriginItemTag) super.joinSubeventData(other);
    }

    @Override
    public RemoveOriginItemTag prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        return (RemoveOriginItemTag) super.prepare(context, originPoint);
    }

    @Override
    public RemoveOriginItemTag run(RPGLContext context, JsonArray originPoint) {
        RPGLItem originItem = UUIDTable.getItem(super.getOriginItem());
        if (originItem != null) {
            originItem.removeTag(this.json.getString("tag"));
        }
        return this;
    }

    @Override
    public RemoveOriginItemTag setOriginItem(String originItem) {
        return (RemoveOriginItemTag) super.setOriginItem(originItem);
    }

    @Override
    public RemoveOriginItemTag setSource(RPGLObject source) {
        return (RemoveOriginItemTag) super.setSource(source);
    }

    @Override
    public RemoveOriginItemTag setTarget(RPGLObject target) {
        return (RemoveOriginItemTag) super.setTarget(target);
    }

}
