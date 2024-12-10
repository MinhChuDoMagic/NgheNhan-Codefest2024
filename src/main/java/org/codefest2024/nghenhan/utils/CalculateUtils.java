package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CalculateUtils {
    private CalculateUtils() {
    }

    private static final List<int[]> directions = Arrays.asList(
            new int[]{0, -1, Integer.parseInt(Dir.LEFT)},
            new int[]{0, 1, Integer.parseInt(Dir.RIGHT)},
            new int[]{-1, 0, Integer.parseInt(Dir.UP)},
            new int[]{1, 0, Integer.parseInt(Dir.DOWN)}
    );

    public static List<int[]> getDirections() {
        List<int[]> randomDirections = new ArrayList<>(directions);
        Collections.shuffle(randomDirections);
        return randomDirections;
    }

    public static int manhattanDistance(Position curr, Position des) {
        return Math.abs(curr.row - des.row) + Math.abs(curr.col - des.col);
    }

    public static double realDistance(Position curr, Position des) {
        return Math.sqrt(1.0 * (curr.row - des.row) * (curr.row - des.row) + (curr.col - des.col) * (curr.col - des.col));
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

    public static boolean enemyNearby(Position curr, Position enemy) {
        return manhattanDistance(curr, enemy) < 16;
    }

    public static boolean enemySupperNearby(Position curr, Position enemy) {
        return manhattanDistance(curr, enemy) < 8;
    }

    public static String processDirWithBrick(String dir) {
        int indexOfB = dir.indexOf('b');
        if (indexOfB == 2 && dir.charAt(0) != dir.charAt(1)) {
            return dir.substring(0, 2);
        } else {
            return dir.length() > 3 ? dir.substring(0, 3) : dir;
        }
    }

    public static boolean isNearBox(int[][] map, Position curr) {
        return directions.stream()
                .anyMatch(dir -> map[curr.row + dir[0]][curr.col + dir[1]] == MapInfo.BOX);
    }

    public static boolean isNearBrick(int[][] map, Position curr) {
        return directions.stream()
                .anyMatch(dir -> map[curr.row + dir[0]][curr.col + dir[1]] == MapInfo.BRICK);
    }
}
