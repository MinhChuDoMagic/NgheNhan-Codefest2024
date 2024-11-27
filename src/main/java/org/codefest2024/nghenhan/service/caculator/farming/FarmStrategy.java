package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.CollectSpoilsStrategy;
import org.codefest2024.nghenhan.service.caculator.DodgeStrategy;
import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.caculator.UseSkillStrategy;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.Utils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class FarmStrategy implements Strategy {
    private final NormalFarmStrategy normalFarmStrategy = new NormalFarmStrategy();
    private final GodFarmStrategy godFarmStrategy = new GodFarmStrategy();
    private final DodgeStrategy dodgeStrategy = new DodgeStrategy();
    private final CollectSpoilsStrategy collectSpoilsStrategy = new CollectSpoilsStrategy();
    private final UseSkillStrategy useSkillStrategy = new UseSkillStrategy();

    @Override
    public List<Order> find(GameInfo gameInfo) {
        MapInfo mapInfo = gameInfo.map_info;
        Player myPlayer = null;
        Player enemyPlayer = null;
        Player myChild = null;
        Player enemyChild = null;
        for (Player player : mapInfo.players) {
            if (player.id.startsWith(Constants.KEY_TEAM)) {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    myChild = player;
                } else {
                    myPlayer = player;
                }
            } else {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    enemyChild = player;
                } else {
                    enemyPlayer = player;
                }
            }
        }

        mapInfo.map = updateMap(
                mapInfo.map,
                Utils.filterNonNull(myPlayer, myChild, enemyPlayer, enemyChild),
                mapInfo.bombs
        );

        if (myPlayer != null) {
            if (!myPlayer.hasTransform) {
                return normalFarmStrategy.find(gameInfo, myPlayer);
            } else {
                updateEnemyData(enemyPlayer, mapInfo.weaponHammers);
                List<Order> orders = playerStrategy(mapInfo, myPlayer, myChild, enemyPlayer, enemyChild);
                if (myChild != null) {
                    orders = new ArrayList<>(orders);
                    orders.addAll(playerStrategy(mapInfo, myChild, myPlayer, enemyPlayer, enemyChild));
                }
                return orders;
            }
        }

        return List.of();
    }

    private int[][] updateMap(int[][] map, List<Player> players, List<Bomb> bombs) {
        return updateMap(map,
                Utils.combineList(
                        players.stream().map(player -> player.currentPosition).toList(),
                        bombs.stream().map(Position.class::cast).toList()
                ));
    }

    private int[][] updateMap(int[][] map, List<Position> positions) {
        for (Position position : positions) {
            if (map[position.row][position.col] == MapInfo.BADGE) {
                map[position.row][position.col] = MapInfo.CAPTURED_BADGE;
            } else {
                map[position.row][position.col] = MapInfo.WALL;
            }
        }

        return map;
    }

    private void updateEnemyData(Player enemy, List<WeaponHammer> hammers) {
        if (InGameInfo.enemyType == 0 && enemy != null) {
            InGameInfo.enemyType = enemy.transformType;
        }

        for (WeaponHammer hammer : hammers) {
            if (!hammer.playerId.startsWith(Constants.KEY_TEAM)) {
                if (hammer.playerId.endsWith(Constants.KEY_CHILD)) {
                    InGameInfo.enemyChildLastSkillTime = hammer.createdAt;
                } else {
                    InGameInfo.enemyLastSkillTime = hammer.createdAt;
                }
            }
        }
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player teammate, Player enemyPlayer, Player enemyChild) {
        List<Order> dodgeBombsOrders = dodgeStrategy.find(mapInfo, player);
        if (!dodgeBombsOrders.isEmpty()) {
            return dodgeBombsOrders;
        }

        List<Order> useSkillOrders = useSkillStrategy.find(mapInfo, player, enemyPlayer, enemyChild);
        if (!useSkillOrders.isEmpty()) {
            return useSkillOrders;
        }

        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemyPlayer != null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> collectSpoilOrders = collectSpoilsStrategy.find(mapInfo, player, Utils.filterNonNull(teammate, enemyPlayer, enemyChild));
        if (!collectSpoilOrders.isEmpty()) {
            return collectSpoilOrders;
        }

        return godFarmStrategy.find(mapInfo, player);
    }
}
