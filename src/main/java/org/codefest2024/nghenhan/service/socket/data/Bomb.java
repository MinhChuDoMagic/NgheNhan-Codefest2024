package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

import java.util.Objects;

public class Bomb extends Position {
    public static final double BOMB_TIME = 2.1;
    public static final double STUN_TIME = 3;
    public static final double STUN_COOLDOWN = 5;
    public static final double BOMB_EXPLORE_TIME = 2;

    public String playerId;
    public int remainTime;
    public int power;
    public long createdAt;

    public Bomb() {
    }

    public Bomb(Position position, int power) {
        super(position.row, position.col);
        this.power = power;
    }

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
