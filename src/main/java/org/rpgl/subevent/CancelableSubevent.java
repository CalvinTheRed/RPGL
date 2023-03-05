package org.rpgl.subevent;

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
