package org.codefest2024.nghenhan.service.socket.data;

public class WeaponWind {
    public static final int COOL_DOWN = 3;
    public String id;
    public String playerId;
    public int currentRow;
    public int currentCol;
    public int direction;

    public WeaponWind() {
    }

    public WeaponWind(int row, int col, int direction) {
        this.currentRow = row;
        this.currentCol = col;
        this.direction = direction;
    }
}
