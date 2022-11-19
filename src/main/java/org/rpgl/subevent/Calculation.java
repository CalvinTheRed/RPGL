package org.rpgl.subevent;

import java.util.Objects;

public abstract class Calculation extends Subevent {

    public Calculation(String subeventId) {
        super(subeventId);
    }

    public void addBonus(long bonus) {
        Long currentBonus = (Long) this.subeventJson.put("bonus", bonus);
        if (currentBonus == null) {
            currentBonus = 0L;
        }
        this.subeventJson.put("bonus", currentBonus + bonus);
    }

    public long getBonus() {
        return Objects.requireNonNullElse((Long) this.subeventJson.get("bonus"), 0L);
    }

    public void set(long value) {
        Long previousValue = (Long) this.subeventJson.get("set");
        if (previousValue == null || previousValue < value) {
            this.subeventJson.put("set", value);
        }
    }

    public Long get() {
        Long set = (Long) this.subeventJson.get("set");
        Long bonus = this.getBonus();
        if (set != null) {
            return set + bonus;
        }
        return (Long) this.subeventJson.get("base") + bonus;
    }

}
