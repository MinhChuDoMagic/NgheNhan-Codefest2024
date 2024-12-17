package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BFSFinder;
import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class GodFarmBox {
    private final BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player player) {
        List<Order> orders = new ArrayList<>();

        if (mapInfo.playerBombs.isEmpty()) {
            Node boxNode = bfsFinder.find(mapInfo.map, player.currentPosition, MapInfo.BOX, mapInfo.size);
            if (boxNode.parent != null) {
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(boxNode.parent.reconstructPath() + Dir.ACTION, player.isChild));
            }
        }

        return orders;
    }
}

