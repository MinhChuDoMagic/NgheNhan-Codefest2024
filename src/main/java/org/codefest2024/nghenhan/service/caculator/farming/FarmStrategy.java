package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.List;

public class FarmStrategy implements Strategy {
    private final NormalFarmStrategy normalFarmStrategy = new NormalFarmStrategy();
    private final GodFarmStrategy godFarmStrategy = new GodFarmStrategy();
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
            return normalFarmStrategy.find(gameInfo, myPlayer);
        } else {
            return godFarmStrategy.find(gameInfo, myPlayer);
        }

    }
}
