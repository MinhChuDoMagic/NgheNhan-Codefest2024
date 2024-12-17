package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.AStarFinder;
import org.codefest2024.nghenhan.service.finder.PowerFarmFinder;
import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.FinderUtils;

import java.util.List;

public class CollectSpoils {
    private final AStarFinder aStarFinder = AStarFinder.getInstance();
    private final PowerFarmFinder powerFarmFinder = PowerFarmFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer, List<Player> otherPlayers) {
        for (Spoil spoil : mapInfo.spoils) {
            if (CalculateUtils.isNearest(myPlayer.currentPosition, spoil, otherPlayers.stream().map(player -> player.currentPosition).toList())
                    && CalculateUtils.manhattanDistance(myPlayer.currentPosition, spoil) < 12) {
                String dir = aStarFinder.find(mapInfo.map, myPlayer.currentPosition, spoil, mapInfo.size);
                if (!dir.contains(Dir.ACTION)) {
                    return List.of(new Dir(dir, myPlayer.isChild));
                }
            }
        }
        return List.of();
    }

    public List<Order> findVer2(MapInfo mapInfo, Player player) {
        if (!mapInfo.spoils.isEmpty()) {
            AStarNode spoilNode = powerFarmFinder.findSpoil(mapInfo.map, player.currentPosition, mapInfo.size);
            if (spoilNode != null) {
                String dir = spoilNode.reconstructPath();

                if (!dir.isEmpty()) {
                    return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
                }
            }
        }

        return List.of();
    }
}
