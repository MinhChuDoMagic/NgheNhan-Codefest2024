package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.socket.data.Position;

import java.util.List;

public class CalculateUtils {
    private CalculateUtils() {
    }

    public static int manhattanDistance(Position curr, Position des) {
        return Math.abs(curr.row - des.row) + Math.abs(curr.col - des.col);
    }

    public static boolean isNearest(Position curr, Position des, List<Position> otherPositions) {
        int currDistance = manhattanDistance(curr, des);

        for (Position other : otherPositions) {
            int otherDistance = manhattanDistance(other, des);
            if (otherDistance < currDistance) {
                return false;
            }
        }

        return true;
    }
}
