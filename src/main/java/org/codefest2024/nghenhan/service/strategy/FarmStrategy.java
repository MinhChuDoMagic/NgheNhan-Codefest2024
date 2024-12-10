package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.service.usecase.*;
import org.codefest2024.nghenhan.utils.Utils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class FarmStrategy implements Strategy {
    private final FindBadge findBadge = new FindBadge();
    private final Dodge dodge = new Dodge();
    private final CollectSpoils collectSpoils = new CollectSpoils();
    private final UseSkill useSkill = new UseSkill();
    private final FindAndFire findAndFire = new FindAndFire();
    private final RandomRun randomRun = new RandomRun();
    private final OptimalFarmBox optimalFarmBox = new OptimalFarmBox();

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
                return findBadge.find(gameInfo, myPlayer);
            } else {
                List<Order> orders = playerStrategy(mapInfo, myPlayer, myChild, enemyPlayer, enemyChild);
                if (myChild != null) {
                    orders = new ArrayList<>(orders);
                    orders.addAll(playerStrategy(mapInfo, myChild, myPlayer, enemyPlayer, enemyChild));
                }
                return orders;
            }
        }

        return List.of();
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player teammate, Player enemyPlayer, Player enemyChild) {
        List<Order> dodgeBombsOrders = dodge.find(mapInfo, player);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemyPlayer != null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> useSkillOrders = useSkill.find(mapInfo, player, teammate, enemyPlayer, enemyChild);
        if (!useSkillOrders.isEmpty()) {
            return useSkillOrders;
        }

        List<Order> collectSpoilOrders = collectSpoils.find(mapInfo, player, Utils.filterNonNull(teammate, enemyPlayer, enemyChild));
        if (!collectSpoilOrders.isEmpty()) {
            return collectSpoilOrders;
        }

        List<Order> godFarmOrders = optimalFarmBox.find(mapInfo, player);
        if (!godFarmOrders.isEmpty()) {
            return godFarmOrders;
        }

        List<Order> findAndFireOrders = findAndFire.find(mapInfo, player, teammate, enemyPlayer, enemyChild);
        if (!findAndFireOrders.isEmpty()) {
            return findAndFireOrders;
        }

        List<Order> randomRunOrders = randomRun.find(mapInfo, player);
        if (!randomRunOrders.isEmpty()) {
            return randomRunOrders;
        }

        return List.of();
    }
}
