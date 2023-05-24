package org.rpgl.testUtils;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.subevent.Subevent;

/**
 * A dummy class meant to help with testing.
 *
 * @author Calvin Withun
 */
public class DummyContext extends RPGLContext {
    @Override
    public boolean isObjectsTurn(RPGLObject object) {
        return false;
    }

    @Override
    public void viewCompletedSubevent(Subevent subevent) {

    }
}
