package org.codefest2024.nghenhan.service.socket.data;

public sealed class Order permits Dir, Action, Wait {
    public static final String USE_CHILD = "child";
    public String characterType;
}
