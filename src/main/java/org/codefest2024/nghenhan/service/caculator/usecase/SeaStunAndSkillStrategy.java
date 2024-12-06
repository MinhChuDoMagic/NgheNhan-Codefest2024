package org.codefest2024.nghenhan.service.caculator.usecase;

import org.codefest2024.nghenhan.service.caculator.finder.ForwardFinder;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class SeaStunAndSkillStrategy {
    private final ForwardFinder forwardFinder = ForwardFinder.getInstance();
    private boolean firstTry = true;

    public List<Order> find(MapInfo mapInfo, Player player, Player enemy) {
        if (!enemy.isStun && !CalculateUtils.isCooldown(player.isChild) && InGameInfo.isEnemyStun) {
            InGameInfo.isEnemyStun = false;
            return List.of(new Action(Action.USE_WEAPON, player.isChild));
        }

        if(enemy != null && !enemy.hasTransform){
            firstTry = true;
        }

        if (enemy != null
                && enemy.hasTransform
                && firstTry
                && CalculateUtils.enemySupperNearby(player.currentPosition, enemy.currentPosition)
                && CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) > 2) {
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
                firstTry = false;
                return orders;
            }
        }

        String dir = forwardFinder.find(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size).reconstructPath();
        List<Order> orders = new ArrayList<>();
        if (player.currentWeapon != 1) {
            orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
        }
        orders.add(new Dir(dir));
        return orders;
    }
}
