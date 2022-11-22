package org.rpgl.subevent;

import java.util.Objects;

public abstract class GetProficiency extends Subevent {

    public GetProficiency(String subeventId) {
        super(subeventId);
    }

    public void grantProficiency() {
        this.subeventJson.put("is_proficient", true);
    }

    public Boolean getIsProficient() {
        return Objects.requireNonNullElse((Boolean) this.subeventJson.get("is_proficient"), false);
    }
}
