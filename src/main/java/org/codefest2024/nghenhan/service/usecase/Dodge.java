package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BFSFinder;
import org.codefest2024.nghenhan.service.finder.KeepDistanceFinder;
import org.codefest2024.nghenhan.service.finder.PowerFarmFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.FinderUtils;
import org.codefest2024.nghenhan.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Dodge {
    private final BFSFinder bfsFinder = BFSFinder.getInstance();
    private final KeepDistanceFinder keepDistanceFinder = KeepDistanceFinder.getInstance();
    private final PowerFarmFinder powerFarmFinder = PowerFarmFinder.getInstance();
    private final StunAndBomb stunAndBomb = new StunAndBomb();
    private final FarmBrick farmBrick = new FarmBrick();

    public List<Order> find(MapInfo mapInfo, Player myPlayer) {
        List<Order> orders = new ArrayList<>();

        List<Bomb> dangerousBombs = mapInfo
                .bombs
                .stream()
                .filter(bomb -> isDangerousBomb(bomb, myPlayer.currentPosition))
                .toList();
        List<Hammer> dangerousHammers = mapInfo
                .weaponHammers
                .stream()
                .filter(hammer -> isDangerousHammer(hammer, myPlayer.currentPosition))
                .toList();
        List<Wind> dangerousWinds = mapInfo
                .weaponWinds
                .stream()
                .filter(wind -> isDangerousWind(mapInfo.map, wind, myPlayer.currentPosition))
                .toList();

        if (!dangerousBombs.isEmpty() || !dangerousHammers.isEmpty() || !dangerousWinds.isEmpty()) {
            String dir = bfsFinder
                    .findSafe(mapInfo.map, myPlayer.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, dangerousWinds)
                    .reconstructPath();
            if (dir.isEmpty()) {
//                String oneSafeStep = bfsFinder.oneSafeStep(mapInfo.map, myPlayer.currentPosition, dangerousBombs, dangerousHammers, dangerousWinds);
//                orders.add(new Dir(oneSafeStep, myPlayer.isChild));
            } else {
                orders.add(new Dir(dir, myPlayer.isChild));
            }
        }

        return orders;
    }

    public List<Order> findForPowerFarm(MapInfo mapInfo, Player myPlayer) {
        List<Bomb> dangerousBombs = mapInfo
                .bombs
                .stream()
                .filter(bomb -> isDangerousBomb(bomb, myPlayer.currentPosition))
                .toList();
        List<Hammer> dangerousHammers = mapInfo
                .weaponHammers
                .stream()
                .filter(hammer -> isDangerousHammer(hammer, myPlayer.currentPosition))
                .toList();
        List<Wind> dangerousWinds = mapInfo
                .weaponWinds
                .stream()
                .filter(wind -> isDangerousWind(mapInfo.map, wind, myPlayer.currentPosition))
                .toList();

        if (!dangerousBombs.isEmpty() || !dangerousHammers.isEmpty() || !dangerousWinds.isEmpty()) {
            String dir = powerFarmFinder
                    .findSafe(mapInfo.map, myPlayer.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, dangerousWinds)
                    .reconstructPath();
            if (dir.isEmpty()) {
                String oneSafeStep = bfsFinder.oneSafeStep(mapInfo.map, myPlayer.currentPosition, dangerousBombs, dangerousHammers, dangerousWinds);
                return List.of(new Dir(oneSafeStep, myPlayer.isChild));
            } else {
                return FinderUtils.processDirWithBrick(dir, myPlayer.isChild, myPlayer.currentWeapon);
            }
        }

        return List.of();
    }

    public List<Order> findWithoutWind(MapInfo mapInfo, Player myPlayer) {
        List<Order> orders = new ArrayList<>();

        List<Bomb> dangerousBombs = mapInfo
                .bombs
                .stream()
                .filter(bomb -> isDangerousBomb(bomb, myPlayer.currentPosition))
                .toList();
        List<Hammer> dangerousHammers = mapInfo
                .weaponHammers
                .stream()
                .filter(hammer -> isDangerousHammer(hammer, myPlayer.currentPosition))
                .toList();

        if (!dangerousBombs.isEmpty() || !dangerousHammers.isEmpty()) {
            String dir = bfsFinder
                    .findSafe(mapInfo.map, myPlayer.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, List.of())
                    .reconstructPath();
            if (dir.isEmpty()) {
                String oneSafeStep = bfsFinder.oneSafeStep(mapInfo.map, myPlayer.currentPosition, dangerousBombs, dangerousHammers, List.of());
                orders.add(new Dir(oneSafeStep, myPlayer.isChild));
            } else {
                orders.add(new Dir(dir, myPlayer.isChild));
            }
        }

        return orders;
    }

    public List<Order> findAndKeepDistance(MapInfo mapInfo, Player player, Player teammate, Player enemy, Player enemyChild) {
        Player nearestEnemy = CalculateUtils.nearestEnemy(player, enemy, enemyChild);
        List<Bomb> dangerousBombs = mapInfo
                .bombs
                .stream()
                .filter(bomb -> isDangerousBomb(bomb, player.currentPosition))
                .toList();
        List<Hammer> dangerousHammers = mapInfo
                .weaponHammers
                .stream()
                .filter(hammer -> isDangerousHammer(hammer, player.currentPosition))
                .toList();
        List<Wind> dangerousWinds = mapInfo
                .weaponWinds
                .stream()
                .filter(wind -> isDangerousWind(mapInfo.map, wind, player.currentPosition))
                .toList();

        if (!dangerousBombs.isEmpty() || !dangerousHammers.isEmpty() || !dangerousWinds.isEmpty()) {
            String dir = nearestEnemy != null && CalculateUtils.manhattanDistance(player.currentPosition, nearestEnemy.currentPosition) <= 12
                    ? keepDistanceFinder.findSafe(mapInfo.map, player.currentPosition, nearestEnemy.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, dangerousWinds).reconstructPath()
                    : teammate != null
                    ? keepDistanceFinder.findSafe(mapInfo.map, player.currentPosition, teammate.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, dangerousWinds).reconstructPath()
                    : bfsFinder.findSafe(mapInfo.map, player.currentPosition, mapInfo.size, dangerousBombs, dangerousHammers, dangerousWinds).reconstructPath();
            if (dir.isEmpty()) {
                List<Order> stunOrders = stunAndBomb.findAndStun(mapInfo, player, Utils.filterNonNull(enemy, enemyChild));
                if (!stunOrders.isEmpty()) {
                    return stunOrders;
                }

//                List<Order> farmBrickOrders = farmBrick.farmBrickWhenDodge(mapInfo, player, Utils.filterNonNull(enemy, enemyChild));
//                if (!farmBrickOrders.isEmpty()) {
//                    return farmBrickOrders;
//                }
            } else {
                return List.of(new Dir(dir, player.isChild));
            }
        }

        return List.of();
    }

    private boolean isDangerousBomb(Bomb bomb, Position curr) {
        return CalculateUtils.manhattanDistance(curr, bomb) < Math.max(5, bomb.power + 1);
    }

    private boolean isDangerousHammer(Hammer hammer, Position curr) {
        return Math.abs(curr.col - hammer.destination.col) <= 2 * hammer.power
                && Math.abs(curr.row - hammer.destination.row) <= 2 * hammer.power;
    }

    private boolean isDangerousWind(int[][] map, Wind wind, Position curr) {
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
