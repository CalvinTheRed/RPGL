package org.rpgl.subevent;

/**
 * This interface is to be used by Subevents which include damage types.
 *
 * @author Calvin Withun
 */
public interface DamageTypeSubevent {

    /**
     * Returns whether a given damage type is included in the Subevent.
     *
     * @param damageType a damage type
     * @return true if the given damage type is included in the Subevent
     */
    boolean includesDamageType(String damageType);

}
