package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AddOriginItemTag();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context) {
        RPGLItem originItem = UUIDTable.getItem(this.getOriginItem());
        if (originItem != null) {
            originItem.addTag(this.json.getString("tag"));
        }
    }
}
