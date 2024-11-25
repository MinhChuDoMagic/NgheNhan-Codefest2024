package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.socket.data.Position;

public class CalculateUtils {
    private CalculateUtils() {
    }

    public static int manhattanDistance(Position curr, Position des) {
        return Math.abs(curr.row - des.row) + Math.abs(curr.col - des.col);
    }
}
