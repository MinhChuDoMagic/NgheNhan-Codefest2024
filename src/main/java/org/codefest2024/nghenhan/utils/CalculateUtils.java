package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Player;
import org.codefest2024.nghenhan.service.socket.data.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CalculateUtils {
    private CalculateUtils() {
    }

    private static final List<int[]> directions = Arrays.asList(
            new int[]{0, -1, Integer.parseInt(Dir.LEFT)},
            new int[]{0, 1, Integer.parseInt(Dir.RIGHT)},
            new int[]{-1, 0, Integer.parseInt(Dir.UP)},
            new int[]{1, 0, Integer.parseInt(Dir.DOWN)}
    );

    static {
        Collections.shuffle(directions);
    }

    private static final List<int[]> crossDirections = Arrays.asList(
            new int[]{-1, -1},
            new int[]{-1, 1},
            new int[]{1, -1},
            new int[]{1, 1}
    );

    public static List<int[]> getDirections(Position curr, Position enemy) {
        List<int[]> sortedDirections = new ArrayList<>(directions);

        // Sort directions based on Manhattan distance and higher axis priority
        Collections.sort(sortedDirections, (dir1, dir2) -> {
            // Calculate new positions
            int newRow1 = curr.row + dir1[0];
            int newCol1 = curr.col + dir1[1];
            int newRow2 = curr.row + dir2[0];
            int newCol2 = curr.col + dir2[1];

            // Calculate Manhattan distances
            int manhattan1 = Math.abs(enemy.row - newRow1) + Math.abs(enemy.col - newCol1);
            int manhattan2 = Math.abs(enemy.row - newRow2) + Math.abs(enemy.col - newCol2);

            // Compare Manhattan distances
            if (manhattan1 != manhattan2) {
                return Integer.compare(manhattan2, manhattan1); // Prioritize higher Manhattan distance
            }

            // If Manhattan distances are equal, compare higher axis priority
            int verticalDist1 = Math.abs(enemy.row - newRow1);
            int horizontalDist1 = Math.abs(enemy.col - newCol1);
            int verticalDist2 = Math.abs(enemy.row - newRow2);
            int horizontalDist2 = Math.abs(enemy.col - newCol2);

            // Prioritize higher vertical or horizontal distance
            int higherAxis1 = Math.max(verticalDist1, horizontalDist1);
            int higherAxis2 = Math.max(verticalDist2, horizontalDist2);
            return Integer.compare(higherAxis2, higherAxis1); // Reverse comparison for higher priority
        });

        return sortedDirections;
    }

    public static List<int[]> getDirections() {
//        List<int[]> randomDirections = new ArrayList<>(directions);
//        Collections.shuffle(randomDirections);
        return directions;
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
        return Stream.concat(directions.stream(), crossDirections.stream())
                .anyMatch(dir -> map[curr.row + dir[0]][curr.col + dir[1]] == MapInfo.BRICK);
    }

    public static boolean isNearBombExplored(int[][] map, Position curr) {
        return Stream.concat(directions.stream(), crossDirections.stream())
                .anyMatch(dir -> map[curr.row + dir[0]][curr.col + dir[1]] == MapInfo.BRICK);
    }

    public static Player nearestEnemy(Player player, Player enemy, Player enemyChild) {
        if (enemy == null) {
            return enemyChild;
        }

        if (enemyChild == null) {
            return enemy;
        }

        int enemyDistance = manhattanDistance(player.currentPosition, enemy.currentPosition);
        int enemyChildDistance = manhattanDistance(player.currentPosition, enemyChild.currentPosition);

        if (enemyChildDistance < 12 && enemyDistance < 12) {
            return enemy;
        }

        return enemyChildDistance < enemyDistance ? enemyChild : enemy;
    }
}
