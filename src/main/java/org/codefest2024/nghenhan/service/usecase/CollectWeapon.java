package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.AStarFinder;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Player;
import org.codefest2024.nghenhan.service.socket.data.WeaponPlace;
import org.codefest2024.nghenhan.utils.FinderUtils;

import java.util.List;

public class CollectWeapon {
    private final AStarFinder aStarFinder = AStarFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player player) {
        WeaponPlace weaponPlace = player.isChild ? mapInfo.childWeaponPlace : mapInfo.playerWeaponPlace;
        if (weaponPlace != null) {
            String dir = aStarFinder.findVer2(mapInfo.map, player.currentPosition, weaponPlace, mapInfo.size);
            if (!dir.isEmpty()) {
                return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
            }
        }
        return List.of();
    }
}
