package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.caculator.finder.AStarFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.List;

public class CollectSpoilsStrategy {
    private final AStarFinder aStarFinder = AStarFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer, List<Player> otherPlayers) {
        for (Spoil spoil : mapInfo.spoils) {
            if (CalculateUtils.isNearest(
                    myPlayer.currentPosition,
                    spoil,
                    otherPlayers.stream().map(player -> player.currentPosition).toList()
            )) {
                String dir = aStarFinder.find(mapInfo.map, myPlayer.currentPosition, spoil, mapInfo.size);
                if (!dir.contains("b")) {
                    return List.of(new Dir(dir, myPlayer.isChild));
                }
            }
        }
        return List.of();
    }
}
