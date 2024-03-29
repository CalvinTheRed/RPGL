package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AttackAbilityCollection;
import org.rpgl.subevent.Subevent;

/**
 * This Function is dedicated to adding abilities to AttackAbilityCollection subevents.
 *
 * @author Calvin Withun
 */
public class AddAttackAbility extends Function {

    public AddAttackAbility() {
        super("add_attack_ability");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof AttackAbilityCollection attackAbilityCollection) {
            String attackAbility = functionJson.getString("ability");
            if (!attackAbilityCollection.getAbilities().asList().contains(attackAbility)) {
                attackAbilityCollection.addAbility(attackAbility);
            }
        }
    }

}
