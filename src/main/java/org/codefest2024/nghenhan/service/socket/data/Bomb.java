package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

public class Bomb extends Position {
    public String playerId;
    public int remainTime;
    public int power;
    public long createAt;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
