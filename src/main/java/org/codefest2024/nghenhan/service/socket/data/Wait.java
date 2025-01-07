package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

public final class Wait extends Order{
    public static final int REDIRECT = 600;
    public int duration;

    public Wait(int duration, boolean useChild) {
        this.duration = duration;
        this.characterType = useChild ? USE_CHILD : null;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
