package org.codefest2024.nghenhan.service.caculator.usecase;

import org.codefest2024.nghenhan.service.caculator.data.AStarNode;
import org.codefest2024.nghenhan.service.caculator.finder.KeepDistanceFinderVer2;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class FarmBrickStrategy {
    private final KeepDistanceFinderVer2 keepDistanceFinderVer2 = KeepDistanceFinderVer2.getInstance();

    public List<Order> farmBrick(MapInfo mapInfo, Player player, Player enemy) {
        AStarNode boxNode = keepDistanceFinderVer2.findBrick(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size);
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
