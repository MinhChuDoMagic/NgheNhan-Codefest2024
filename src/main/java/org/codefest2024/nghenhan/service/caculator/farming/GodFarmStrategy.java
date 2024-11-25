package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.data.Node;
import org.codefest2024.nghenhan.service.caculator.finder.BFSFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class GodFarmStrategy {
    private final BFSFinder bfsFinder = new BFSFinder();

    public List<Order> find(GameInfo gameInfo, Player myPlayer) {
        MapInfo mapInfo = gameInfo.map_info;
        List<Order> orders = new ArrayList<>();

        if (myPlayer.currentWeapon != 2) {
            orders.add(new Action(Action.SWITCH_WEAPON));
        }

        Bomb myBomb = null;
        for (Bomb bomb : mapInfo.bombs) {
            if (Constants.KEY_TEAM.equals(bomb.playerId)) {
                myBomb = bomb;
            }
        }

        if (myBomb == null) {
            Node bombNode = bfsFinder.find(mapInfo.map, myPlayer.currentPosition, MapInfo.BOX, mapInfo.size);
            Bomb bomb = new Bomb();
            bomb.row = bombNode.row;
            bomb.col = bombNode.col;
            bomb.power = myPlayer.power;
            List<Bomb> bombs = List.of(bomb);

            Node safeNode = bfsFinder.findSafe(mapInfo.map, new Position(bombNode.row, bombNode.col), bombs, mapInfo.size);
            orders.add(new Dir(bombNode.reconstructPath() + "b" + safeNode.reconstructPath()));
        }


        return orders;
    }
}

