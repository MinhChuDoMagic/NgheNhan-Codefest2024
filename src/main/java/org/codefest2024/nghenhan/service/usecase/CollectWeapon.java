package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.AStarFinder;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Player;
import org.codefest2024.nghenhan.service.socket.data.WeaponPlace;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.FinderUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.util.List;

public class CollectWeapon {
    private final AStarFinder aStarFinder = AStarFinder.getInstance();
    private static final int SAFE_DISTANCE = 10;

    public List<Order> find(MapInfo mapInfo, Player player, Player enemy, Player enemyChild) {
        WeaponPlace weaponPlace = player.isChild ? mapInfo.childWeaponPlace : mapInfo.playerWeaponPlace;
        if (weaponPlace != null && isSafeEnemies(mapInfo, player, enemy, enemyChild)) {
            String dir = aStarFinder.findVer2(mapInfo.map, player.currentPosition, weaponPlace, mapInfo.size);
            if (!dir.isEmpty()) {
                return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
            }
        }
        return List.of();
    }

    private boolean isSafeEnemies(MapInfo mapInfo, Player player, Player enemy, Player enemyChild) {
        return !mapInfo.enemyIsMarried
                || (enemy.timeToUseSpecialWeapons == 0 && (enemyChild == null || enemyChild.timeToUseSpecialWeapons == 0))
                || (isSafeEnemy(player, enemy) && isSafeEnemy(player, enemyChild));
    }

    private boolean isSafeEnemy(Player player, Player enemy) {
        return enemy == null
                || SkillUtils.isEnemySkillCooldown(enemy.isChild)
                || CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) > SAFE_DISTANCE;
    }
}
