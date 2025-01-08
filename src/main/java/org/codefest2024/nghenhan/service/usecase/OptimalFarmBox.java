package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BombPlaceFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.FinderUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.util.ArrayList;
import java.util.List;

public class OptimalFarmBox {
    private BombPlaceFinder bombPlaceFinder = BombPlaceFinder.getInstance();
    private final CollectSpoils collectSpoils = new CollectSpoils();

    public List<Order> find(MapInfo mapInfo, Player player) {

        AStarNode bombPlaceNode = bombPlaceFinder.find(mapInfo.map, player.currentPosition, player.power, mapInfo.size);
        if (bombPlaceNode != null) {
            String dir = bombPlaceNode.reconstructPath();

            if (dir.isEmpty()) {
                if (mapInfo.playerBombs.isEmpty()) {
                    List<Order> orders = new ArrayList<>();
                    if (player.currentWeapon != 2) {
                        orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                    }
                    orders.add(new Dir(Dir.ACTION, player.isChild));
                    return orders;
                }
            } else {
                return FinderUtils.processDirWithBrick(bombPlaceNode.reconstructPath(), player.isChild, player.currentWeapon);
            }
        }
        return List.of();
    }

    public List<Order> findVer2(MapInfo mapInfo, Player player) {
        AStarNode bombPlaceNode = bombPlaceFinder.find(mapInfo.map, player.currentPosition, player.power, mapInfo.size);
        var playerBombs = player.isChild ? mapInfo.childBombs : mapInfo.playerBombs;
        if (bombPlaceNode != null
                && bombPlaceNode.reconstructPath().isEmpty()
//                && player.isChild ? mapInfo.childBombs.isEmpty() : mapInfo.playerBombs.isEmpty()
                && !SkillUtils.isBombCooldown(player.delay, player.isChild)
                && (isSafeBombPlace(mapInfo, player) || playerBombs.isEmpty())
        ) {

            List<Order> orders = new ArrayList<>();
            if (player.currentWeapon != 2) {
                orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
            }
            orders.add(new Dir(Dir.ACTION, player.isChild));
            System.out.println("--Place bomb--");
            orders.forEach(System.out::println);
            return orders;
        }

        List<Order> collectSpoilOrders = collectSpoils.findVer2(mapInfo, player);
        if (!collectSpoilOrders.isEmpty()) {
            System.out.println("--Collect spoil--");
            collectSpoilOrders.forEach(System.out::println);
            return collectSpoilOrders;
        }

        if (bombPlaceNode != null
                && !bombPlaceNode.reconstructPath().isEmpty()
                && playerBombs.size() < 2
                && (playerBombs.isEmpty() || bombPlaceNode.reconstructPath().length() < 9)
        ) {
            System.out.println("--farm--");
            System.out.println(bombPlaceNode.reconstructPath() + " - " + player.isChild);
            return FinderUtils.processDirWithBrick(bombPlaceNode.reconstructPath(), player.isChild, player.currentWeapon);
        }

        return List.of();
    }

    public boolean isSafeBombPlace(MapInfo mapInfo, Player player) {
        Node safeNode = bombPlaceFinder
                .findSafe(mapInfo.map, player.currentPosition, new Bomb(player.currentPosition, player.power), mapInfo.size);

        return !CalculateUtils.isNearBombExplored(mapInfo.map, player.currentPosition)
                && safeNode != null
                && safeNode.reconstructPath().length() <= 2;
    }
}
