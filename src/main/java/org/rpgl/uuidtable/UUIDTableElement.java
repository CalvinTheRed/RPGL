package org.rpgl.uuidtable;

import org.rpgl.datapack.UUIDTableElementTO;
import org.rpgl.json.JsonObject;

public class UUIDTableElement extends JsonObject {

    public String getUuid() {
        return this.getString(UUIDTableElementTO.UUID_ALIAS);
    }
    public void setUuid(String uuid) {
        this.putString(UUIDTableElementTO.UUID_ALIAS, uuid);
    }
    public void deleteUuid() {
        this.asMap().remove(UUIDTableElementTO.UUID_ALIAS);
    }

}
