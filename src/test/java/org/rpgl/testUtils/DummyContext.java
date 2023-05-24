package org.rpgl.testUtils;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;

/**
 * A dummy class meant to help with testing.
 *
 * @author Calvin Withun
 */
public class DummyContext extends RPGLContext {
    private boolean isTurn = false;

    @Override
    public boolean isObjectsTurn(RPGLObject object) {
        return this.isTurn;
    }

    public void setIsTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
}
