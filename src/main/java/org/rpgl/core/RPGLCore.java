package org.rpgl.core;

import org.rpgl.condition.Condition;
import org.rpgl.function.Function;
import org.rpgl.math.Die;
import org.rpgl.subevent.Subevent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is a high-level controller for RPGL, intended to make it easier for a client program to interface wit
 * necessary methods used to prepare RPGL for use.
 *
 * @author Calvin Withun
 */
public final class RPGLCore {

    static{
        // this is here for slf4j to generate distinct logging files for each instance of RPGL that is run
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

    /**
     * This method initializes all Conditions, Functions, and Subevents used by RPGL, as well as configures Die to not
     *	operate in testing mode. This method must be called in order for RPGL to function.
     */
    @SuppressWarnings("unused") // this is only intended to be used by a client, as it disables testing-only features
    public static void initialize() {
        Condition.initialize(false);
        Function.initialize(false);
        Subevent.initialize(false);
        Die.setTesting(false);
    }

    /**
     * This method initializes all Conditions, Functions, and Subevents used by RPGL (including testing-only options),
     * as well as configures Die to operate in testing mode. This method must be called in order for RPGL to function.
     */
    public static void initializeTesting() {
        Condition.initialize(true);
        Function.initialize(true);
        Subevent.initialize(true);
        Die.setTesting(true);
    }

}
