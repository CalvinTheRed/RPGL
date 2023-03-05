package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;

/**
 * This interface is used by all Subevents which are associated with a particular ability score.
 *
 * @author Calvin Withun
 */
public interface AbilitySubevent {

    /**
     * Returns the ability score name this Subevent is associated with.
     *
     * @param context the context ion which the Subevent is being used
     * @return an ability score name
     */
    String getAbility(RPGLContext context);

}
