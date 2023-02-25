package org.rpgl.exception;

/**
 * This exception should be thrown if the json data is a Contest's target_contest field stores invalid data.
 *
 * @author Calvin Withun
 */
public class ContestTargetFormatException extends Exception {

    public ContestTargetFormatException(String found) {
        super(String.format("Expected subevent of type [ability_check] or [calculate_save_difficulty_class] but found [%s] instead",
                found
        ));
    }

}
