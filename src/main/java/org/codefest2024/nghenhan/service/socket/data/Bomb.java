package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

import java.util.Objects;

public class Bomb extends Position {
    public static final double BOMB_TIME = 3.5;

    public String playerId;
    public int remainTime;
    public int power;
    public long createdAt;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bomb bomb)) return false;
        if (!super.equals(o)) return false;
        return power == bomb.power && createdAt == bomb.createdAt && Objects.equals(playerId, bomb.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, power, createdAt);
    }
}
