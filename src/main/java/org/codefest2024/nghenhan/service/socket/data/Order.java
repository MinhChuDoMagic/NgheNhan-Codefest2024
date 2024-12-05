package org.codefest2024.nghenhan.service.socket.data;

public sealed  class Order permits Dir, Action {
    public String characterType;
}
