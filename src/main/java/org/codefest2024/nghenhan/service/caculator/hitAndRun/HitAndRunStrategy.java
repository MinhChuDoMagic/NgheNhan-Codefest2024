package org.codefest2024.nghenhan.service.caculator.hitAndRun;

import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.caculator.farming.GodFarmStrategy;
import org.codefest2024.nghenhan.service.caculator.farming.NormalFarmStrategy;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.caculator.usecase.CollectSpoilsStrategy;
import org.codefest2024.nghenhan.service.caculator.usecase.DodgeStrategy;
import org.codefest2024.nghenhan.service.caculator.usecase.KeepDistanceStrategy;
import org.codefest2024.nghenhan.service.caculator.usecase.UseSkillStrategy;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.Utils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class HitAndRunStrategy implements Strategy {
    private final NormalFarmStrategy normalFarmStrategy = new NormalFarmStrategy();
    private final KeepDistanceStrategy keepDistanceStrategy = new KeepDistanceStrategy();
    private final UseSkillStrategy useSkillStrategy = new UseSkillStrategy();
    private final GodFarmStrategy godFarmStrategy = new GodFarmStrategy();
    private final DodgeStrategy dodgeStrategy = new DodgeStrategy();
    private final CollectSpoilsStrategy collectSpoilsStrategy = new CollectSpoilsStrategy();

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

        updateMap(mapInfo.map, myPlayer, myChild, enemyPlayer, enemyChild, mapInfo.bombs, mapInfo.weaponWinds, mapInfo.weaponHammers);

        if (myPlayer != null) {
            if (!myPlayer.hasTransform) {
                return normalFarmStrategy.find(gameInfo, myPlayer);
            } else {
                updateSkillData(enemyPlayer, mapInfo.weaponHammers);
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

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player teammate, Player enemy, Player enemyChild) {
        List<Order> useSkillOrders = useSkillStrategy.find(mapInfo, player, teammate, enemy, enemyChild);
        if (!useSkillOrders.isEmpty()) {
            return useSkillOrders;
        }

        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemy != null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> keepDistanceOrders = keepDistanceStrategy.find(mapInfo, player, enemy);
        if (!keepDistanceOrders.isEmpty()) {
            return keepDistanceOrders;
        }

//        List<Order> dodgeBombsOrders = dodgeStrategy.find(mapInfo, player);
//        if (!dodgeBombsOrders.isEmpty()) {
//            return dodgeBombsOrders;
//        }

        List<Order> collectSpoilOrders = collectSpoilsStrategy.find(mapInfo, player, Utils.filterNonNull(teammate, enemy, enemyChild));
        if (!collectSpoilOrders.isEmpty()) {
            return collectSpoilOrders;
        }

        List<Order> godFarmOrders = godFarmStrategy.find(mapInfo, player);
        if (!godFarmOrders.isEmpty()) {
            return godFarmOrders;
        }

        return List.of();
    }

    private void updateMap(int[][] map, Player player, Player child, Player enemy, Player enemyChild, List<Bomb> bombs, List<Wind> winds, List<Hammer> hammers) {
        updateMap(map, bombs, hammers, winds);
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

    private void updateMap(int[][] map, List<Bomb> bombs, List<Hammer> hammers, List<Wind> winds) {
        List<int[]> directions = CalculateUtils.getDirections();
        MapSize size = new MapSize(map.length, map[0].length);

        for (Bomb bomb : bombs) {
            map[bomb.row][bomb.col] = MapInfo.BOMB;
            directions.forEach(dir -> markBombExplosion(map, bomb, dir[0], dir[1], size));
        }

        hammers.forEach(hammer -> markHammerExplosion(map, hammer, size));

        for (Wind wind:winds){
            map[wind.currentRow][wind.currentCol] = MapInfo.WIND;
        }
    }

    private void markBombExplosion(int[][] map, Bomb bomb, int rowDir, int colDir, MapSize size){
        for (int i = 1; i <= bomb.power; i++) {
            int newRow = bomb.row + i * rowDir;
            int newCol = bomb.col + i * colDir;

            // Check boundaries
            if (newRow < 0 || newRow >= size.rows || newCol < 0 || newCol >= size.cols) {
                break;
            }

            if(map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] == MapInfo.DESTROYED){
                map[newRow][newCol] = MapInfo.BOMB_EXPLODE;
            }
        }
    }

    private void markHammerExplosion(int[][] map, Hammer hammer, MapSize mapSize){
        int centerRow = hammer.destination.row;
        int centerCol = hammer.destination.col;
        int power = hammer.power;

        for (int row = centerRow - power; row <= centerRow + power; row++) {
            for (int col = centerCol - power; col <= centerCol + power; col++) {
                // Check boundaries
                if (row < 0 || row >= mapSize.rows || col < 0 || col >= mapSize.cols) {
                    continue;
                }

                if (map[row][col] == MapInfo.BLANK || map[row][col] == MapInfo.DESTROYED) {
                    map[row][col] = MapInfo.HAMMER_EXPLODE;
                }
            }
        }
    }

    private void updateSkillData(Player enemy, List<Hammer> hammers) {
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
}
