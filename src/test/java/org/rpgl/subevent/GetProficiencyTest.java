package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.GetProficiency class.
 *
 * @author Calvin Withun
 */
public class GetProficiencyTest {

    private GetProficiency getProficiency;

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

    @BeforeEach
    void beforeEach() {
        // create an anonymous class for GetProficiency for the purpose of running tests on it
        getProficiency = new GetProficiency("get_proficiency") {

            @Override
            public Subevent clone() {
                return this;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return this;
            }

        };

    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("defaultBehavior returns all false")
    void defaultBehavior_returnsAllFalse() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);

        assertFalse(getProficiency.isHalfProficient(),
                "GetProficiency should default to not granting half proficiency"
        );
        assertFalse(getProficiency.isProficient(),
                "GetProficiency should default to not granting proficiency"
        );
        assertFalse(getProficiency.isExpert(),
                "GetProficiency should default to not granting expertise"
        );
    }

    @Test
    @DisplayName("grantHalfProficiency grants half proficiency only")
    void grantHalfProficiency_grantsHalfProficiencyOnly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantHalfProficiency();

        assertTrue(getProficiency.isHalfProficient(),
                "GetProficiency should grant half proficiency after calling grantHalfProficiency()"
        );
        assertFalse(getProficiency.isProficient(),
                "GetProficiency should not grant proficiency after calling grantHalfProficiency"
        );
        assertFalse(getProficiency.isExpert(),
                "GetProficiency should not grant expertise after calling grantHalfProficiency"
        );
    }

    @Test
    @DisplayName("grant and revoke half proficiency does not grant half proficiency")
    void grantAndRevokeHalfProficiency_doesNotGrantHalfProficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantHalfProficiency();
        getProficiency.revokeHalfProficiency();

        assertFalse(getProficiency.isHalfProficient(),
                "GetProficiency should not grant half proficiency after having it revoked()"
        );
    }

    @Test
    @DisplayName("grant and revoke proficiency does not grant proficiency")
    void grantAndRevokeProficiency_doesNotGrantProficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantProficiency();
        getProficiency.revokeProficiency();

        assertFalse(getProficiency.isProficient(),
                "GetProficiency should not grant proficiency after having it revoked()"
        );
    }

    @Test
    @DisplayName("grant and revoke expertise does not grant expertise")
    void grantAndRevokeExpertise_doesNotGrantExpertise() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantExpertise();
        getProficiency.revokeExpertise();

        assertFalse(getProficiency.isExpert(),
                "GetProficiency should not grant expertise after having it revoked()"
        );
    }

    @Test
    @DisplayName("grantProficiency revokes half proficiency")
    void grantProficiency_revokesHalfProficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantHalfProficiency();
        getProficiency.grantProficiency();

        assertFalse(getProficiency.isHalfProficient(),
                "GetProficiency should not grant half proficiency after being granted proficiency"
        );
    }

    @Test
    @DisplayName("grantExpertise revokes half proficiency")
    void grantExpertise_revokesHalfProficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantHalfProficiency();
        getProficiency.grantExpertise();

        assertFalse(getProficiency.isHalfProficient(),
                "GetProficiency should not grant half proficiency after being granted expertise"
        );
    }

    @Test
    @DisplayName("grantExpertise revokes proficiency")
    void grantExpertise_revokesProficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        getProficiency.setSource(source);
        getProficiency.prepare(context);
        getProficiency.grantProficiency();
        getProficiency.grantExpertise();

        assertFalse(getProficiency.isProficient(),
                "GetProficiency should not grant proficiency after being granted expertise"
        );
    }

}
