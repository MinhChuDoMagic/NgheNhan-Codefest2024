package org.codefest2024.nghenhan.service.socket.data;

public class WeaponHammer {
    public static final int RANGE = 7;
    public static final int COOL_DOWN = 10;
    public static final int POWER = 2;
    public String playerId;
    public int power;
    public Position destination;
    public long createdAt;

    public WeaponHammer() {
    }

    public WeaponHammer(Position destination) {
        this.power = POWER;
        this.destination = destination;
    }
}
