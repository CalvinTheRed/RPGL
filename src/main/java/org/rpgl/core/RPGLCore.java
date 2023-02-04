package org.rpgl.core;

import org.rpgl.condition.Condition;
import org.rpgl.function.Function;
import org.rpgl.subevent.Subevent;

/**
 * This class is a high-level controller for RPGL, intended to make it easier for a client program to interface wit
 * necessary methods used to prepare RPGL for use.
 *
 * @author Calvin Withun
 */
public final class RPGLCore {

    /**
     * 	<p><b><i>initialize</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static void initialize()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method initializes all Conditions, Functions, and Subevents used by RPGL. This method must be called in
     * 	order for RPGL to function.
     * 	</p>
     */
    public static void initialize() {
        Condition.initialize(false);
        Function.initialize(false);
        Subevent.initialize(false);
    }

    /**
     * 	<p><b><i>initializeTesting</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static void initializeTesting()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method initializes all Conditions, Functions, and Subevents used by RPGL. This method must be called in
     * 	order for RPGL to function. This method initializes testing-only options which are not designed to be used by a
     * 	client program.
     * 	</p>
     */
    public static void initializeTesting() {
        Condition.initialize(true);
        Function.initialize(true);
        Subevent.initialize(true);
    }

}
