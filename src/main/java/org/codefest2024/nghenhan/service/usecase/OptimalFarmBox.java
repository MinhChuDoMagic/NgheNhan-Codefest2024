package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BombPlaceFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class OptimalFarmBox {
    private BombPlaceFinder bombPlaceFinder = BombPlaceFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player player) {

        AStarNode bombPlaceNode = bombPlaceFinder.find(mapInfo.map, player.currentPosition, player.power, mapInfo.size);
        if (bombPlaceNode != null) {
            String dir = bombPlaceNode.reconstructPath();

            if(dir.isEmpty()){
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
}
