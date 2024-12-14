package org.codefest2024.nghenhan.service.socket.data;

public final class Wait extends Order{
    public static final int REDIRECT = 400;
    public int duration;

    public Wait(int duration, boolean useChild) {
        this.duration = duration;
        this.characterType = useChild ? USE_CHILD : null;
    }
}
