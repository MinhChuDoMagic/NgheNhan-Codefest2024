package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.finder.AStarFinder;
import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.service.usecase.Dodge;
import org.codefest2024.nghenhan.service.usecase.FarmBrick;
import org.codefest2024.nghenhan.service.usecase.FindBadge;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.util.ArrayList;
import java.util.List;

public class SeaAttackStrategy implements Strategy {
    private final FindBadge findBadge = new FindBadge();
    private final AStarFinder aStarFinder = AStarFinder.getInstance();
    private final Dodge dodge = new Dodge();
    private final FarmBrick farmBrick = new FarmBrick();

    @Override
    public List<Order> find(GameInfo gameInfo) {
        MapInfo mapInfo = gameInfo.map_info;
        Player player = mapInfo.player;
        Player enemy = mapInfo.enemy;
        Player child = mapInfo.child;
        Player enemyChild = mapInfo.enemyChild;

        if (player != null) {
            if (!player.hasTransform) {
                return findBadge.find(gameInfo, player);
            } else if (enemy != null && !enemy.hasTransform) {
                return farmBrick.farmBrick(mapInfo, player, enemy);
            } else {
                List<Order> orders = playerStrategy(mapInfo, player, enemy);
                if (child != null) {
                    orders = new ArrayList<>(orders);
                    orders.addAll(playerStrategy(mapInfo, child, enemy));
                }
                return orders;
            }
        }

        return List.of();
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player enemy) {
        List<Order> dodgeBombsOrders = dodge.findWithoutWind(mapInfo, player);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        if (enemy.isStun) {
            InGameInfo.isEnemyStun = true;
        }

        if (!enemy.isStun && !SkillUtils.isSkillCooldown(player.isChild) && InGameInfo.isEnemyStun) {
            InGameInfo.isEnemyStun = false;
            return List.of(new Action(Action.USE_WEAPON, player.isChild));
        }
        String dir = aStarFinder.find(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size);
        return List.of(new Dir(CalculateUtils.processDirWithBrick(dir)));
    }
}
