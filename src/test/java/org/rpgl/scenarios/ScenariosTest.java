package org.rpgl.scenarios;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing class for miscellaneous scenarios. Tests here are designed to stress test RPGL at a high level.
 *
 * @author Calvin Withun
 */
public class ScenariosTest {

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
    @DisplayName("flametongue test")
    void flametongueTest() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("bugtest:dummy");
        RPGLItem flametongue = RPGLFactory.newItem("std:flametongue_scimitar");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.giveItem(flametongue.getUuid());
        source.equipItem(flametongue.getUuid(), "mainhand");

        RPGLEvent flametongueAttack = TestUtils.getEventById(source.getEventObjects(context), "std:scimitar_melee");
        assertNotNull(flametongueAttack);

//        source.invokeEvent(
//                new RPGLObject[] {
//                        target
//                },
//                flametongueAttack,
//                new ArrayList<>() {{
//                    this.add(TestUtils.getResourceById(source.getResourceObjects(), "std:action"));
//                }},
//                context
//        );
    }

}
