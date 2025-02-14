package org.codefest2024.nghenhan.service.usecase;

import org.codefest2024.nghenhan.service.finder.BFSFinder;
import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;
import org.codefest2024.nghenhan.utils.Utils;

import java.util.List;

public class UseSkill {
    BFSFinder bfsFinder = BFSFinder.getInstance();

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
            case Player.MOUNTAIN -> useMountainSkill(mapInfo, player, teammate, enemies);
            case Player.SEA -> useSeaSkill(mapInfo.map, player, enemies);
            default -> throw new IllegalStateException("Invalid transformType: " + InGameInfo.playerType);
        };
    }

    private List<Order> useMountainSkill(MapInfo mapInfo, Player player, Player teammate, List<Player> enemies) {
        for (Player enemy : enemies) {
            if (Math.abs(player.currentPosition.col - enemy.currentPosition.col) <= Hammer.RANGE
                    && Math.abs(player.currentPosition.row - enemy.currentPosition.row) <= Hammer.RANGE
                    && SkillUtils.inHammerRange2(player.currentPosition, enemy.currentPosition, mapInfo.bombs)
                    && isSafeHammer(Utils.filterNonNull(player, teammate), new Hammer(enemy.currentPosition))) {
                return List.of(new Action(Action.USE_WEAPON, new Payload(enemy.currentPosition), player.isChild));
            }

            if (CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) <= 14) {
                String dir = bfsFinder.findEnemy(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size).reconstructPath();
                if (!dir.isEmpty() && dir.length() < 10) {
                    return List.of(new Dir(dir, player.isChild));
                }
            }
        }
        return List.of();
    }

    public List<Order> useMountainSkillDirect(MapInfo mapInfo, Player player, Player teammate, Player enemy, Player enemyChild) {
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

        for (Player enemyPlayer : enemies) {
            if (Math.abs(player.currentPosition.col - enemyPlayer.currentPosition.col) <= Hammer.RANGE
                    && Math.abs(player.currentPosition.row - enemyPlayer.currentPosition.row) <= Hammer.RANGE
                    && SkillUtils.inHammerRange2(player.currentPosition, enemyPlayer.currentPosition, mapInfo.bombs)
                    && isSafeHammer(Utils.filterNonNull(player, teammate), new Hammer(enemyPlayer.currentPosition))) {
                return List.of(new Action(Action.USE_WEAPON, new Payload(enemyPlayer.currentPosition), player.isChild));
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
