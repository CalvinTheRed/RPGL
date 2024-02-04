package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLEffect objects. Data stored in this
 * object is copied and then processed to create a specific RPGLEffect defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTemplate extends RPGLTemplate {

    public RPGLEffectTemplate() {
        super();
    }

    public RPGLEffectTemplate(JsonObject other) {
        this();
        this.join(other);
    }

    @Override
    public RPGLEffect newInstance() {
        RPGLEffect effect = new RPGLEffect();
        this.setup(effect);
        UUIDTable.register(effect);
        return effect;
    }

    @Override
    public void setup(JsonObject effect) {
        super.setup(effect);
    }

    @Override
    public RPGLEffectTemplate applyBonuses(JsonArray bonuses) {
        return new RPGLEffectTemplate(super.applyBonuses(bonuses));
    }

}
