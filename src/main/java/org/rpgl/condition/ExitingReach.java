package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Movement;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Condition is dedicated to determining whether an object is moving out of the reach of another object.
 *
 * @author Calvin Withun
 */
public class ExitingReach extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExitingReach.class);

    public ExitingReach() {
        super("exiting_reach");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        if (subevent instanceof Movement movement) {
            double reach = effect.getTarget().getReach(context);
            String algorithm = Objects.requireNonNullElse(conditionJson.getString("algorithm"), "direct");
            double startingDistance = Condition.getDistance(
                    effect.getTarget().getPosition(),
                    movement.getTarget().getPosition(),
                    algorithm
            );
            double endingDistance = Condition.getDistance(
                    effect.getTarget().getPosition(),
                    originPoint,
                    algorithm
            );
            return startingDistance <= reach && endingDistance > reach;
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
