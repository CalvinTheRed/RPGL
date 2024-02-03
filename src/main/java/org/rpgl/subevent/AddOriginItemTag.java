package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This Subevent is dedicated to adding a tag to an item (specifically the origin item of an event).
 * <br>
 * <br>
 * Source: an RPGLObject adding a tag to an origin item
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class AddOriginItemTag extends Subevent {

    public AddOriginItemTag() {
        super("add_origin_item_tag");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AddOriginItemTag();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AddOriginItemTag();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public AddOriginItemTag invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (AddOriginItemTag) super.invoke(context, originPoint);
    }

    @Override
    public AddOriginItemTag joinSubeventData(JsonObject other) {
        return (AddOriginItemTag) super.joinSubeventData(other);
    }

    @Override
    public AddOriginItemTag run(RPGLContext context, JsonArray originPoint) {
        RPGLItem originItem = UUIDTable.getItem(super.getOriginItem());
        if (originItem != null) {
            originItem.addTag(this.json.getString("tag"));
        }
        return this;
    }

    @Override
    public AddOriginItemTag setOriginItem(String originItem) {
        return (AddOriginItemTag) super.setOriginItem(originItem);
    }

    @Override
    public AddOriginItemTag setSource(RPGLObject source) {
        return (AddOriginItemTag) super.setSource(source);
    }

    @Override
    public AddOriginItemTag setTarget(RPGLObject target) {
        return (AddOriginItemTag) super.setTarget(target);
    }
}
