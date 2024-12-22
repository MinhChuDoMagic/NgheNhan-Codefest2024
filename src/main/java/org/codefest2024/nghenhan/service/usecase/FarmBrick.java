package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.PowerFarmFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Player;
import org.codefest2024.nghenhan.utils.FinderUtils;

import java.util.List;

public class FarmBrick {
    private final PowerFarmFinder powerFarmFinder = PowerFarmFinder.getInstance();

    public List<Order> farmBrick(MapInfo mapInfo, Player player) {
        AStarNode brickNode = powerFarmFinder.findBrick(mapInfo.map, player.currentPosition, mapInfo.size);
        if (brickNode != null) {
            String dir = brickNode.reconstructPath();
            if (!dir.isEmpty()) {
                return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
            }
        }

        return List.of();
    }
}
