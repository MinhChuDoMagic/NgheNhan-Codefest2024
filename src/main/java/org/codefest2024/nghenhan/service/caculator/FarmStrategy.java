package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.List;

public class FarmStrategy implements Strategy {
    @Override
    public List<Order> find(GameInfo gameInfo) {
        MapInfo mapInfo = gameInfo.map_info;
        Player myPlayer = null;
        for (Player player : mapInfo.players) {
            if (Constants.KEY_TEAM.equals(player.id)) {
                myPlayer = player;
            }
        }

        if (!myPlayer.hasTransform) {
            String dir = new AStarFinder().find(mapInfo.map, myPlayer.currentPosition, mapInfo.size);
            return List.of(new Dir(dir));
        } else if (myPlayer.currentWeapon != 2) {
            return List.of(new Action(Action.SWITCH_WEAPON));
        }

        return List.of();
    }
}
