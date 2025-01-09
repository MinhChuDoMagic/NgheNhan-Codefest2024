package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.KeepDistanceFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.FinderUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class KeepDistance {
//    public final int TEAMMATE_DANGEROUS_DISTANCE = 10;
    public final int TEAMMATE_DANGEROUS_DISTANCE = 7;
    public final int TEAMMATE_SAFE_DISTANCE = 16;
//    public final int ENEMY_DANGEROUS_DISTANCE = 10;
//    public final int ENEMY_SAFE_DISTANCE = 16;
    public final int ENEMY_DANGEROUS_DISTANCE = 7;
    public final int ENEMY_SAFE_DISTANCE = 12;


    private final KeepDistanceFinder keepDistanceFinder = KeepDistanceFinder.getInstance();
    private final GodFarmBox godFarmBox = new GodFarmBox();
    private final OptimalFarmBox optimalFarmBox = new OptimalFarmBox();

    public List<Order> farm(MapInfo mapInfo, Player player, Player enemy) {

        if (mapInfo.playerBombs.isEmpty()) {
            if (CalculateUtils.isNearBox(mapInfo.map, player.currentPosition)) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(Dir.ACTION, player.isChild));
                return orders;
            } else if (enemy != null) {
                AStarNode boxNodeWithoutBrick = keepDistanceFinder.findWithoutBrick(mapInfo.map, player.currentPosition, enemy.currentPosition, MapInfo.BOX, mapInfo.size);
                if (boxNodeWithoutBrick.parent != null) {
                    String dir = boxNodeWithoutBrick.parent.reconstructPath();
                    if (!dir.isEmpty()) {
                        List<Order> orders = new ArrayList<>();
                        if (dir.contains(Dir.ACTION) && player.currentWeapon != 1) {
                            orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                        }
                        orders.add(new Dir(boxNodeWithoutBrick.parent.reconstructPath(), player.isChild));
                        return orders;
                    }
                }

                AStarNode boxNode = keepDistanceFinder.find(mapInfo.map, player.currentPosition, enemy.currentPosition, MapInfo.BOX, mapInfo.size);
                if (boxNode.parent != null) {
                    String dir = boxNode.parent.reconstructPath();
                    if (!dir.isEmpty()) {
                        List<Order> orders = new ArrayList<>();
                        if (dir.contains(Dir.ACTION) && player.currentWeapon != 1) {
                            orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                        }
                        orders.add(new Dir(boxNode.parent.reconstructPath(), player.isChild));
                        return orders;
                    }
                }
            } else {
                godFarmBox.find(mapInfo, player);
            }
        }

        return List.of();
    }

    public List<Order> run(MapInfo mapInfo, Player player, Player enemy) {
        if (enemy != null && CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) < ENEMY_SAFE_DISTANCE) {
            Bomb myBomb = null;
            for (Bomb bomb : mapInfo.bombs) {
                if (bomb.playerId.startsWith(Constants.KEY_TEAM)
                        && (!player.isChild || bomb.playerId.endsWith(Constants.KEY_CHILD))) {
                    myBomb = bomb;
                }
            }

            if (myBomb == null
                    && CalculateUtils.enemySupperNearby(player.currentPosition, enemy.currentPosition)
                    && !CalculateUtils.isNearBrick(mapInfo.map, player.currentPosition)) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(Dir.ACTION, player.isChild));
                return orders;
            }

            String dirWithoutBrick = keepDistanceFinder.keepDistanceWithoutBrick(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size).reconstructPath();
            if (!dirWithoutBrick.isEmpty()) {
                return List.of(new Dir(dirWithoutBrick, player.isChild));
            }

            String dir = keepDistanceFinder.keepDistance(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size, ENEMY_SAFE_DISTANCE).reconstructPath();
            if (!dir.isEmpty()) {
                String processDir = CalculateUtils.processDirWithBrick(dir);
                List<Order> orders = new ArrayList<>();
                if (processDir.contains("b") && player.currentWeapon != 1) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(processDir, player.isChild));
                return orders;
            }
        }
        return List.of();
    }

    public List<Order> keepTeammateDistance(MapInfo mapInfo, Player player, Player teammate) {
        if (player.isChild && teammate != null && CalculateUtils.manhattanDistance(player.currentPosition, teammate.currentPosition) < TEAMMATE_DANGEROUS_DISTANCE) {
            String dir = keepDistanceFinder.keepDistance(mapInfo.map, player.currentPosition, teammate.currentPosition, mapInfo.size, TEAMMATE_SAFE_DISTANCE).reconstructPath();
            if (!dir.isEmpty()) {
                return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
            }
        }

        return List.of();
    }

    public List<Order> keepEnemiesDistance(MapInfo mapInfo, Player player, List<Player> enemies) {
        for (Player enemy : enemies) {
            if (enemy != null && CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) < ENEMY_DANGEROUS_DISTANCE) {
                if (!SkillUtils.isBombCooldown(player.delay, player.isChild)
                        && optimalFarmBox.isSafeBombPlace(mapInfo, player)
                        && !CalculateUtils.isNearBrick(mapInfo.map, player.currentPosition)) {
                    List<Order> orders = new ArrayList<>();
                    if (player.currentWeapon != 2) {
                        orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                    }
                    orders.add(new Dir(Dir.ACTION, player.isChild));
                    return orders;
                }

                String dir = keepDistanceFinder.keepDistance(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size, ENEMY_SAFE_DISTANCE).reconstructPath();
                if (!dir.isEmpty()) {
                    return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
                }
            }
        }
        return List.of();
    }
}
