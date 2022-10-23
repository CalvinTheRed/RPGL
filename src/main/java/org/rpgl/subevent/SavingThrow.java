package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;

public class SavingThrow extends Subevent {

    private static final String SUBEVENT_ID = "saving_throw";

    static {
        Subevent.SUBEVENTS.put(SUBEVENT_ID, new SavingThrow());
    }

    public SavingThrow() {
        super(SUBEVENT_ID);
    }

    @Override
    public Subevent clone() {
        return new SavingThrow();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new SavingThrow();
        clone.joinSubeventJson(subeventJson);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) {
        try {
            // calculate spell save DC
            CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
            String prepareSubeventJsonString = String.format("{" +
                            "\"subevent\":\"calculate_save_difficulty_class\"," +
                            "\"spellcasting_ability\":\"%s\"" +
                            "}",
                    this.subeventJson.get("difficulty_class_ability")
            );
            JsonObject prepareSubeventJson = JsonParser.parseObjectString(prepareSubeventJsonString);
            calculateSaveDifficultyClass.joinSubeventJson(prepareSubeventJson);
            //calculateSaveDifficultyClass.prepare(source);
            calculateSaveDifficultyClass.invoke(source, source);
            this.subeventJson.put("difficulty_class", calculateSaveDifficultyClass.getDifficultyClass());

            // TODO calculate damage - review plan of attack for generating damage values
        } catch (Exception e) {
            throw new RuntimeException("Encountered an error while preparing saving_throw subevent", e);
        }
    }

    @Override
    public void invoke(RPGLObject source, RPGLObject target) throws SubeventMismatchException {
        super.verifySubevent(SUBEVENT_ID);
        // TODO...
        // target makes a saving throw
        // compare saving throw result against dc
        // consequences of pass or fail
    }

}
