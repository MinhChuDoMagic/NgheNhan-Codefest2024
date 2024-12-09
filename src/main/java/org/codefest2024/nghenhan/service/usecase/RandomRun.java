package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BFSFinder;
import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Player;

import java.util.List;

public class RandomRun {
    BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player player) {
        String dir = bfsFinder.oneSafeStep(mapInfo.map, player.currentPosition, mapInfo.bombs, mapInfo.weaponHammers, mapInfo.weaponWinds);
        if (!dir.isEmpty()) {
            return List.of(new Dir(dir, player.isChild));
        }

        return List.of();
    }

}
