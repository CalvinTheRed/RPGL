package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class DummySubevent extends Subevent {

    public DummySubevent() {
        super("dummy_subevent");
    }

    @Override
    public Subevent clone() {
        return new DummySubevent();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new DummySubevent();
        clone.joinSubeventJson(subeventJson);
        return clone;
    }

}
