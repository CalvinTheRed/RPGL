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
 * This Condition is dedicated to checking the distance between two points.
 *
 * @author Calvin Withun
 */
public class CheckDistance extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckDistance.class);

    public CheckDistance() {
        super("check_distance");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        if (subevent instanceof Movement movement) {
            JsonObject fromValue = conditionJson.getJsonObject("from");
            JsonArray from = fromValue == null
                    ? originPoint
                    : RPGLEffect.getObject(effect, movement, fromValue).getPosition();
            JsonArray to = RPGLEffect.getObject(effect, movement, conditionJson.getJsonObject("to")).getPosition();
            String algorithm = Objects.requireNonNullElse(conditionJson.getString("algorithm"), "direct");
            return Condition.compareValues(
                    Condition.getDistance(from, to, algorithm),
                    conditionJson.getDouble("boundary"),
                    conditionJson.getString("comparison")
            );
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
