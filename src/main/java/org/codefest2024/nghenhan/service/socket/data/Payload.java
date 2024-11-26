package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

public class Payload {
    public Position destination;

    public Payload() {
    }

    public Payload(Position destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
