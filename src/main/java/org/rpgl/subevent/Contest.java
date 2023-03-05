package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.exception.ContestTargetFormatException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This subevent is dedicated to facilitating contests where one RPGLObject makes an ability check against another
 * RPGLObject's ability check, a save DC, or a static value. The source can win, or the target can win. Oftentimes,
 * further subevents are only invoked if the source wins.
 * <br>
 * <br>
 * Source: an RPGLObject initiating a contest
 * <br>
 * Target: an RPGLObject being challenged to a contest
 *
 * @author Calvin Withun
 */
public class Contest extends Subevent {

    // TODO how can an ObjectHasTag Condition be evaluated against the target of a Contest?
    // TODO how can this fit in with the CancelableSubevent interface?

    private static final Logger LOGGER = LoggerFactory.getLogger(Contest.class);

    public Contest() {
        super("contest");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new Contest();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new Contest();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        // when target is a static value or a save difficulty class, source wins by matching or exceeding target.
        // when target is an ability check, source wins only by exceeding target.
        boolean mustExceedTarget;
        try {
          mustExceedTarget = this.json.getJsonObject("target_contest").getString("subevent").equals("ability_check");
        } catch (NullPointerException e) {
            mustExceedTarget = false;
        }
        this.json.putBoolean("must_exceed_target", mustExceedTarget);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        int sourceResult = this.getSourceResult(context);
        int targetResult = this.getTargetResult(context);
        if (this.json.getBoolean("must_exceed_target")) {
            targetResult++;
        }

        if (sourceResult >= targetResult) {
            this.resolveNestedSubevents("source_wins", context);
        } else {
            this.resolveNestedSubevents("target_wins", context);
        }
    }

    /**
     * This helper method returns the result of the source.
     *
     * @param context the context in which the subevent is invoked
     * @return the source's result
     *
     * @throws Exception if an exception occurs
     */
    int getSourceResult(RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(this.json.getJsonObject("source_contest"));
        abilityCheck.setSource(this.getSource());
        abilityCheck.prepare(context);
        abilityCheck.json.getJsonArray("tags").asList().addAll(this.json.getJsonArray("tags").asList());
        abilityCheck.setTarget(this.getSource());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    /**
     * This helper method returns the result of the target.
     *
     * @param context the context in which the subevent is invoked
     * @return the target's result
     * @throws Exception if an exception occurs
     */
    int getTargetResult(RPGLContext context) throws Exception {
        Integer targetResult = this.json.getInteger("target_contest");
        if (targetResult != null) {
            return targetResult;
        }

        JsonObject targetContestJson = this.json.getJsonObject("target_contest");
        String subeventId = targetContestJson.getString("subevent");
        if (subeventId.equals("calculate_save_difficulty_class")) {
            return this.getTargetResultAsSaveDifficultyClass(targetContestJson, context);
        } else if (subeventId.equals("ability_check")) {
            return this.getTargetResultAsAbilityCheck(targetContestJson, context);
        }

        ContestTargetFormatException e = new ContestTargetFormatException(subeventId);
        LOGGER.error(e.getMessage());
        throw e;
    }

    /**
     * This helper method returns the result of the target when it is a save difficulty class calculation.
     *
     * @param targetJson the JSON data defining the target's contribution to the Contest
     * @param context    the context in which the subevent is invoked
     * @return the target's result
     * @throws Exception if an exception occurs
     */
    int getTargetResultAsSaveDifficultyClass(JsonObject targetJson, RPGLContext context) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        calculateSaveDifficultyClass.joinSubeventData(targetJson);
        calculateSaveDifficultyClass.setSource(this.getTarget());
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(this.getTarget());
        calculateSaveDifficultyClass.invoke(context);
        return calculateSaveDifficultyClass.get();
    }

    /**
     * This helper method returns the result of the target when it is an ability check.
     *
     * @param targetJson the JSON data defining the target's contribution to the Contest
     * @param context the context in which the subevent is invoked
     * @return the target's result
     * @throws Exception if an exception occurs
     */
    int getTargetResultAsAbilityCheck(JsonObject targetJson, RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(targetJson);
        abilityCheck.setSource(this.getTarget());
        abilityCheck.prepare(context);
        abilityCheck.json.getJsonArray("tags").asList().addAll(this.json.getJsonArray("tags").asList());
        abilityCheck.setTarget(this.getTarget());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    /**
     * This helper method resolves the subevents for the source or the target, according to the victor of the contest.
     *
     * @param whoWins a key indicating who won the contest (<code>source_wins</code> or <code>target_wins</code>)
     * @param context the context in which the subevent is invoked
     * @throws Exception if an exception occurs
     */
    void resolveNestedSubevents(String whoWins, RPGLContext context) throws Exception {
        JsonArray subeventJsonArray = Objects.requireNonNullElse(this.json.getJsonArray(whoWins), new JsonArray());
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject nestedSubeventJson = subeventJsonArray.getJsonObject(i);
            Subevent subevent = Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson);
            subevent.setSource(this.getSource());
            subevent.prepare(context);
            subevent.setTarget(this.getTarget());
            subevent.invoke(context);
        }
    }

}
