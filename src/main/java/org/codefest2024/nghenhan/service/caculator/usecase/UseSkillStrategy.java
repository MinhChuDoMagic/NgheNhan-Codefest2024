package org.codefest2024.nghenhan.service.caculator.usecase;

import org.codefest2024.nghenhan.service.caculator.finder.BFSFinder;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UseSkillStrategy {
    BFSFinder bfsFinder = BFSFinder.getInstance();

    public List<Order> find(MapInfo mapInfo, Player myPlayer, Player teammate, Player enemyPlayer, Player enemyChild) {
        if (myPlayer.timeToUseSpecialWeapons == 0
                || (enemyPlayer != null && !enemyPlayer.hasTransform)) {
            return List.of();
        }

        List<Player> enemies = Utils.filterNonNull(enemyPlayer, enemyChild);
        if (enemies.isEmpty() || isCooldown(myPlayer.isChild)) {
            return List.of();
        }

        return switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> useMountainSkill(myPlayer, teammate, enemies, mapInfo.map, mapInfo.size);
            case Player.SEA -> useSeaSkill(mapInfo.map, myPlayer, enemies);
            default -> throw new IllegalStateException("Invalid transformType: " + InGameInfo.playerType);
        };
    }

    private List<Order> useMountainSkill(Player player, Player teammate, List<Player> enemies, int[][] map, MapSize mapSize) {
        for (Player enemy : enemies) {
            List<Order> orders = new ArrayList<>();
            if(CalculateUtils.manhattanDistance(player.currentPosition, enemy.currentPosition) <= 14){
                String dir = bfsFinder.findEnemy(map, player.currentPosition, enemy.currentPosition, mapSize).reconstructPath();
                if (!dir.isEmpty() && dir.length() < 10){
                    orders.add(new Dir(dir, player.isChild));
                }
            }

            if (Math.abs(player.currentPosition.col - enemy.currentPosition.col) <= WeaponHammer.RANGE
                    && Math.abs(player.currentPosition.row - enemy.currentPosition.row) <= WeaponHammer.RANGE
                    && CalculateUtils.inHammerRange(player.currentPosition, enemy.currentPosition)
                    && isSafeHammer(Utils.filterNonNull(player, teammate), new WeaponHammer(enemy.currentPosition))) {
                orders.add(new Action(Action.USE_WEAPON, new Payload(enemy.currentPosition), player.isChild));
                return orders;
            }
        }
        return List.of();
    }

    private boolean isSafeHammer(List<Player> players, WeaponHammer hammer) {
        return players.stream()
                .noneMatch(player -> CalculateUtils.isHitHammer(player.currentPosition, hammer));
    }

    private List<Order> useSeaSkill(int[][] map, Player player, List<Player> enemies) {
        for (Player enemy : enemies) {
            String dir = windDirection(map, player.currentPosition, enemy.currentPosition);
            if (!dir.isEmpty()) {
                updatePlayerSkillTime(player.isChild);
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

    private void updatePlayerSkillTime(boolean isChild) {
        if (isChild) {
            InGameInfo.myChildLastSkillTime = Instant.now().toEpochMilli();
        } else {
            InGameInfo.myPlayerLastSkillTime = Instant.now().toEpochMilli();
        }
    }

    private boolean isCooldown(boolean isChild) {
        long cooldown = switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> WeaponHammer.COOL_DOWN;
            case Player.SEA -> WeaponWind.COOL_DOWN;
            default -> 0L;
        } * 1000;

        return (isChild && Instant.now().toEpochMilli() - InGameInfo.myChildLastSkillTime <= cooldown)
                || (!isChild && Instant.now().toEpochMilli() - InGameInfo.myPlayerLastSkillTime <= cooldown);
    }
}
