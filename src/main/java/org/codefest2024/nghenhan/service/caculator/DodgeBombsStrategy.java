package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.caculator.finder.BFSFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.ArrayList;
import java.util.List;

public class DodgeBombsStrategy {
    private final BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer) {
        List<Order> orders = new ArrayList<>();

        List<Bomb> dangerousBombsForPlayer = mapInfo.bombs.stream().filter(bomb -> isDangerousBomb(bomb, myPlayer.currentPosition)).toList();
        if (!dangerousBombsForPlayer.isEmpty()) {
            String dir = bfsFinder.findSafe(mapInfo.map, myPlayer.currentPosition, dangerousBombsForPlayer, mapInfo.size).reconstructPath();
            orders.add(new Dir(dir, myPlayer.isChild));
        }

        return orders;
    }

    private boolean isDangerousBomb(Bomb bomb, Position currPosition) {
        return CalculateUtils.manhattanDistance(currPosition, bomb) < 3 * bomb.power;
    }
}
