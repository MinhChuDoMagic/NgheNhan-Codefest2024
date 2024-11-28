package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.caculator.finder.BFSFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.ArrayList;
import java.util.List;

public class DodgeStrategy {
    private final BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer) {
        List<Order> orders = new ArrayList<>();

        List<Bomb> dangerousBombs = mapInfo
                .bombs
                .stream()
                .filter(bomb -> isDangerousBomb(bomb, myPlayer.currentPosition))
                .toList();
        List<WeaponHammer> dangerousHammers = mapInfo
                .weaponHammers
                .stream()
                .filter(hammer -> isDangerousHammer(hammer, myPlayer.currentPosition))
                .toList();
        List<WeaponWind> dangerousWinds = mapInfo
                .weaponWinds
                .stream()
                .filter(wind -> isDangerousWind(mapInfo.map, wind, myPlayer.currentPosition))
                .toList();

        if (!dangerousBombs.isEmpty() || !dangerousHammers.isEmpty() || !dangerousWinds.isEmpty()) {
            String dir = bfsFinder
                    .findSafe(mapInfo.map, myPlayer.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, dangerousWinds)
                    .reconstructPath();
            if (dir.isEmpty()) {
                String oneSafeStep = bfsFinder.oneSafeStep(mapInfo.map, myPlayer.currentPosition, dangerousBombs, dangerousHammers, dangerousWinds);
                orders.add(new Dir(oneSafeStep, myPlayer.isChild));
            } else {
                orders.add(new Dir(dir, myPlayer.isChild));
            }
        }

        return orders;
    }

    private boolean isDangerousBomb(Bomb bomb, Position curr) {
        return CalculateUtils.manhattanDistance(curr, bomb) < Math.max(5, bomb.power + 1);
    }

    private boolean isDangerousHammer(WeaponHammer hammer, Position curr) {
        return Math.abs(curr.col - hammer.destination.col) <= 2 * hammer.power
                && Math.abs(curr.row - hammer.destination.row) <= 2 * hammer.power;
    }

    private boolean isDangerousWind(int[][] map, WeaponWind wind, Position curr) {
        int row = wind.currentRow;
        int col = wind.currentCol;

        switch (wind.direction) {
            case 1: // Left
                while (col >= 0) {
                    if (Math.abs(row - curr.row) <= 2 && Math.abs(col - curr.col) <= 2) {
                        return true; // Bullet can reach within a 2-unit radius
                    }
                    if (map[row][col] != 0) {
                        break; // Bullet is blocked
                    }
                    col--;
                }
                break;

            case 2: // Right
                while (col < map[0].length) {
                    if (Math.abs(row - curr.row) <= 2 && Math.abs(col - curr.col) <= 2) {
                        return true; // Bullet can reach within a 2-unit radius
                    }
                    if (map[row][col] != 0) {
                        break; // Bullet is blocked
                    }
                    col++;
                }
                break;

            case 3: // Up
                while (row >= 0) {
                    if (Math.abs(row - curr.row) <= 2 && Math.abs(col - curr.col) <= 2) {
                        return true; // Bullet can reach within a 2-unit radius
                    }
                    if (map[row][col] != 0) {
                        break; // Bullet is blocked
                    }
                    row--;
                }
                break;

            case 4: // Down
                while (row < map.length) {
                    if (Math.abs(row - curr.row) <= 2 && Math.abs(col - curr.col) <= 2) {
                        return true; // Bullet can reach within a 2-unit radius
                    }
                    if (map[row][col] != 0) {
                        break; // Bullet is blocked
                    }
                    row++;
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid direction: " + wind.direction);
        }

        return false; // Bullet cannot reach within a 2-unit radius
    }
}
