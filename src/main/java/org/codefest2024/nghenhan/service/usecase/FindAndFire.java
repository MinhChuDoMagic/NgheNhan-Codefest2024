package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.AStarFinder;
import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.SkillUtils;
import org.codefest2024.nghenhan.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FindAndFire {
    AStarFinder aStarFinder = AStarFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player player, Player teammate, Player enemy, Player enemyChild) {
        if (!mapInfo.playerIsMarried
                || !player.haveSpecialWeapon
                || (player.timeToUseSpecialWeapons == 0 && (teammate == null || teammate.timeToUseSpecialWeapons == 0))
                || (enemy != null && !enemy.hasTransform)) {
            return List.of();
        }

        List<Player> enemies = Utils.filterNonNull(enemy, enemyChild);
        if (enemies.isEmpty() || SkillUtils.isSkillCooldown(player.isChild)) {
            return List.of();
        }

        return switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> useMountainSkill(player, teammate, enemies, mapInfo.map, mapInfo.size);
            case Player.SEA -> useSeaSkill(mapInfo.map, player, enemies);
            default -> throw new IllegalStateException("Invalid transformType: " + InGameInfo.playerType);
        };
    }

    private List<Order> useMountainSkill(Player player, Player teammate, List<Player> enemies, int[][] map, MapSize mapSize) {
        for (Player enemy : enemies) {
            if (Math.abs(player.currentPosition.col - enemy.currentPosition.col) <= Hammer.RANGE
                    && Math.abs(player.currentPosition.row - enemy.currentPosition.row) <= Hammer.RANGE
                    && SkillUtils.inHammerRange(player.currentPosition, enemy.currentPosition)
                    && isSafeHammer(Utils.filterNonNull(player, teammate), new Hammer(enemy.currentPosition))) {
                return List.of(new Action(Action.USE_WEAPON, new Payload(enemy.currentPosition), player.isChild));
            }

            String dir = aStarFinder.find(map, player.currentPosition, enemy.currentPosition, mapSize);
            if (!dir.isEmpty()) {
                List<Order> orders = new ArrayList<>();
                if (player.currentWeapon != 1) {
                    orders.add(new Action(Action.SWITCH_WEAPON, player.isChild));
                }
                orders.add(new Dir(dir, player.isChild));
                return orders;
            }
        }
        return List.of();
    }

    private boolean isSafeHammer(List<Player> players, Hammer hammer) {
        return players.stream()
                .noneMatch(player -> SkillUtils.isHitHammer(player.currentPosition, hammer));
    }

    private List<Order> useSeaSkill(int[][] map, Player player, List<Player> enemies) {
        for (Player enemy : enemies) {
            String dir = SkillUtils.windDirection(map, player.currentPosition, enemy.currentPosition);
            if (!dir.isEmpty()) {
                return List.of(new Dir(dir, player.isChild), new Action(Action.USE_WEAPON, player.isChild));
            }
        }
        return List.of();
    }
}
