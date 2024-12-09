package org.codefest2024.nghenhan.service.socket.data;

public class Hammer extends Weapon {
    public static final int RANGE = 7;
    public static final int COOL_DOWN = 10;
    public static final int POWER = 2;
    public int power;
    public Position destination;
    public long createdAt;

    public Hammer() {
    }

    public Hammer(Position destination) {
        this.power = POWER;
        this.destination = destination;
    }
}
