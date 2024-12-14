package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

public final class Action extends Order {
    public static final String SWITCH_WEAPON = "switch weapon";
    public static final String USE_WEAPON = "use weapon";
    public static final String MARRY_WIFE = "marry wife";

    public String action;
    public Payload payload;

    public Action(String action, Payload payload, boolean useChild) {
        this.action = action;
        this.payload = payload;
        this.characterType = useChild ? USE_CHILD : null;
    }

    public Action(String action, Payload payload) {
        this.action = action;
        this.payload = payload;
    }

    public Action(String action, boolean useChild) {
        this.action = action;
        this.characterType = useChild ? USE_CHILD : null;
    }

    public Action(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
