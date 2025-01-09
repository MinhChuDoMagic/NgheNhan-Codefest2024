package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.AStarFinder;
import org.codefest2024.nghenhan.service.finder.BombPlaceFinder;
import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.FinderUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class StunAndBomb {
    private final AStarFinder aStarFinder = AStarFinder.getInstance();
    private final BombPlaceFinder bombPlaceFinder = BombPlaceFinder.getInstance();

    public List<Order> findAndBomb(MapInfo mapInfo, Player player, List<Player> enemies) {
        for (Player enemy : enemies) {
            List<Order> orders = findAndBomb(mapInfo, player, enemy);
            if (!orders.isEmpty()) {
                return orders;
            }
        }

        return List.of();
    }

    public List<Order> findAndBomb(MapInfo mapInfo, Player player, Player enemy) {
        long enemyLastStunTime = enemy.isChild ? InGameInfo.enemyChildLastStunTime : InGameInfo.enemyLastStunTime;
        if (enemy.isStun
                && Instant.now().toEpochMilli() - enemyLastStunTime > (Bomb.STUN_TIME - Bomb.BOMB_EXPLORE_TIME) * 1000
                && !SkillUtils.isBombCooldown(player.delay, player.isChild)) {
            if (SkillUtils.isHitBomb(enemy.currentPosition, new Bomb(player.currentPosition, player.power))) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 2) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(Dir.ACTION, player.isChild));
                return orders;
            }

            Node bombNode = bombPlaceFinder.findBombAttack(mapInfo.map, player.currentPosition, enemy.currentPosition, player.power, mapInfo.size);
            if (bombNode != null && bombNode.reconstructPath().length() <= 6) {
                return List.of(new Dir(bombNode.reconstructPath(), player.isChild));
            }
        }

        return List.of();
    }

    public List<Order> findAndStun(MapInfo mapInfo, Player player, List<Player> enemies) {
        for (Player enemy : enemies) {
            List<Order> orders = findAndStun(mapInfo, player, enemy);
            if (!orders.isEmpty()) {
                return orders;
            }
        }

        return List.of();
    }

    public List<Order> findAndStun(MapInfo mapInfo, Player player, Player enemy) {
        long enemyLastStunTime = enemy.isChild ? InGameInfo.enemyChildLastStunTime : InGameInfo.enemyLastStunTime;
        if (CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) <= 3
                && Instant.now().toEpochMilli() - enemyLastStunTime > Bomb.STUN_COOLDOWN
                && (InGameInfo.enemyType != 2 || !mapInfo.enemyIsMarried || !enemy.haveSpecialWeapon || mapInfo.enemyTimeToUseSpecialWeapons == 0)) {
            String dir = aStarFinder.findVer3(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size);
            if (!dir.isEmpty()) {
                return FinderUtils.processDirWithBrick(dir, player.isChild, player.currentWeapon);
            }
        }

        return List.of();
    }
}
