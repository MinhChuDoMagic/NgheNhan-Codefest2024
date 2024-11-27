package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.finder.AStarFinder;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.List;

public class NormalFarmStrategy {
    private final AStarFinder aStarFinder = AStarFinder.getInstance();

    public List<Order> find(GameInfo gameInfo, Player myPlayer) {
        MapInfo mapInfo = gameInfo.map_info;

        if (mapInfo.map[myPlayer.currentPosition.row][myPlayer.currentPosition.col] == MapInfo.CAPTURED_BADGE) {
            return List.of();
        }

        Position destination = getNearestBadgePosition(mapInfo.map, myPlayer.currentPosition, mapInfo.size);
        String dir = aStarFinder.find(mapInfo.map, myPlayer.currentPosition, destination, mapInfo.size);
//        return List.of(new Dir(dir));
        return List.of(new Dir(dir.length() > 3 ? dir.substring(0, 3) : dir));
    }

    private Position getNearestBadgePosition(int[][] map, Position currentPosition, MapSize size) {
        Position ans = null;
        for (int r = 0; r < size.rows; r++) {
            for (int c = 0; c < size.cols; c++) {
                if (map[r][c] == MapInfo.BADGE &&
                        (ans == null
                                || Math.abs(ans.row - currentPosition.row) + Math.abs(ans.col - currentPosition.col)
                                > Math.abs(currentPosition.row - r) + Math.abs(currentPosition.col - c)
                        )
                ) {
                    ans = new Position(r, c);
                }
            }
        }
        return ans;
    }
}
