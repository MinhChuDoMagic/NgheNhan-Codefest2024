package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.caculator.finder.BFSFinder;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.Utils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class UseSkillStrategy {
    private static long myPlayerLastSkillTime = 0;
    private static long myChildLastSkillTime = 0;
    private static long enemyLastSkillTime = 0;
    private static long enemyChildLastSkillTime = 0;

    private static int playerType = 0;
    private static int enemyType = 0;

    public List<Order> find(MapInfo mapInfo, Player myPlayer, Player enemyPlayer, Player enemyChild) {
        if (myPlayer.timeToUseSpecialWeapons == 0
                || (enemyPlayer != null && !enemyPlayer.hasTransform)) {
            return List.of();
        }

        updateData(myPlayer, enemyPlayer, mapInfo.weaponHammers, mapInfo.weaponWinds);

        List<Player> enemies = Utils.filterNonNull(enemyPlayer, enemyChild);
        if (enemies.isEmpty()) {
            return List.of();
        }

        return switch (playerType) {
            case Player.MOUNTAIN -> useMountainSkill(myPlayer, enemies);
            case Player.SEA -> useSeaSkill(mapInfo.map, myPlayer, enemies);
            default -> throw new IllegalStateException("Invalid transformType: " + playerType);
        };
    }

    private void updateData(Player myPlayer, Player enemy, List<WeaponHammer> hammers, List<WeaponWind> winds) {
        if (playerType == 0) {
            playerType = myPlayer.transformType;
        }

        if (enemyType == 0 && enemy != null) {
            enemyType = enemy.transformType;
        }


        for (WeaponHammer hammer : hammers) {
            if (hammer.playerId.startsWith(Constants.KEY_TEAM)) {
                if (hammer.playerId.endsWith(Constants.KEY_CHILD)) {
                    myChildLastSkillTime = hammer.createdAt;
                } else {
                    myPlayerLastSkillTime = hammer.createdAt;
                }
            } else {
                if (hammer.playerId.endsWith(Constants.KEY_CHILD)) {
                    enemyChildLastSkillTime = hammer.createdAt;
                } else {
                    enemyLastSkillTime = hammer.createdAt;
                }
            }
        }

        for (WeaponWind wind : winds) {
            if (wind.playerId.startsWith(Constants.KEY_TEAM)) {
                if (wind.playerId.endsWith(Constants.KEY_CHILD)) {
                    myChildLastSkillTime = wind.createAt;
                } else {
                    myPlayerLastSkillTime = wind.createAt;
                }
            } else {
                if (wind.playerId.endsWith(Constants.KEY_CHILD)) {
                    enemyChildLastSkillTime = wind.createAt;
                } else {
                    enemyLastSkillTime = wind.createAt;
                }
            }
        }
    }

    private List<Order> useMountainSkill(Player player, List<Player> enemies) {
        if (player.isChild) {
            if (Instant.now().toEpochMilli() - myChildLastSkillTime <= WeaponHammer.COOL_DOWN * 1000) {
                return List.of();
            }
        } else if (Instant.now().toEpochMilli() - myPlayerLastSkillTime <= WeaponHammer.COOL_DOWN * 1000) {
            return List.of();
        }


        for (Player enemy : enemies) {
            if (Math.abs(player.currentPosition.col - enemy.currentPosition.col) <= WeaponHammer.RANGE
                    && Math.abs(player.currentPosition.row - enemy.currentPosition.row) <= WeaponHammer.RANGE) {
                return List.of(new Action(Action.USE_WEAPON, new Payload(enemy.currentPosition), player.isChild));
            }
        }
        return List.of();
    }

    private List<Order> useSeaSkill(int[][] map, Player player, List<Player> enemies) {
        if (player.isChild) {
            if (Instant.now().toEpochMilli() - myChildLastSkillTime <= WeaponWind.COOL_DOWN * 1000) {
                return List.of();
            }
        } else if (Instant.now().toEpochMilli() - myPlayerLastSkillTime <= WeaponWind.COOL_DOWN * 1000) {
            return List.of();
        }

        for (Player enemy : enemies) {
            String dir = windDirection(map, player.currentPosition, enemy.currentPosition);
            if (!dir.isEmpty()) {
                return List.of(new Dir(dir, player.isChild), new Action(Action.USE_WEAPON, player.isChild));
            }
        }
        return List.of();
    }

    private String windDirection(int[][] map, Position player, Position enemy) {

        // Check Left Direction
        if (Math.abs(enemy.row - player.row) <= 1 && enemy.col < player.col) {
            var winds = List.of(
                    new WeaponWind(player.row - 1, player.col, 1),
                    new WeaponWind(player.row, player.col, 1),
                    new WeaponWind(player.row + 1, player.col, 1));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.LEFT;
            }
        }

        // Check Right Direction
        if (Math.abs(enemy.row - player.row) <= 1 && enemy.col > player.col) {
            var winds = List.of(
                    new WeaponWind(player.row - 1, player.col, 2),
                    new WeaponWind(player.row, player.col, 2),
                    new WeaponWind(player.row + 1, player.col, 2));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.RIGHT;
            }
        }

        // Check Up Direction
        if (Math.abs(enemy.col - player.col) <= 1 && enemy.row < player.row) {
            var winds = List.of(
                    new WeaponWind(player.row, player.col - 1, 3),
                    new WeaponWind(player.row, player.col, 3),
                    new WeaponWind(player.row, player.col + 1, 3));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.UP;
            }
        }

        // Check Down Direction
        if (Math.abs(enemy.col - player.col) <= 1 && enemy.row > player.row) {
            var winds = List.of(
                    new WeaponWind(player.row, player.col - 1, 4),
                    new WeaponWind(player.row, player.col, 4),
                    new WeaponWind(player.row, player.col + 1, 4));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.DOWN;
            }
        }

        return Dir.INVALID;
    }
}
