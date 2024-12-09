package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.service.usecase.*;

import java.util.ArrayList;
import java.util.List;

public class HitAndRunStrategy implements Strategy {
    private final FindBadge findBadge = new FindBadge();
    private final KeepDistance keepDistance = new KeepDistance();
    private final UseSkill useSkill = new UseSkill();
    private final Dodge dodge = new Dodge();
    private final RandomRun randomRun = new RandomRun();
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
        List<Order> useSkillOrders = useSkill.useMountainSkillDirect(player, teammate, enemy, enemyChild);
        if (!useSkillOrders.isEmpty()) {
            return useSkillOrders;
        }

        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemy != null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> runOrders = keepDistance.run(mapInfo, player, enemy);
        if (!runOrders.isEmpty()) {
            return runOrders;
        }

        List<Order> dodgeBombsOrders = dodge.findAndKeepDistance(mapInfo, player, enemy);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        List<Order> farmOrders = keepDistance.farm(mapInfo, player, enemy);
        if (!farmOrders.isEmpty()) {
            return farmOrders;
        }

        List<Order> randomRunOrders = randomRun.find(mapInfo, player);
        if (!randomRunOrders.isEmpty()) {
            return randomRunOrders;
        }

        return List.of();
    }
}
