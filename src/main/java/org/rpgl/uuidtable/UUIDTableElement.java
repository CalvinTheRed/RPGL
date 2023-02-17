package org.rpgl.uuidtable;

import org.rpgl.datapack.DatapackContent;
import org.rpgl.datapack.UUIDTableElementTO;

/**
 * This class is the base class of any object which gets stored in UUIDTable.
 *
 * @author Calvin Withun
 */
public class UUIDTableElement extends DatapackContent {

    /**
     * Returns the UUID of this object.
     *
     * @return the UUID of this object
     */
    public String getUuid() {
        return this.getString(UUIDTableElementTO.UUID_ALIAS);
    }


    /**
     * Sets the UUID of this object.
     *
     * @param uuid the UUID to be assigned to this object
     */
    public void setUuid(String uuid) {
        this.putString(UUIDTableElementTO.UUID_ALIAS, uuid);
    }

    /**
     * Deletes the key-value pair storing this object's UUID.
     */
    public void deleteUuid() {
        this.asMap().remove(UUIDTableElementTO.UUID_ALIAS);
    }

}
