package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.core.RPGLContext class.
 *
 * @author Calvin Withun
 */
public class RPGLContextTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        RPGLCore.initializeTesting();
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("rollObjectInitiative initiative roll is within expected bounds")
    void rollObjectInitiative_initiativeRollIsWithinExpectedBounds() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();

        double initiativeScore = context.rollObjectInitiative(object);
        double lowerBound = 10.10;
        double upperBound = 10.11;
        assertTrue(lowerBound <= initiativeScore && initiativeScore < upperBound,
                "initiative score should be 10 + 0.10 + following random digits"
        );
    }

    @Test
    @DisplayName("currentObject returns highest initiative object when not yet called")
    void currentObject_returnsHighestInitiativeObjectWhenNotYetCalled() throws Exception {
        RPGLObject object1 = RPGLFactory.newObject("demo:commoner");
        RPGLObject object2 = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object1, 10);
        context.add(object2, 12);

        assertEquals(object2, context.currentObject(),
                "the object with the highest initiative should be returned the first time currentObject() is called"
        );
    }

    @Test
    @DisplayName("nextObject returns highest initiative object when not yet called")
    void nextObject_returnsHighestInitiativeObjectWhenNotYetCalled() throws Exception {
        RPGLObject object1 = RPGLFactory.newObject("demo:commoner");
        RPGLObject object2 = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object1, 10);
        context.add(object2, 12);

        assertEquals(object2, context.nextObject(),
                "the object with the highest initiative should be returned the first time nextObject() is called"
        );
    }

    @Test
    @DisplayName("nextObject returns objects in initiative order including cycling")
    void nextObject_returnsObjectsInInitiativeOrderIncludingCycling() throws Exception {
        RPGLObject object1 = RPGLFactory.newObject("demo:commoner");
        RPGLObject object2 = RPGLFactory.newObject("demo:commoner");
        RPGLObject object3 = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object2, 12);
        context.add(object3, 13);
        context.add(object1, 11);

        assertEquals(object3, context.nextObject(),
                "the object with the highest initiative should be returned the first time nextObject() is called"
        );

        assertEquals(object2, context.nextObject(),
                "the object with the second highest initiative should be returned the second time nextObject() is called"
        );

        assertEquals(object1, context.nextObject(),
                "the object with the third highest initiative should be returned the third time nextObject() is called"
        );

        assertEquals(object3, context.nextObject(),
                "the object with the highest initiative should be returned when nextObject() is called, if the last call returned the lowest initiative"
        );
    }

    @Test
    @DisplayName("remove removes object from context and from turn order")
    void remove_removesObjectFromContextAndFromTurnOrder() throws Exception {
        RPGLObject object1 = RPGLFactory.newObject("demo:commoner");
        RPGLObject object2 = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object1, 11);
        context.add(object2, 12);

        context.currentObject();
        context.remove(object2);

        assertEquals(object1, context.nextObject(),
                "nextObject should immediately loop back to first object when second object is removed"
        );
    }

}
