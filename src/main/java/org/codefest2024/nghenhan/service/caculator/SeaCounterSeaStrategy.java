package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.caculator.farming.NormalFarmStrategy;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.caculator.usecase.DodgeStrategy;
import org.codefest2024.nghenhan.service.caculator.usecase.EnemyNearbyStrategy;
import org.codefest2024.nghenhan.service.caculator.usecase.SeaStunAndSkillStrategy;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SeaCounterSeaStrategy implements Strategy {
    private final NormalFarmStrategy normalFarmStrategy = new NormalFarmStrategy();
    private final EnemyNearbyStrategy enemyNearbyStrategy = new EnemyNearbyStrategy();
    private final DodgeStrategy dodgeStrategy = new DodgeStrategy();
    private final SeaStunAndSkillStrategy seaStunAndSkillStrategy = new SeaStunAndSkillStrategy();

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

        updateMap(mapInfo.map,
                myPlayer, myChild, enemyPlayer, enemyChild,
                mapInfo.bombs, mapInfo.weaponWinds, mapInfo.weaponHammers,
                mapInfo.spoils);

        if (myPlayer != null) {
            if (!myPlayer.hasTransform) {
                return normalFarmStrategy.find(gameInfo, myPlayer);
            } else {
                updateSkillData(enemyPlayer, mapInfo.weaponHammers, mapInfo.weaponWinds);
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
        if(enemy.isStun){
            InGameInfo.isEnemyStun = true;
        }

//        List<Order> enemyNearbyOrders = enemyNearbyStrategy.find(mapInfo, player, enemy);
//        if (!enemyNearbyOrders.isEmpty()) {
//            return enemyNearbyOrders;
//        }

//        List<Order> dodgeBombsOrders = dodgeStrategy.findAndStand(mapInfo, player, enemy);
//        if (!dodgeBombsOrders.isEmpty()) {
//            return dodgeBombsOrders;
//        }

        List<Order> seaStunAndSkillOrders = seaStunAndSkillStrategy.find(mapInfo, player, enemy);
        if (!seaStunAndSkillOrders.isEmpty()) {
            return seaStunAndSkillOrders;
        }

        return List.of();
    }

    private void updateMap(int[][] map,
                           Player player, Player child, Player enemy, Player enemyChild,
                           List<Bomb> bombs, List<Wind> winds, List<Hammer> hammers,
                           List<Spoil> spoils) {
        updateMap(map, spoils);
        updateMap(map, bombs, hammers, winds);
        updateMap(map, player, child, enemy, enemyChild);
    }

    private void updateMap(int[][] map, List<Spoil> spoils) {
        for (Spoil spoil : spoils) {
            map[spoil.row][spoil.col] = MapInfo.SPOIL;
        }
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

        for (Wind wind : winds) {
            map[wind.currentRow][wind.currentCol] = MapInfo.WIND;
        }
    }

    private void markBombExplosion(int[][] map, Bomb bomb, int rowDir, int colDir, MapSize size) {
        for (int i = 1; i <= bomb.power; i++) {
            int newRow = bomb.row + i * rowDir;
            int newCol = bomb.col + i * colDir;

            // Check boundaries
            if (newRow < 0 || newRow >= size.rows || newCol < 0 || newCol >= size.cols) {
                break;
            }

            if (map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] == MapInfo.DESTROYED) {
                map[newRow][newCol] = MapInfo.BOMB_EXPLODE;
            }
        }
    }

    private void markHammerExplosion(int[][] map, Hammer hammer, MapSize mapSize) {
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

    private void updateSkillData(Player enemy, List<Hammer> hammers, List<Wind> winds) {
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

        for (Wind wind : winds) {
            long currentTime = Instant.now().toEpochMilli();
            if (wind.playerId.startsWith(Constants.KEY_TEAM)) {
                if (wind.playerId.endsWith(Constants.KEY_CHILD)
                        && currentTime - InGameInfo.myChildLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.myChildLastSkillTime = currentTime;
                } else if (currentTime - InGameInfo.myPlayerLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.myPlayerLastSkillTime = currentTime;
                }
            } else {
                if (wind.playerId.endsWith(Constants.KEY_CHILD)
                        && currentTime - InGameInfo.enemyChildLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.enemyChildLastSkillTime = currentTime;
                } else if (currentTime - InGameInfo.enemyLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.enemyLastSkillTime = currentTime;
                }
            }
        }
    }
}
