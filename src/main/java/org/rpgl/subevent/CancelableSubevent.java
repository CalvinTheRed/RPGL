package org.rpgl.subevent;

/**
 * This interface is to be used by Subevents which can be canceled. This is meant to be used for the Subevents which can
 * be directly invoked by an RPGLEvent.
 *
 * @author Calvin Withun
 */
public interface CancelableSubevent {

    /**
     * This method "cancels" this Subevent, causing the Subevent to not complete its invoke() behavior.
     */
    void cancel();

    /**
     * This helper method returns whether the Subevent was canceled.
     *
     * @return true if the Subevent was canceled.
     */
    boolean isNotCanceled();

}
