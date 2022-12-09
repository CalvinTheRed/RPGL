package org.rpgl.scenarios;

import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.math.Die;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleScenarios {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        Die.setTesting(true);
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
    @DisplayName("Dragon fighting 5 Knights")
    void dragonFightingKnights() throws Exception {
        RPGLObject dragon  = Objects.requireNonNull(RPGLFactory.newObject("demo:young_red_dragon"));
        RPGLObject knight1 = Objects.requireNonNull(RPGLFactory.newObject("demo:knight"));
        RPGLObject knight2 = Objects.requireNonNull(RPGLFactory.newObject("demo:knight"));
        RPGLObject knight3 = Objects.requireNonNull(RPGLFactory.newObject("demo:knight"));
        RPGLObject knight4 = Objects.requireNonNull(RPGLFactory.newObject("demo:knight"));
        RPGLObject knight5 = Objects.requireNonNull(RPGLFactory.newObject("demo:knight"));

        RPGLContext context = new RPGLContext();
        context.add(dragon);
        context.add(knight1);
        context.add(knight2);
        context.add(knight3);
        context.add(knight4);
        context.add(knight5);

        // Dragon's turn!

        // The dragon ponders what it can do...
        assertEquals(3, dragon.getEvents().length,
                "Dragon has 3 options."
        );

        // The dragon chooses to breathe fire!
        dragon.invokeEvent(
                new RPGLObject[] { knight1, knight2, knight3, knight4, knight5 },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:young_red_dragon_breath")),
                context
        );

        assertEquals(4L, knight1.seek("health_data.current"),
                "Knight should have taken 48 damage (52-48=4)"
        );
        assertEquals(4L, knight2.seek("health_data.current"),
                "Knight should have taken 48 damage (52-48=4)"
        );
        assertEquals(4L, knight3.seek("health_data.current"),
                "Knight should have taken 48 damage (52-48=4)"
        );
        assertEquals(4L, knight4.seek("health_data.current"),
                "Knight should have taken 48 damage (52-48=4)"
        );
        assertEquals(4L, knight5.seek("health_data.current"),
                "Knight should have taken 48 damage (52-(16x[6])=4)"
        );

        // Knights' turns!

        knight1.invokeEvent(
                new RPGLObject[] { dragon },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );
        knight2.invokeEvent(
                new RPGLObject[] { dragon },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );
        knight3.invokeEvent(
                new RPGLObject[] { dragon },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );
        knight4.invokeEvent(
                new RPGLObject[] { dragon },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );
        knight5.invokeEvent(
                new RPGLObject[] { dragon },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );

        assertEquals(133L, dragon.seek("health_data.current"),
                "Dragon should have taken 48 damage (178-(5x([3+3]+3))=133)"
        );
    }

    @Test
    @DisplayName("Kobold fighting a Knight")
    void koboldFightingKnight() throws Exception {
        RPGLObject kobold = Objects.requireNonNull(RPGLFactory.newObject("demo:kobold"));
        RPGLItem koboldDagger = UUIDTable.getItem((String) kobold.seek("items.hand_1"));
        RPGLObject knight = Objects.requireNonNull(RPGLFactory.newObject("demo:knight"));

        RPGLContext context = new RPGLContext();
        context.add(kobold);
        context.add(knight);

        // Kobold's turn!

        // This kobold is not smart, and wants to attack using str instead of using dex
        koboldDagger.setAttackAbility("melee", "str");
        kobold.invokeEvent(
                new RPGLObject[] { knight },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );

        assertEquals(52L, knight.seek("health_data.current"),
                "Knight should have taken 0 damage (kobold missed because it has a bad str modifier for the attack)"
        );

        // Having learned from his mistake, the Kobold switches back to attacking with dex instead of str
        koboldDagger.setAttackAbility("melee", "dex");
        kobold.invokeEvent(
                new RPGLObject[] { knight },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );

        assertEquals(48L, knight.seek("health_data.current"),
                "Knight should have taken 4 damage (52-([2]+2)=48)"
        );

        // Knight's turn!

        knight.invokeEvent(
                new RPGLObject[] { kobold },
                Objects.requireNonNull(RPGLFactory.newEvent("demo:hand_1_attack")),
                context
        );

        // This would kill the Kobold
        assertEquals(-4L, kobold.seek("health_data.current"),
                "Kobold should have taken 9 damage (5-([3+3]+3)=-4)"
        );
    }

}
