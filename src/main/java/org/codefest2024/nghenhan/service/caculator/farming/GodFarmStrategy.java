package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.data.Node;
import org.codefest2024.nghenhan.service.caculator.finder.BFSFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class GodFarmStrategy {
    private final BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer) {
        List<Order> orders = new ArrayList<>();

        Bomb myBomb = null;
        for (Bomb bomb : mapInfo.bombs) {
            if (bomb.playerId.startsWith(Constants.KEY_TEAM)
                    && (!myPlayer.isChild || bomb.playerId.endsWith(Constants.KEY_CHILD))) {
                myBomb = bomb;
            }
        }

        if (myBomb == null) {
            Node boxNode = bfsFinder.find(mapInfo.map, myPlayer.currentPosition, MapInfo.BOX, mapInfo.size);
            if (boxNode.parent != null) {
                if (myPlayer.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, myPlayer.isChild));
                }
                orders.add(new Dir(boxNode.parent.reconstructPath() + Dir.ACTION, myPlayer.isChild));
            }
        }

        return orders;
    }
}

