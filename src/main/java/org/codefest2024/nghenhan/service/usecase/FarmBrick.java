package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.KeepDistanceFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class FarmBrick {
    private final KeepDistanceFinder keepDistanceFinder = KeepDistanceFinder.getInstance();

    public List<Order> farmBrick(MapInfo mapInfo, Player player, Player enemy) {
        AStarNode boxNode = keepDistanceFinder.findBrick(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size);
        if (boxNode != null) {
            String dir = boxNode.reconstructPath();
            if (!dir.isEmpty()) {
                List<Order> orders = new ArrayList<>();
                if (dir.contains(Dir.ACTION) && player.currentWeapon != 1) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(dir, player.isChild));
                return orders;
            }
        }

        return List.of();
    }
}
