package org.codefest2024.nghenhan.service.socket.data;

public class Wind extends Weapon {
    public static final int COOL_DOWN = 7;
    public String id;
    public int currentRow;
    public int currentCol;
    public int direction;

    public Wind() {
    }

    public Wind(int row, int col, int direction) {
        this.currentRow = row;
        this.currentCol = col;
        this.direction = direction;
    }
}
