package org.codefest2024.nghenhan.service.caculator.farming;

import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.caculator.usecase.*;
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
    private final FindAndFireStrategy findAndFireStrategy = new FindAndFireStrategy();
    private final RandomRunStrategy randomRunStrategy = new RandomRunStrategy();

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

        updateMap(mapInfo.map, myPlayer, myChild, enemyPlayer, enemyChild, mapInfo.bombs);

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

    private void updateMap(int[][] map, Player player, Player child, Player enemy, Player enemyChild, List<Bomb> bombs) {
        updateMap(map, bombs);
        updateMap(map, player, child, enemy, enemyChild);
    }

    private void updateMap(int[][] map, Player player, Player child, Player enemy, Player enemyChild) {
        if (player != null) {
            if (map[player.currentPosition.row][player.currentPosition.col] == MapInfo.BADGE) {
                map[player.currentPosition.row][player.currentPosition.col] = MapInfo.CAPTURED_BADGE;
            } else {
                map[player.currentPosition.row][player.currentPosition.col] = MapInfo.PLAYER;
            }
        }

        if (child != null) {
            map[child.currentPosition.row][child.currentPosition.col] = MapInfo.CHILD;
        }

        if (enemy != null) {
            if (map[enemy.currentPosition.row][enemy.currentPosition.col] == MapInfo.BADGE) {
                map[enemy.currentPosition.row][enemy.currentPosition.col] = MapInfo.CAPTURED_BADGE;
            } else {
                map[enemy.currentPosition.row][enemy.currentPosition.col] = MapInfo.ENEMY;
            }
        }

        if (enemyChild != null) {
            map[enemyChild.currentPosition.row][enemyChild.currentPosition.col] = MapInfo.ENEMY_CHILD;
        }
    }

    private void updateMap(int[][] map, List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            map[bomb.row][bomb.col] = MapInfo.BOMB;
        }
    }


    private void updateEnemyData(Player enemy, List<Hammer> hammers) {
        if (InGameInfo.enemyType == 0 && enemy != null) {
            InGameInfo.enemyType = enemy.transformType;
        }

        for (Hammer hammer : hammers) {
            if (hammer.playerId.startsWith(Constants.KEY_TEAM)) {
                if (hammer.playerId.endsWith(Constants.KEY_CHILD)) {
                    InGameInfo.myChildLastSkillTime = hammer.createdAt;
                } else {
                    InGameInfo.myPlayerLastSkillTime = hammer.createdAt;
                }
            } else {
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

        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemyPlayer != null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> useSkillOrders = useSkillStrategy.find(mapInfo, player, teammate, enemyPlayer, enemyChild);
        if (!useSkillOrders.isEmpty()) {
            return useSkillOrders;
        }

        List<Order> collectSpoilOrders = collectSpoilsStrategy.find(mapInfo, player, Utils.filterNonNull(teammate, enemyPlayer, enemyChild));
        if (!collectSpoilOrders.isEmpty()) {
            return collectSpoilOrders;
        }

        List<Order> godFarmOrders = godFarmStrategy.find(mapInfo, player);
        if (!godFarmOrders.isEmpty()) {
            return godFarmOrders;
        }

        List<Order> findAndFireOrders = findAndFireStrategy.find(mapInfo, player, teammate, enemyPlayer, enemyChild);
        if (!findAndFireOrders.isEmpty()) {
            return findAndFireOrders;
        }

        List<Order> randomRunOrders = randomRunStrategy.find(mapInfo, player);
        if (!randomRunOrders.isEmpty()) {
            return randomRunOrders;
        }

        return List.of();
    }
}
