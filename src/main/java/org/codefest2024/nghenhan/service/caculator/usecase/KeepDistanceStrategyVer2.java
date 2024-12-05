package org.codefest2024.nghenhan.service.caculator.usecase;

import org.codefest2024.nghenhan.service.caculator.data.AStarNode;
import org.codefest2024.nghenhan.service.caculator.finder.KeepDistanceFinderVer2;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class KeepDistanceStrategyVer2 {
    private final KeepDistanceFinderVer2 keepDistanceFinderVer2 = KeepDistanceFinderVer2.getInstance();

    public List<Order> farm(MapInfo mapInfo, Player player, Player enemy) {
        Bomb myBomb = null;
        for (Bomb bomb : mapInfo.bombs) {
            if (bomb.playerId.startsWith(Constants.KEY_TEAM)
                    && (!player.isChild || bomb.playerId.endsWith(Constants.KEY_CHILD))) {
                myBomb = bomb;
            }
        }

        if (myBomb == null) {
            if (CalculateUtils.isNearBox(mapInfo.map, player.currentPosition)) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(Dir.ACTION, player.isChild));
                return orders;
            } else {
                AStarNode boxNode = keepDistanceFinderVer2.find(mapInfo.map, player.currentPosition, enemy.currentPosition, MapInfo.BOX, mapInfo.size);
                if (boxNode.parent != null) {
                    String dir = boxNode.parent.reconstructPath();
                    if (!dir.isEmpty()) {
                        List<Order> orders = new ArrayList<>();
                        if (dir.contains(Dir.ACTION) && player.currentWeapon != 1) {
                            orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                        }
                        orders.add(new Dir(boxNode.parent.reconstructPath(), player.isChild));
                    }
                }
            }
        }

        return List.of();
    }

    public List<Order> run(MapInfo mapInfo, Player player, Player enemy) {
        if (enemy != null && CalculateUtils.enemyNearby(player.currentPosition, enemy.currentPosition)) {
            Bomb myBomb = null;
            for (Bomb bomb : mapInfo.bombs) {
                if (bomb.playerId.startsWith(Constants.KEY_TEAM)
                        && (!player.isChild || bomb.playerId.endsWith(Constants.KEY_CHILD))) {
                    myBomb = bomb;
                }
            }

            if (myBomb == null && CalculateUtils.enemySupperNearby(player.currentPosition, enemy.currentPosition)) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(Dir.ACTION, player.isChild));
                return orders;
            }

            String dir = keepDistanceFinderVer2.keepDistance(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size).reconstructPath();
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
}
