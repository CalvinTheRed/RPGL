package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubeventTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("recognizes new effect")
    void recognizesNewEffect() {
        Subevent subevent = new DummySubevent();
        RPGLEffect effect = RPGLFactory.newEffect("debug:blank");

        assertFalse(subevent.effectAlreadyApplied(effect),
                "effect has not already been applied"
        );
    }

    @Test
    @DisplayName("recognizes applied effect")
    void recognizesAppliedEffect() {
        Subevent subevent = new DummySubevent();
        RPGLEffect effect = RPGLFactory.newEffect("debug:blank");
        subevent.addModifyingEffect(effect);

        assertTrue(subevent.effectAlreadyApplied(effect),
                "effect has already been applied"
        );
    }

    @Test
    @DisplayName("recognizes duplicate effect (duplicates not allowed)")
    void recognizesDuplicateEffect_duplicatesNotAllowed() {
        Subevent subevent = new DummySubevent();
        RPGLEffect effect = RPGLFactory.newEffect("debug:blank");
        RPGLEffect duplicate = RPGLFactory.newEffect("debug:blank");
        subevent.addModifyingEffect(effect);

        assertTrue(subevent.effectAlreadyApplied(duplicate),
                "effect is a disallowed duplicate"
        );
    }

    @Test
    @DisplayName("recognizes duplicate effect (duplicates allowed)")
    void recognizesDuplicateEffect_duplicatesAllowed() {
        Subevent subevent = new DummySubevent();
        RPGLEffect effect = RPGLFactory.newEffect("debug:blank");
        effect.addTag("allow_duplicates");
        RPGLEffect duplicate = RPGLFactory.newEffect("debug:blank");
        duplicate.addTag("allow_duplicates");
        subevent.addModifyingEffect(effect);

        assertFalse(subevent.effectAlreadyApplied(duplicate),
                "effect is an allowed duplicate"
        );
    }

    @Test
    @DisplayName("recognizes applied effect (duplicates allowed)")
    void recognizesAppliedEffect_duplicatesAllowed() {
        Subevent subevent = new DummySubevent();
        RPGLEffect effect = RPGLFactory.newEffect("debug:blank");
        effect.addTag("allow_duplicates");
        subevent.addModifyingEffect(effect);

        assertTrue(subevent.effectAlreadyApplied(effect),
                "effect has already been applied"
        );
    }

    @Test
    @DisplayName("recognizes deleted duplicate effect")
    void recognizesDeletedDuplicateEffect() {
        Subevent subevent = new DummySubevent();
        RPGLEffect effect = RPGLFactory.newEffect("debug:blank");
        RPGLEffect duplicate = RPGLFactory.newEffect("debug:blank");
        subevent.addModifyingEffect(effect);
        UUIDTable.unregister(effect.getUuid());

        assertTrue(subevent.effectAlreadyApplied(duplicate),
                "effect duplicates a deleted effect"
        );
    }

}
