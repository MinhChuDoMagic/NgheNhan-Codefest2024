package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BombPlaceFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.*;
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
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 1) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(dir, player.isChild));
                return orders;
            }
        }
        return List.of();
    }

    public List<Order> findVer2(MapInfo mapInfo, Player player) {
        AStarNode bombPlaceNode = bombPlaceFinder.find(mapInfo.map, player.currentPosition, player.power, mapInfo.size);

        if (bombPlaceNode != null
                && bombPlaceNode.reconstructPath().isEmpty()
                && !SkillUtils.isBombCooldown(player.delay, player.isChild)) {

            List<Order> orders = new ArrayList<>();
            if (player.currentWeapon != 2) {
                orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
            }
            orders.add(new Dir(Dir.ACTION, player.isChild));
            return orders;
        }

//        List<Order> collectSpoilOrders = collectSpoils.findVer2(mapInfo, player);
//        if (!collectSpoilOrders.isEmpty()) {
//            return collectSpoilOrders;
//        }

        if (bombPlaceNode != null && !bombPlaceNode.reconstructPath().isEmpty()) {
            return FinderUtils.processDirWithBrick(bombPlaceNode.reconstructPath(), player.isChild, player.currentWeapon);
        }

        return List.of();
    }
}
