package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.time.Instant;
import java.util.*;

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

    public static boolean isHitBomb(Position curr, Bomb bomb) {
        return (curr.row == bomb.row && Math.abs(curr.col - bomb.col) <= bomb.power)
                || (curr.col == bomb.col && Math.abs(curr.row - bomb.row) <= bomb.power);
    }

    public static boolean isHitHammer(Position curr, WeaponHammer hammer) {
        return Math.abs(curr.col - hammer.destination.col) <= hammer.power
                && Math.abs(curr.row - hammer.destination.row) <= hammer.power;
    }

    public static boolean isHitWind(int[][] map, Position curr, WeaponWind wind) {
        int row = wind.currentRow;
        int col = wind.currentCol;

        switch (wind.direction) {
            case 1: // Left
                while (col >= 0) {
                    if (row == curr.row && col == curr.col) return true; // wind can hit the player
                    if (map[row][col] != 0) break; // wind is blocked
                    col--;
                }
                break;

            case 2: // Right
                while (col < map[0].length) {
                    if (row == curr.row && col == curr.col) return true;
                    if (map[row][col] != 0) break;
                    col++;
                }
                break;

            case 3: // Up
                while (row >= 0) {
                    if (row == curr.row && col == curr.col) return true;
                    if (map[row][col] != 0) break;
                    row--;
                }
                break;

            case 4: // Down
                while (row < map.length) {
                    if (row == curr.row && col == curr.col) return true;
                    if (map[row][col] != 0) break;
                    row++;
                }
                break;

            default:
                return false;
        }

        return false;
    }

    public static boolean inHammerRange(Position curr, Position enemy) {
        return Math.abs(curr.col - enemy.col) < 3 && Math.abs(curr.row - enemy.row) < 5 ||
                Math.abs(curr.row - enemy.row) < 3 && Math.abs(curr.col - enemy.col) < 5;
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
//        String input = dir;
//        if (input == null || input.isEmpty()) {
//            return ""; // Handle null or empty strings
//        }
//
//        if (input.charAt(0) == 'b') {
//            return "b";
//        }
//
//        int indexOfB = input.indexOf('b');
//        if (indexOfB != -1) {
//            return input.substring(0, indexOfB);
//        }
//
//        return input;
    }

    public static boolean isNearBox(int[][] map, Position curr){
        return directions.stream()
                .anyMatch(dir -> map[curr.row + dir[0]][curr.col + dir[1]] == MapInfo.BOX);
    }

    public static boolean isNearBrick(int[][] map, Position curr){
        return directions.stream()
                .anyMatch(dir -> map[curr.row + dir[0]][curr.col + dir[1]] == MapInfo.BRICK);
    }

    public static boolean isCooldown(boolean isChild) {
        long cooldown = switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> WeaponHammer.COOL_DOWN;
            case Player.SEA -> WeaponWind.COOL_DOWN;
            default -> 0L;
        } * 1000;

        return (isChild && Instant.now().toEpochMilli() - InGameInfo.myChildLastSkillTime <= cooldown)
                || (!isChild && Instant.now().toEpochMilli() - InGameInfo.myPlayerLastSkillTime <= cooldown);
    }
}
