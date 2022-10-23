package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class TestSubevent extends Subevent {

    private static final String SUBEVENT_ID = "test_subevent";

    public TestSubevent() {
        super(SUBEVENT_ID);
    }

    @Override
    public Subevent clone() {
        return new TestSubevent();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new TestSubevent();
        clone.joinSubeventJson(subeventJson);
        return clone;
    }

}
