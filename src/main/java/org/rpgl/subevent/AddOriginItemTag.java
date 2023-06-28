package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

public class AddOriginItemTag extends Subevent {

    public AddOriginItemTag() {
        super("add_origin_item_tag");
        this.addTag("add_origin_item_tag");
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
