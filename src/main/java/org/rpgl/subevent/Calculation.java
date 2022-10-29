package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;

public abstract class Calculation extends Subevent {

    public Calculation(String subeventId) {
        super(subeventId);
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        this.addBonus(0L);
    }

    public void addBonus(long bonus) {
        Long currentBonus = (Long) this.subeventJson.put("bonus", bonus);
        if (currentBonus == null) {
            currentBonus = 0L;
        }
        this.subeventJson.put("bonus", currentBonus + bonus);
    }

    public void set(long value) {
        Long previousValue = (Long) this.subeventJson.get("set");
        if (previousValue == null || previousValue < value) {
            this.subeventJson.put("set", value);
        }
    }

    public Long get() {
        Long bonus = (Long) this.subeventJson.get("bonus");
        Long set = (Long) this.subeventJson.get("set");
        if (set != null) {
            return set + bonus;
        }
        return (Long) this.subeventJson.get("raw") + bonus;
    }

}
