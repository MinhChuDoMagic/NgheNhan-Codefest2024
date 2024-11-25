package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.CollectSpoilsStrategy;
import org.codefest2024.nghenhan.service.caculator.DodgeBombsStrategy;
import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.Utils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class FarmStrategy implements Strategy {
    private final NormalFarmStrategy normalFarmStrategy = new NormalFarmStrategy();
    private final GodFarmStrategy godFarmStrategy = new GodFarmStrategy();
    private final DodgeBombsStrategy dodgeBombsStrategy = new DodgeBombsStrategy();
    private final CollectSpoilsStrategy collectSpoilsStrategy = new CollectSpoilsStrategy();

    @Override
    public List<Order> find(GameInfo gameInfo) {
        MapInfo mapInfo = gameInfo.map_info;
        Player myPlayer = null;
        Player enemyPlayer = null;
        Player myChild = null;
        Player enemyChild = null;
        for (Player player : mapInfo.players) {
            if (player.id.startsWith(Constants.KEY_TEAM)) {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    myChild = player;
                } else {
                    myPlayer = player;
                }
            } else {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    enemyChild = player;
                } else {
                    enemyPlayer = player;
                }
            }
        }

        if (myPlayer != null) {
            if (!myPlayer.hasTransform) {
                return normalFarmStrategy.find(gameInfo, myPlayer);
            } else {
                List<Order> orders = playerStrategy(mapInfo, myPlayer, myChild, enemyPlayer, enemyChild);
                if (myChild != null) {
                    orders = new ArrayList<>(orders);
                    orders.addAll(childStrategy(mapInfo, myPlayer, myChild, enemyPlayer, enemyChild));
                }
                return orders;
            }
        }

        return List.of();
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player myPlayer, Player myChild, Player enemyPlayer, Player enemyChild) {
        List<Order> dodgeBombsOrders = dodgeBombsStrategy.find(mapInfo, myPlayer);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        if (myPlayer.eternalBadge > 0 && myChild == null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> collectSpoilOrders = collectSpoilsStrategy.find(mapInfo, myPlayer, Utils.filterNonNull(myChild, enemyPlayer, enemyChild));
        if (!collectSpoilOrders.isEmpty()) {
            return collectSpoilOrders;
        }

        return godFarmStrategy.find(mapInfo, myPlayer);
    }

    private List<Order> childStrategy(MapInfo mapInfo, Player myPlayer, Player myChild, Player enemyPlayer, Player enemyChild) {
        List<Order> dodgeBombsOrders = dodgeBombsStrategy.find(mapInfo, myChild);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        List<Order> collectSpoilOrders = collectSpoilsStrategy.find(mapInfo, myChild, Utils.filterNonNull(myPlayer, enemyPlayer, enemyChild));
        if (!collectSpoilOrders.isEmpty()) {
            return collectSpoilOrders;
        }

        return godFarmStrategy.find(mapInfo, myChild);
    }
}
