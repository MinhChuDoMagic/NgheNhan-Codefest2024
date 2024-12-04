package org.codefest2024.nghenhan.service.caculator.usecase;

import org.codefest2024.nghenhan.service.caculator.finder.KeepDistanceFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class KeepDistanceStrategy {
    private final KeepDistanceFinder keepDistanceFinder = KeepDistanceFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player player, Player enemy) {
        if (enemy != null && CalculateUtils.enemyNearby(player.currentPosition, enemy.currentPosition)) {
            Bomb myBomb = null;
            for (Bomb bomb : mapInfo.bombs) {
                if (bomb.playerId.startsWith(Constants.KEY_TEAM)
                        && (!player.isChild || bomb.playerId.endsWith(Constants.KEY_CHILD))) {
                    myBomb = bomb;
                }
            }

            if (myBomb == null) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(Dir.ACTION, player.isChild));
                return orders;
            }

            String dir = keepDistanceFinder.find(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size);
            if (!dir.isEmpty()) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 1) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(CalculateUtils.processDirWithBrick(dir), player.isChild));
                return orders;
            }
        }

        return List.of();
    }


}
