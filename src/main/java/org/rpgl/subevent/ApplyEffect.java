package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.uuidtable.UUIDTable;

public class ApplyEffect extends Subevent {

    static {
        Subevent.SUBEVENTS.put("apply_effect", new ApplyEffect());
    }

    @Override
    public Subevent clone() {
        return new ApplyEffect();
    }

    @Override
    public void invokeSubevent(long sourceUuid, long targetUuid, JsonObject data) throws SubeventMismatchException {
        super.verifySubevent("apply_effect", data);
        RPGLEffect effect = RPGLFactory.newEffect((String) data.get("effect"));
        effect.put("source", sourceUuid);
        effect.put("target", targetUuid);
        UUIDTable.getObject(targetUuid).addEffect(effect);
    }

}
