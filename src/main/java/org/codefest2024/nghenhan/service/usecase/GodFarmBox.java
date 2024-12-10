package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BFSFinder;
import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class GodFarmBox {
    private final BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer) {
        List<Order> orders = new ArrayList<>();

        if (mapInfo.playerBomb == null) {
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

