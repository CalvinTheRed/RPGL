package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AbilityCheck class.
 *
 * @author Calvin Withun
 */
public class AbilityCheckTest {

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("prepare adds ability_check tag")
    void prepare_addsAbilityCheckTag() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        AbilityCheckSubevent abilityCheck = new AbilityCheckSubevent();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "str"
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "str");
        }});

        abilityCheck.setSource(source);
        abilityCheck.prepare(context);

        assertTrue(abilityCheck.hasTag("ability_check"),
                "prepare should add ability_check tag to subevent"
        );
    }

    @Test
    @DisplayName("invoke adds relevant bonuses (no proficiency)")
    void invoke_addsRelevantBonuses_noProficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        AbilityCheckSubevent abilityCheck = new AbilityCheckSubevent();
        abilityCheck.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "str",
                "determined": [ 10 ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "str");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});

        abilityCheck.setSource(source);
        abilityCheck.prepare(context);
        abilityCheck.setTarget(source);
        abilityCheck.invoke(context);

        assertEquals(13, abilityCheck.get(),
                "AbilityCheck should total 13 (10+3=13)"
        );
    }

}
