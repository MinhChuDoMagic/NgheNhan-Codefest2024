package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.GameInfo;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Player;
import org.codefest2024.nghenhan.service.usecase.Dodge;
import org.codefest2024.nghenhan.service.usecase.FindBadge;
import org.codefest2024.nghenhan.service.usecase.OptimalFarmBox;
import org.codefest2024.nghenhan.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PowerFarmStrategy implements Strategy {
    private final FindBadge findBadge = new FindBadge();
    private final Dodge dodge = new Dodge();
    private final OptimalFarmBox optimalFarmBox = new OptimalFarmBox();

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
            } else {
                List<Order> orders = playerStrategy(mapInfo, player, child, enemy, enemyChild);
                if (child != null) {
                    orders = new ArrayList<>(orders);
                    orders.addAll(playerStrategy(mapInfo, child, player, enemy, enemyChild));
                }
                return orders;
            }
        }

        return List.of();
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player teammate, Player enemy, Player enemyChild) {
//        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemy != null) {
//            return List.of(new Action(Action.MARRY_WIFE));
//        }

        List<Order> dodgeBombsOrders = dodge.findForPowerFarm(mapInfo, player);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        List<Order> godFarmOrders = optimalFarmBox.findVer2(mapInfo, player, Utils.filterNonNull(teammate, enemy, enemyChild));
        if (!godFarmOrders.isEmpty()) {
            return godFarmOrders;
        }

        return List.of();
    }
}
